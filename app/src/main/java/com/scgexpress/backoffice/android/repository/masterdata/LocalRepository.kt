package com.scgexpress.backoffice.android.repository.masterdata

import android.util.Log
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.util.IOUtils
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.scgexpress.backoffice.android.api.MasterDataService
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.thenPass
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.model.MasterData
import com.scgexpress.backoffice.android.model.MasterDataVersion
import com.scgexpress.backoffice.android.model.ParserStateInfo
import com.scgexpress.backoffice.android.model.State
import com.scgexpress.backoffice.android.parser.masterdata.StreamObject
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import timber.log.Timber
import java.io.*
import java.lang.IllegalStateException
import java.util.zip.GZIPInputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LocalRepository @Inject
constructor(
        private val dao: MasterDataDao,
        private val service: MasterDataService,
        private val masterDataPreference: MasterDataPreference
) {

    private val TAG = "-MasterData"
    private val FETCH_JSON_ROWS = 100

    fun getVersion(): Single<MasterDataVersion> {
        return service.getVersion()
    }

    private fun clearAll(): Completable {
        return Completable.fromCallable {
            dao.clearTblAuthUsers()
            dao.clearTblManifestItems()
            dao.clearTblManifestOfdItems()
            dao.clearTblManifestOfdSheets()
            dao.clearTblManifestSheets()
            dao.clearTblManifestSheetsGeneral()
            dao.clearTblMasterCustomerType()
            dao.clearTblMasterDataSource()
            dao.clearTblMasterIrregularType()
            dao.clearTblMasterManifestType()
            dao.clearTblMasterOperationCode()
            dao.clearTblMasterParcelProperties()
            dao.clearTblMasterParcelSizing()
            dao.clearTblMasterParcelStatus()
            dao.clearTblMasterPaymentType()
            dao.clearTblMasterPromocode()
            dao.clearTblMasterRetentionReason()
            dao.clearTblMasterReturnReason()
            dao.clearTblMasterServiceTypeLevel1()
            dao.clearTblMasterServiceTypeLevel2()
            dao.clearTblMasterServiceTypeLevel3()
            dao.clearTblParcelStatus()
            dao.clearTblPrice()
            dao.clearTblReturnreruteSheets()
            dao.clearTblScgAssortCode()
            dao.clearTblScgBranch()
            dao.clearTblScgCodModel()
            dao.clearTblScgCodTier()
            dao.clearTblScgConsignment()
            dao.clearTblScgConsignmentDel()
            dao.clearTblScgNekoBilling()
            dao.clearTblScgNekoTracking()
            dao.clearTblScgParcelReturnrerute()
            dao.clearTblScgParcelTracking()
            dao.clearTblScgParcels()
            dao.clearTblScgPostalcode()
            dao.clearTblScgPriceTier()
            dao.clearTblScgPricingModel()
            dao.clearTblScgReceipt()
            dao.clearTblScgRegion()
            dao.clearTblScgRegionDiff()
            dao.clearTblMasterBillingCompany()
            dao.clearTblOrganization()
            dao.clearTblScgApiToken()
            dao.clearTblScgUnregisteredTracking()
            dao.clearTblTempCal()
            Completable.complete()
        }
    }

    private fun insertAll(masterData: MasterData) {
//        return Completable.fromAction {
//        Log.i("-masterdata", "insertAll::$masterData")
//            masterData.scgPricingModel?.let {
//                val x = dao.insertAllTblMasterManifestType(it)
//                Log.i("-masterdata", "insertAll:: x=${x.joinToString(",")}")
//            }

        val tasks = listOf(
                { masterData.authUsers?.let { dao.insertAllTblAuthUsers(it) } },
                { masterData.manifestItems?.let { dao.insertAllTblManifestItems(it) } },
                { masterData.manifestOfdItems?.let { dao.insertAllTblManifestOfdItems(it) } },
                { masterData.manifestOfdSheets?.let { dao.insertAllTblManifestOfdSheets(it) } },
                { masterData.manifestSheets?.let { dao.insertAllTblManifestSheets(it) } },
                { masterData.manifestSheetsGeneral?.let { dao.insertAllTblManifestSheetsGeneral(it) } },
                { masterData.masterCustomerType?.let { dao.insertAllTblMasterCustomerType(it) } },
                { masterData.masterDataSource?.let { dao.insertAllTblMasterDataSource(it) } },
                { masterData.masterIrregularType?.let { dao.insertAllTblMasterIrregularType(it) } },
                { masterData.masterManifestType?.let { dao.insertAllTblMasterManifestType(it) } },
                { masterData.masterOperationCode?.let { dao.insertAllTblMasterOperationCode(it) } },
                { masterData.masterParcelProperties?.let { dao.insertAllTblMasterParcelProperties(it) } },
                { masterData.masterParcelSizing?.let { dao.insertAllTblMasterParcelSizing(it) } },
                { masterData.masterParcelStatus?.let { dao.insertAllTblMasterParcelStatus(it) } },
                { masterData.masterPaymentType?.let { dao.insertAllTblMasterPaymentType(it) } },
                { masterData.masterPromocode?.let { dao.insertAllTblMasterPromocode(it) } },
                { masterData.masterRetentionReason?.let { dao.insertAllTblMasterRetentionReason(it) } },
                { masterData.masterReturnReason?.let { dao.insertAllTblMasterReturnReason(it) } },
                { masterData.masterServiceTypeLevel1?.let { dao.insertAllTblMasterServiceTypeLevel1(it) } },
                { masterData.masterServiceTypeLevel2?.let { dao.insertAllTblMasterServiceTypeLevel2(it) } },
                { masterData.masterServiceTypeLevel3?.let { dao.insertAllTblMasterServiceTypeLevel3(it) } },
                { masterData.parcelStatus?.let { dao.insertAllTblParcelStatus(it) } },
                { masterData.price?.let { dao.insertAllTblPrice(it) } },
                { masterData.returnreruteSheets?.let { dao.insertAllTblReturnreruteSheets(it) } },
                { masterData.scgAssortCode?.let { dao.insertAllTblScgAssortCode(it) } },
                { masterData.scgBranch?.let { dao.insertAllTblScgBranch(it) } },
                { masterData.scgCodModel?.let { dao.insertAllTblScgCodModel(it) } },
                { masterData.scgCodTier?.let { dao.insertAllTblScgCodTier(it) } },
                { masterData.scgConsignment?.let { dao.insertAllTblScgConsignment(it) } },
                { masterData.scgConsignmentDel?.let { dao.insertAllTblScgConsignmentDel(it) } },
                { masterData.scgNekoBilling?.let { dao.insertAllTblScgNekoBilling(it) } },
                { masterData.scgNekoTracking?.let { dao.insertAllTblScgNekoTracking(it) } },
                { masterData.scgParcelReturnrerute?.let { dao.insertAllTblScgParcelReturnrerute(it) } },
                { masterData.scgParcelTracking?.let { dao.insertAllTblScgParcelTracking(it) } },
                { masterData.scgParcels?.let { dao.insertAllTblScgParcels(it) } },
                { masterData.scgPostalcode?.let { dao.insertAllTblScgPostalcode(it) } },
                { masterData.scgPriceTier?.let { dao.insertAllTblScgPriceTier(it) } },
                { masterData.scgPricingModel?.let { dao.insertAllTblScgPricingModel(it) } },
                { masterData.scgReceipt?.let { dao.insertAllTblScgReceipt(it) } },
                { masterData.scgRegion?.let { dao.insertAllTblScgRegion(it) } },
                { masterData.scgRegionDiff?.let { dao.insertAllTblScgRegionDiff(it) } },
                { masterData.masterBillingCompany?.let { dao.insertAllTblMasterBillingCompany(it) } },
                { masterData.organization?.let { dao.insertAllTblOrganization(it) } },
                { masterData.scgApiToken?.let { dao.insertAllTblScgApiToken(it) } },
                { masterData.scgUnregisteredTracking?.let { dao.insertAllTblScgUnregisteredTracking(it) } },
                { masterData.tempCal?.let { dao.insertAllTblTempCal(it) } }
        )

        tasks.forEach { task ->
            //            Log.i("-masterdata", "in insertAll before task $task")
            task()
//            Log.i("-masterdata", "in insertAll after task $task")
        }
//        }
    }


    //////////

    fun loadMasterDataJsonFile(s3Client: AmazonS3Client, canonicalPath: String): Observable<ParserStateInfo> {

        val masterDataTempFileName = Const.MASTERDATA_TEMP_FILENAME
        val masterDataTempDir = "$canonicalPath/${Const.MASTERDATA_TEMP_DIR}"
        val masterDataTempFile = "$masterDataTempDir/$masterDataTempFileName"
        var s3Filename = ""

        Log.i("-masterdata", "masterDataTempFile = $masterDataTempFile")

        return Observable.create { emitter ->
            Single.just(ParserStateInfo(State.INIT))
                    .flatMap {
                        emitter.onNext(it)
                        emitter.onNext(ParserStateInfo(State.CHECK_VERSION))
                        getVersion()
                    }
                    .flatMap { version ->
                        emitter.onNext(ParserStateInfo(State.DOWNLOAD_FILE_FROM_S3))
                        s3Filename = version.filename!!

                        if(s3Filename == masterDataPreference.lastestVersionFilename){
                            Timber.d("master data s3Filename equals to prev version, no need to update")
                            emitter.onComplete()
                            throw IllegalStateException()
                        }

                        download(s3Client, Const.AWS_BUCKET_DATA, s3Filename)
                    }
                    .flatMap { s3Object ->
                        emitter.onNext(ParserStateInfo(State.CONVERT_OBJECT_TO_FILE))
                        convertS3ObjectToFile(s3Object, masterDataTempDir, masterDataTempFile)
                    }
                    .flatMap { fileDir ->
                        emitter.onNext(ParserStateInfo(State.TRUNCATE_DB))
                        clearAll() thenPass fileDir
                    }
                    .flatMap { fileDir ->
                        fetchMasterDataFile(fileDir, emitter) thenPass 0
                    }
                    .flatMap {
                        emitter.onNext(ParserStateInfo(State.UPDATE_VERSION))
                        updateMasterDataVersion(s3Filename) thenPass 0
                    }
                    .subscribe({
                        emitter.onComplete()
                    }, {
                        emitter.onError(it)
                    })
        }
    }

    fun _loadMasterDataFromLocalFile(inputStream: InputStream, canonicalPath: String): Observable<ParserStateInfo> {

        val masterDataTempFileName = Const.MASTERDATA_TEMP_FILENAME
        val masterDataTempDir = "$canonicalPath/${Const.MASTERDATA_TEMP_DIR}"
        val masterDataTempFile = "$masterDataTempDir/$masterDataTempFileName"

        return Observable.create { emitter ->
            Single.just(ParserStateInfo(State.INIT))
                    .flatMap {
                        emitter.onNext(it)
                        emitter.onNext(ParserStateInfo(State.CHECK_VERSION))
                        getVersion()
                    }
                    .flatMap {
                        _writeFile(inputStream, masterDataTempDir, masterDataTempFile) thenPass masterDataTempFile
                    }
                    .flatMap { fileDir ->
                        emitter.onNext(ParserStateInfo(State.TRUNCATE_DB))
                        clearAll() thenPass fileDir
                    }
                    .flatMap { fileDir ->
                        fetchMasterDataFile(fileDir, emitter) thenPass 0
                    }
                    .flatMap {
                        emitter.onNext(ParserStateInfo(State.UPDATE_VERSION))
                        updateMasterDataVersion("input-stream-m-data-1") thenPass 0
                    }
                    .subscribe({
                        emitter.onComplete()
                    }, {
                        emitter.onError(it)
                    })
        }
    }

    fun _writeFile(inputStream: InputStream, tempDir: String, tempFile: String): Completable {
        return Completable.fromAction {
            val buffer = inputStream.run {
                ByteArray(available()).also { read(it) }
            }

            File(tempDir).mkdirs()
            File(tempFile).createNewFile()

            val outStream = FileOutputStream(File(tempFile))
            outStream.write(buffer)
        }
    }

    private fun download(s3Client: AmazonS3Client, bucketName: String, s3MasterDataFilename: String): Single<S3Object> {
        return Single.fromCallable {
            s3Client.getObject(GetObjectRequest(bucketName, s3MasterDataFilename))
        }
    }

    private fun convertS3ObjectToFile(s3Object: S3Object, tempDir: String, tempFile: String): Single<String> {
        return Single.create { emitter ->
            val objectContent = s3Object.objectContent
            val input = GZIPInputStream(PushbackInputStream(objectContent))
            File(tempDir).mkdirs()
            File(tempFile).createNewFile()
            val output = FileOutputStream(tempFile)
            IOUtils.copy(input, output)
            output.close()
            input.close()
            objectContent.close()
            emitter.onSuccess(tempFile)
        }
    }

    private fun fetchMasterDataFile(
            fileDir: String,
            observableEmitter: ObservableEmitter<ParserStateInfo>? = null
    ): Completable {
        return Completable.fromAction {
            val file = File(fileDir)
            val fileSize = file.length()
            val reader = object : BufferedReader(FileReader(file)) {
                var count = 0
                override fun read(cbuf: CharArray?, off: Int, len: Int): Int {
                    count++
                    val ratio = (count * len).toDouble() / fileSize * 100
//                    Log.i("-masterdata", "cbuf = ${(count * len).toDouble()}/$fileSize = $ratio")
//                    Log.v("-masterdata", "cbuf = ```${cbuf!!.joinToString("")}```")
                    observableEmitter?.onNext(ParserStateInfo(State.READ_FILE, ratio))
                    return super.read(cbuf, off, len)
                }
            }

            //val totalTable = MasterData.getTableCount()
            val gson = GsonBuilder().create()
            val rows = FETCH_JSON_ROWS + Random.nextInt(0, 10)
            val streamParser = StreamObject(JsonReader(reader)).apply {
                access()
            }

            while (streamParser.hasNext()) {
                var count = .0
                val arr = streamParser.nextStreamArray().apply {
                    access()
                }
                while (arr.hasNext()) {
                    arr.nextPartialObject(rows)?.let { table ->
                        count += rows
                        //Log.i("-masterdata", "TTTTT $table")
                        //Log.i("-masterdata", "to do table = ($count) ${table.parentKey}")
                        val partialMasterData = gson.fromJson(table.json, MasterData::class.java)
                        insertAll(partialMasterData)
                        //Log.i("-masterdata", "putDB = ($count) $table")
                        observableEmitter?.onNext(
                                ParserStateInfo(
                                        State.INSERT_DB,
                                        process = count,
                                        tableName = table.parentKey
                                )
                        )
                    }
                }
                arr.exit()
            }
        }
    }

    private fun updateMasterDataVersion(s3MasterDataFilename: String): Completable {
        return Completable.fromAction {
            masterDataPreference.lastestVersion = Utils.getCurrentTimestamp()
            masterDataPreference.noticeTime = 0
            masterDataPreference.lastestVersionFilename = s3MasterDataFilename
        }
    }

}