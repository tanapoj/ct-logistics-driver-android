package com.scgexpress.backoffice.android.ui.masterdata

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.util.IOUtils
import com.google.gson.GsonBuilder
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.MasterData
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.repository.masterdata.MasterDataRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.*
import java.util.zip.GZIPInputStream
import javax.inject.Inject

class MasterDataViewModel @Inject constructor(
    application: Application,
    private val repo: MasterDataRepository,
    private val masterDataPreference: MasterDataPreference
) : RxAndroidViewModel(application) {

    var canonicalPath = ""
    var s3MasterDataFilename = ""

    private val masterDataJsonFileLocation = Const.MASTERDATA_S3_LOCATION
    private val masterDataTempFileName = Const.MASTERDATA_TEMP_FILENAME
    private val masterDataTempDir: String
        get() {
            return "$canonicalPath/${Const.MASTERDATA_TEMP_DIR}"
        }
    private val masterDataTempFile: String
        get() {
            return "$masterDataTempDir/$masterDataTempFileName"
        }

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _done = MutableLiveData<Boolean>()
    val done: LiveData<Boolean>
        get() = _done

    private val _progressValue = MutableLiveData<Int>()
    val progressValue: LiveData<Int>
        get() = _progressValue


    fun loadMasterDataJsonFile(s3Client: AmazonS3Client) {

        Timber.i("-masterdata MasterData ViewModel:: lastver=${masterDataPreference.lastestVersion} noticetime=${masterDataPreference.noticeTime}")
        Timber.i("-masterdata MasterData ViewModel:: hasNotice()=${masterDataPreference.hasNotice()} isExpire()=${masterDataPreference.isExpire()}")
        if (!masterDataPreference.isExpire()) {
            _done.value = true
            return
        }

        _done.value = false
        _status.value = "download masterdata json"
        _progressValue.value = -1

        repo.getVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ version ->

                Timber.i("s3 filename = ${version.filename}")
                s3MasterDataFilename = version.filename!!
                val bucketName = Const.AWS_BUCKET_DATA

                if (s3MasterDataFilename == masterDataPreference.lastestVersionFilename) {
                    _done.value = true
                    return@subscribe
                }

                Flowable.fromCallable { s3Client.getObject(GetObjectRequest(bucketName, s3MasterDataFilename)) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val objectContent = it.objectContent
                        _status.value = "create temp json file"
                        convertToFile(objectContent)
                    }
                    .also {
                        addDisposable(it)
                    }

            }, {
                throw it
            })
            .also {
                addDisposable(it)
            }

//            var fileName = masterDataJsonFileLocation
//            Observable.create(ObservableOnSubscribe<S3Object> { emitter ->
//                emitter.onNext(s3Client.getObject(GetObjectRequest(Const.AWS_BUCKET, fileName)))
//            })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe {
//                        try {
//                            val objectContent = it.objectContent
//                            _status.value = "create temp json file"
//                            convertToFile(objectContent)
//                        } catch (e: Exception) {
//                            Timber.e(e)
//                        }
//                    }
//                    .also {
//                        addDisposable(it)
//                    }
    }

    private fun convertToFile(objectContent: S3ObjectInputStream) {

        _status.value = "convert json data"
        _progressValue.value = -1

        Completable.fromCallable {
            try {
                val input = GZIPInputStream(PushbackInputStream(objectContent))
                File(masterDataTempDir).mkdirs()
                File(masterDataTempFile).createNewFile()
                val output = FileOutputStream(masterDataTempFile)
                IOUtils.copy(input, output)
                output.close()
                input.close()
                objectContent.close()

            } catch (e: Exception) {
                Timber.e(e)
            }
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _status.value = "create temp json file"
                readMasterData()
            }
            .also {
                addDisposable(it)
            }
    }

    private fun readMasterData() {

        _status.value = "parsing json to object"

        Observable.create(ObservableOnSubscribe<Int> { emitter ->
            val file = File(masterDataTempFile)
            val fileSize = file.length()
            val reader = object : BufferedReader(FileReader(file)) {

                var count = 0

                override fun read(cbuf: CharArray?, off: Int, len: Int): Int {
                    count++
                    val percent = (count * len).toDouble() / fileSize * 100
                    emitter.onNext(percent.toInt())
                    return super.read(cbuf, off, len)
                }
            }
            val o = GsonBuilder().create().fromJson(reader, MasterData::class.java)
            updateDatabase(o)
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _status.value = "parsing json to object $it%"
                _progressValue.value = it
            }, {
                it.printStackTrace()
            }, {})
            .also {
                addDisposable(it)
            }
    }

    private fun updateDatabase(masterData: MasterData) {
        Timber.i("clear masterdata database")
        repo.clearAll().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Timber.i("start update masterdata to database")
            insertNewMasterData(masterData)
        }.also { addDisposable(it) }
    }

    private fun insertNewMasterData(masterData: MasterData) {
        repo.insertAll(masterData)!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _status.value = "updating new data to database $it%"
                _progressValue.value = it
                updateMasterDataVersion()
            }, {}, {
                _status.value = "update masterdata done!"
                _done.value = true
            }).also {
                addDisposable(it)
            }
    }

    private fun updateMasterDataVersion() {
        masterDataPreference.lastestVersion = Utils.getCurrentTimestamp()
        masterDataPreference.noticeTime = 0
        masterDataPreference.lastestVersionFilename = s3MasterDataFilename
    }

    //    fun uploadMasterDataJsonFile(transferUtility: TransferUtility) {
//        val fileName = "masterdata/mdata1.gz"
//
//        val file = File("/data/data/com.centrillion.backoffice.logistic/files/masterdata/mdata1.gz")
//
//        val uploadObserver = transferUtility.upload(fileName, file)
//
//        uploadObserver.setTransferListener(object : TransferListener {
//
//            override fun onStateChanged(id: Int, state: TransferState) {
//                if (TransferState.COMPLETED == state) {
//                    Log.i(TAG, "Upload Completed!")
//                    //file.delete()
//                } else if (TransferState.FAILED == state) {
//                    Log.i(TAG, "Upload Fail!")
//                    //file.delete()
//                }
//            }
//
//            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
//                val percentDone = percentDonef.toInt()
//
//                Log.i(TAG, String.format("%3.1f / %3.1f", bytesCurrent.toFloat(), bytesTotal.toFloat()))
//            }
//
//            override fun onError(id: Int, ex: Exception) {
//                ex.printStackTrace()
//            }
//
//        })
//    }
//

    fun testCodFee() {

//        repo.getVersion()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    Log.i(TAG, "version: $it")
//                }
//                .also {
//                    addDisposable(it)
//                }
//
//        repo.codFee(1000.0, "10001000")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.i(TAG, "test cod fee: " + it)
//                }, {
//                    Log.i(TAG, "test cod fee: NO DATA!")
//                })
//                .also {
//                    addDisposable(it)
//                }
//
//        repo.deliveryFee("999998", 107, "41240", "110207625381", "3", "1")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.i(TAG, "test scg pricing model: " + it)
//                }, {
//                    Log.i(TAG, "test scg pricing model: NO DATA!")
//                })
//                .also {
//                    addDisposable(it)
//                }

    }

}