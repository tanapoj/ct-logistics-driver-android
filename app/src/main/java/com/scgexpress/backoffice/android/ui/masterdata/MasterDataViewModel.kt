package com.scgexpress.backoffice.android.ui.masterdata

import android.app.Application
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.services.s3.AmazonS3Client
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.numberFormat
import com.scgexpress.backoffice.android.model.ParserStateInfo
import com.scgexpress.backoffice.android.model.State
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.repository.masterdata.LocalRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class MasterDataViewModel @Inject constructor(
        application: Application,
        private val repo: LocalRepository,
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

    private val _statusDetail = MutableLiveData<String>()
    val statusDetail: LiveData<String>
        get() = _statusDetail

    private val _done = MutableLiveData<Boolean>()
    val done: LiveData<Boolean>
        get() = _done

    private val _progressValue = MutableLiveData<Int>()
    val progressValue: LiveData<Int>
        get() = _progressValue

    private val _dialogMessage: MutableLiveData<String> = MutableLiveData()
    val dialogErrorMessage: LiveData<String>
        get() = _dialogMessage


    fun loadMasterDataJsonFile(s3Client: AmazonS3Client, canonicalPath: String) {

        Timber.i("-masterdata MasterData ViewModel:: lastver=${masterDataPreference.lastestVersion} noticetime=${masterDataPreference.noticeTime}")
        Timber.i("-masterdata MasterData ViewModel:: hasNotice()=${masterDataPreference.hasNotice()} isExpire()=${masterDataPreference.isExpire()}")

        if (!masterDataPreference.isExpire()) {
            _done.value = true
            return
        }

        _done.value = false
        _progressValue.value = -1

        repo.loadMasterDataJsonFile(s3Client, canonicalPath)
//        repo._loadMasterDataFromLocalFile(getApplication<Application>().resources.openRawResource(R.raw.mdata_v1_20190805), canonicalPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ info ->
                    //Log.v("-masterdat", "=== observe: $info")
                    val (msg, detail, percent) = getStateMessage(info)
                    detail?.let { _statusDetail.value = it }
                    msg?.let { _status.value = it }
                    percent?.let { _progressValue.value = it }
                }, {
                    //Log.i("-masterdata", "=== error: $it")
                    it.printStackTrace()
                    _dialogMessage.value = "${it.message}"
                }, {
                    //Log.i("-masterdata", "=== complete!")
                    _done.value = true
                })
                .also {
                    addDisposable(it)
                }
    }

    private fun getStateMessage(state: ParserStateInfo) = when (state.state) {
        State.INIT -> Triple(getString(R.string.master_data_update_state_init), null, -1)
        State.CHECK_VERSION -> Triple(getString(R.string.master_data_update_state_check_version), null, -1)
        State.DOWNLOAD_FILE_FROM_S3 -> Triple(getString(R.string.master_data_update_state_download_file), null, -1)
        State.CONVERT_OBJECT_TO_FILE -> Triple(getString(R.string.master_data_update_state_convert_object), null, -1)
        State.TRUNCATE_DB -> Triple(getString(R.string.master_data_update_state_truncate_db), null, -1)
        State.READ_FILE -> Triple(
                getString(R.string.master_data_update_state_read_file, state.process?.toInt().toString()),
                null,
                state.process?.toInt()
        )
        State.INSERT_DB -> Triple(
                null,
                getString(
                        R.string.master_data_update_state_insert_db,
                        state.tableName.toString(),
                        state.process?.toInt()?.numberFormat() ?: "0"
                ),
                null
        )
        State.UPDATE_VERSION -> Triple(
                getString(R.string.master_data_update_state_update_version),
                "update master data version number",
                -1
        )
    }

    private fun getString(resId: Int, vararg values: String) = getApplication<Application>().resources.getString(resId, *values)
//            : String {
//        var str = getApplication<Application>().resources.getString(resId, *values)
//        var i = 1
//        for (value in values) {
//            str = str.replace("\$$i", value.toString())
//            i++
//        }
//        return str
//    }

}