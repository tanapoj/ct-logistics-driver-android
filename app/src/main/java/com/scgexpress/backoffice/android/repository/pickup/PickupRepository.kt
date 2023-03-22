package com.scgexpress.backoffice.android.repository.pickup

import android.content.Context
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.PickupService
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.dao.PickupDao
import com.scgexpress.backoffice.android.db.entity.masterdata.TblMasterParcelSizing
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgAssortCode
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgPostalcode
import com.scgexpress.backoffice.android.db.entity.pickup.PickupPendingReceiptEntity
import com.scgexpress.backoffice.android.db.entity.pickup.PickupTaskEntity
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.pickup.*
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking
import com.scgexpress.backoffice.android.model.tracking.Tracking
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

typealias IdAndValue = Pair<Int, String>

@Singleton
class PickupRepository @Inject
constructor(
    private val scheduler: RxThreadScheduler,
    private val pickupDao: PickupDao,
    private val masterDao: MasterDataDao,
    private val pickupService: PickupService,
    private val pickupPreference: PickupPreference,
    private val offlineRepo: OfflineRepository,
    private val masterDataRepo: DataRepository,
    private val masterCalculatorRepo: CalculatorRepository
) {
    private val TAG = "-pickup_task_repo"

    enum class ReceiptType {
        Normal, Cod, Carton
    }

    enum class ServiceType {
        Normal, Chilled, Frozen
    }

    companion object {
        public const val GENERAL_CUSTOMER = "999997"
        public const val C2_CUSTOMER = "999999"
        public const val BUSINESS_CUSTOMER_TYPE = 2
    }


    val parcelSizingItems: Flowable<List<TblMasterParcelSizing>>
        get() {
            return masterDao.parcelSizingItems.scheduleBy(scheduler)
        }

    private fun generateTemporaryTaskId() =
        Utils.getCurrentTimestamp().run { md5(this) }.substring(0, 10)

    fun createTemporaryTask(customerCode: String): Single<String> {
        return masterDataRepo.getOrganization(customerCode).flatMap { org ->
            Single.fromCallable {
                val id = generateTemporaryTaskId()
                val task = PickupTaskEntity(
                    id = id,
                    isNewGenerateTask = true,
                    customerCode = customerCode,
                    customerName = org.name ?: "",
                    senderCode = customerCode,
                    senderName = org.name ?: "",
                    bookingCode = ""
                )
                pickupDao.insertTasks(listOf(task))
                id
            }.scheduleBy(scheduler)
        }
    }

    @Deprecated("SCGEX change receipt-code format")
    fun generateReceiptCode(
        type: ReceiptType,
        customerCode: String,
        personalId: String,
        runningNumber: Int,
        datetime: String
    ): String {
        val typeCode = when (type) {
            ReceiptType.Normal -> "SDF"
            ReceiptType.Cod -> "SCA"
            ReceiptType.Carton -> "BXC"
        }
        val number = runningNumber.toString().padStart(3, '0')
        return "P-$typeCode-$customerCode-$personalId-$number-$datetime"
    }

    fun generateReceiptCode(
        type: ReceiptType,
        userId: String,
        datetime: String,
        runningNumber: Int
    ): String {
        val typeCode = when (type) {
            ReceiptType.Normal -> "SDF"
            ReceiptType.Cod -> "SCA"
            ReceiptType.Carton -> "BXC"
        }
        val n = runningNumber.toString().padStart(3, '0')
        return "P-$typeCode-$userId-$datetime-$n"
    }

    fun initFetchTask(): Single<List<PickupTask>> {
        return loadTask().scheduleBy(scheduler)
    }

    /**
     * load pickup-task from api, then clear db, then save data to db
     */
    private fun loadTask(): Single<List<PickupTask>> {
        return pickupService.getPickupTasks()
            .toSingle()
            .map { it.items }
//            .flatMap { api ->
//                Timber.d("loadTask: clear task in db")
//                offlineRepo.clearTaskData() thenPass api
//            }
            .flatMap { tasks ->
                Timber.d("loadTask: get pickup from API $tasks")
                save(tasks) thenPass tasks
            }
    }

    /**
     * save data to db (without clear db)
     */
    private fun save(tasks: List<PickupTask>): Completable {
        return Completable.create { emitter ->

            val todo = mutableListOf<Completable>()
            offlineRepo.saveTask(tasks).also { todo.add(it) }

            tasks.forEach { task ->
                task.trackingInfo?.let { data ->
                    offlineRepo.saveTracking(task.id, data.trackings).also { todo.add(it) }
                    offlineRepo.saveReceipt(task.id, data.receipts).also { todo.add(it) }
                    data.receipts.forEach { receipt ->
                        offlineRepo.saveSubmitTracking(
                            task.id,
                            receipt.receiptCode,
                            receipt.submitTrackings
                        )
                            .also { todo.add(it) }
                    }
                }
            }
            Timber.d("save api $tasks to db")

            Completable.concat(todo).thenEmit(emitter)
        }.scheduleBy(scheduler)
    }

    fun clearSubmitTracking(taskId: String) = pickupDao.deleteSubmitTrackingByTaskId(taskId)

    fun saveSubmitTracking(
        taskId: String,
        receiptCode: String,
        submitTrackings: List<SubmitTracking>,
        createdAt: Long = 0
    ): Completable {
        return Completable.create { emitter ->
            val todo = mutableListOf<Completable>()
            offlineRepo.saveSubmitTracking(taskId, receiptCode, submitTrackings, createdAt)
                .also { todo.add(it) }
            Completable.concat(todo).thenEmit(emitter)
        }.scheduleBy(scheduler)
    }

    /**
     * fetch task from API (after fetch, write data to db too), on error fetch from db
     */
    fun getAllTask(): Flowable<List<PickupTask>> {
        return loadTask().toFlowable()
            .onErrorResumeNext { e: Throwable ->
                e.printStackTrace()
                offlineRepo.getAllTask().scheduleBy(scheduler)
            }.scheduleBy(scheduler)
    }

    fun getTaskById(taskID: String): Single<PickupTask> {
        return Single.create { emitter ->
            pickupService.getPickupTask(taskID).scheduleBy(scheduler).onErrorResumeNext {
                Timber.e(it)
                Timber.d("get task: $taskID from API = FAIL --> load from offline")
                offlineRepo.getTask(taskID).scheduleBy(scheduler)
            }.subscribe({
                if (it.id.isBlank()) {
                    Timber.d("get task: $taskID from API = TYPE ERROR, id missing --> load from offline")
                    offlineRepo.getTask(taskID).thenEmit(emitter, scheduler)
                } else {
                    emitter.onSuccess(it)
                }
            }, emitter::onError)
        }
    }

    fun attachOfflineDataToTask(task: PickupTask): Single<PickupTask> {
        val trackingList = offlineRepo.getTrackingsOf(task.id)
        val receiptList = offlineRepo.getReceiptsOf(task.id)
        val submitTracking = offlineRepo.getSubmitTrackingsOf(task.id).toSingle()
        return Single.zip(
            trackingList,
            receiptList,
            submitTracking,
            Function3 { trackingList: List<Tracking>, receiptList: List<Receipt>, submitTracking: List<SubmitTracking> ->
                task.apply {
                    val trackingMap = submitTracking.groupBy { it.receiptCode }
                    Timber.d("attachOfflineDataToTask: trackingMap=$trackingMap")
                    for (receipt in receiptList) {
                        receipt.submitTrackings = trackingMap[receipt.receiptCode] ?: listOf()
                    }
                    trackingInfo = TaskTrackingInfo(trackingList, receiptList)
                }
            })
    }

    //TODO Pickup task : Fix this mockup
    fun getTaskCompleted(taskID: String): Single<PickupTask> {
        return pickupService.getPickupTaskCompleted(taskID).onErrorResumeNext {
            offlineRepo.getTask(taskID)
        }
    }

    fun getTask(codeOrTel: String): Flowable<List<PickupTask>> {
        return offlineRepo.searchTask(codeOrTel)
    }

    fun getTask(taskIds: List<String>): Flowable<List<PickupTask>> {
        return offlineRepo.getTaskByIds(taskIds)
    }

    fun getTracking(trackingId: String): Single<Tracking> {
        return pickupService.getTracking(trackingId).onErrorResumeNext {
            offlineRepo.getTracking(trackingId)
        }
    }

    fun getTrackingsOf(taskId: String): Single<List<Tracking>> {
        return pickupService.getPickupTask(taskId)
            .map {
                it.trackingInfo?.trackings ?: listOf()
            }.onErrorResumeNext {
                Timber.e(it)
                offlineRepo.getTrackingsOf(taskId)
            }.scheduleBy(scheduler)
    }

    fun getSubmitTracking(): Flowable<List<SubmitTracking>> {
        return offlineRepo.getSubmitTracking()
    }

    fun getSubmitTracking(trackingId: String): Single<SubmitTracking> {
        return offlineRepo.getSubmitTracking(trackingId)
    }

    fun getSubmitTrackingsOf(taskId: String): Flowable<List<SubmitTracking>> {
        return offlineRepo.getSubmitTrackingsOf(taskId)
    }

    fun getReceipt(taskId: String): Single<Receipt> {
        return offlineRepo.getReceipt(taskId)
    }

    fun hasPendingTask(): Single<Boolean> {
        return Single.fromCallable {

            //            pickupDao.insertPendingReceipt(listOf(
//                    PickupPendingReceiptEntity(
//                            "MB-12345",
//                            "123",
//                            false,
//                            1,
//                            1,
//                            .0,
//                            .0,
//                            .0,
//                            .0,
//                            1,
//                            "089-123-4567",
//                            "test@test.com",
//                            "2008-10-17T00:00:00+08:00",
//                            Location(latitude = "13.34346254", longitude = "100.34223"),
//                            "1")))

            pickupDao.countPendingReceipt() > 0
        }.scheduleBy(scheduler)
    }

    fun syncAll(): Observable<Pair<Int, Int>> {
        return Observable.create<Pair<Int, Int>> { emitter ->
            pickupDao.getPendingReceipt(false).subscribe({ list ->
                var done = 0
                val total = list.size

                val submit = list.map {
                    getSubmitReceipt(it).flatMap { receipt ->
                        pickupService.submitReceipt(receipt.taskId, receipt).flatMap { res ->
                            check(res.isSuccess()) { "api response with $res" }

                            Single.fromCallable {
                                pickupDao.updatePendingReceiptToDone(
                                    receipt.receiptCode,
                                    res.receiptId
                                )
                            }.scheduleBy(scheduler)
                        }.flatMap { id ->
                            done++
                            emitter.onNext(done to total)
                            Single.just(id)//.delay(1, TimeUnit.SECONDS)
                        }
                    }
                }

                Single.concat(submit).subscribe({
                    emitter.onComplete()
                }, emitter::onError)
            }, emitter::onError)
        }.scheduleBy(scheduler)
    }

    fun acceptTask(id: String, body: PickupTaskAcception): Single<PickupTaskAcceptionResponse> {
        return pickupService.taskAcception(id, body)
            .flatMap {
                check(it.status == "ACCEPT" || it.status == "REJECT") { "task not ACCEPT or REJECT" }
                if (it.status == "ACCEPT") pickupDao.updateTaskStatus("${it.id}", "IN_PROGRESS")
                Single.just(it)
            }
    }

    fun resend(body: ResendReceipt): Single<ResendReceiptResponse> {
        return pickupService.resendReceipt(body.receiptId!!, body)
    }

    @Deprecated("call DataRepository::getServiceTypeWithSizing")
    fun getAllServiceType(context: ContextWrapper): Single<List<IdAndValue>> {
        val cod = context.getStringArray(R.array.service_type_name_cod)
        val nonCod = context.getStringArray(R.array.service_type_name_non_cod)
        val data = (cod + nonCod).map {
            val (id, name) = it.split(",")
            id.toInt() to name
        }
        return Single.just(data)
    }

    fun getServiceType(context: ContextWrapper, isCod: Boolean): Single<List<IdAndValue>> {
        val data = context.getStringArray(
            when (isCod) {
                true -> R.array.service_type_name_cod
                false -> R.array.service_type_name_non_cod
            }
        ).map {
            val (id, name) = it.split(",")
            id.toInt() to name
        }
        return Single.just(data)
    }

    @Deprecated("call DataRepository::getServiceTypeWithSizing")
    fun getAllParcelSize(context: ContextWrapper): Single<List<IdAndValue>> {
        return Single.create { emitter ->
            masterDao.parcelSizingItems
                .scheduleBy(scheduler)
                .subscribe({ list ->
                    list.map {
                        it.id to it.name!!
                    }.also {
                        emitter.onSuccess(it)
                    }
                }, emitter::onError, {})
        }
    }

    @Deprecated("call DataRepository::getServiceTypeWithSizing")
    fun getParcelSize(context: ContextWrapper, service: ServiceType): Single<List<IdAndValue>> {
        return Single.create { emitter ->
            val filterOnlyId = when (service) {
                ServiceType.Normal -> null
                ServiceType.Chilled -> context.getIntArray(R.array.parcel_sizing_chilled_display_id)
                ServiceType.Frozen -> context.getIntArray(R.array.parcel_sizing_frozen_display_id)
            }

            masterDao.parcelSizingItems
                .scheduleBy(scheduler)
                .subscribe({ list ->
                    list.filter {
                        (filterOnlyId == null || it.id in filterOnlyId) && it.name != "Undefined"
                    }.map {
                        it.id to it.name!!
                    }.also {
                        emitter.onSuccess(it)
                    }
                }, emitter::onError, {})
        }
    }

    fun calculateFreightFare(
        branchId: Int,
        serviceTypeId: String,
        sizingId: String,
        zipCode: String,
        selectedPostalId: Int,
        parcelCount: Int,
        customerCode: String
    ): Single<Double> {

        var baseFee = .0

        Timber.d(
            "REPO calculateFreightFare 1: branchId=$branchId, serviceTypeId=$serviceTypeId, sizingId=$sizingId, zipCode=$zipCode, selectedPostalId=$selectedPostalId, parcelCount=$parcelCount"
        )

//                val realCustomerCode =
//                    if (isC2Customer(customerCode)) getRealC2CustomerCode(customerCode, parcelCount)
//                    else customerCode

        return masterCalculatorRepo.deliveryFee(
            customerCode,
            branchId,
            zipCode,
            sizingId,
            serviceTypeId
        ).scheduleBy(scheduler).flatMap {
            //get base-fee from pricing model
            baseFee = it.freightFare?.toDoubleOrNull() ?: .0
            //query is in special area
            masterDataRepo.getScgPostalCode(selectedPostalId).scheduleBy(scheduler)
        }.flatMap { postal ->
            //extra charge for special area
            val extraFee = if (postal.isRemoteArea() && postal.hasExtraCharge()) {
                postal.extraCharge?.toDouble() ?: .0
            } else {
                .0
            }
            Single.just(baseFee + extraFee)
        }.flatMap { fee ->
            //extra charge if C2 Customer
            calculateFreightFareExtraForC2Customer(fee, parcelCount, customerCode)
        }.onErrorReturn {
            Timber.e("DELIVERY CALCULATOR CANNOT FOUND RESULT ROW!!! with message: ${it.message}\ncustomerCode=$customerCode branchId=$branchId zipCode=$zipCode sizingId=$sizingId serviceTypeId=$serviceTypeId")
            -1.0
        }
    }

    private val PARCEL_COUNT_LOWER_BOUND = 10

    private fun calculateFreightFareExtraForC2Customer(
        baseFee: Double,
        parcelCount: Int,
        customerCode: String
    ): Single<Double> {
        var fee = baseFee
//        if (customerCode == C2_CUSTOMER && parcelCount < 3) {
//            fee += 20.0
//        }
//        if (customerCode == C2_CUSTOMER && parcelCount < PARCEL_COUNT_LOWER_BOUND) {
//            fee += 60.0
//        }
        return Single.just(fee)
    }

//    private fun isC2Customer(customerCode: String) = customerCode == "999999"
//
//    private fun getRealC2CustomerCode(customerCode: String, parcelCount: Int) = when {
//        isC2Customer(customerCode) -> {
//            if (parcelCount < PARCEL_COUNT_LOWER_BOUND) "999997"
//            else "999998"
//        }
//        else -> customerCode
//    }

    fun getPostalInformation(zipCode: String): Single<Pair<List<TblScgPostalcode>, TblScgAssortCode>> {
        val remoteArea = masterDataRepo.getScgPostalCodes(zipCode)
        val addr = masterDataRepo.getAssortCode(zipCode)

        return Single.zip(
            remoteArea,
            addr,
            BiFunction { remoteArea: List<TblScgPostalcode>, addr: TblScgAssortCode ->
                Pair(remoteArea, addr)
            }).scheduleBy(scheduler)
    }


    fun calculateCartonPrice(serviceTypeId: String, sizingId: String): Single<Pair<Int, Double>> {

        if (serviceTypeId.isBlank() || sizingId.isBlank()) {
            return Single.error(IllegalArgumentException())
        }

        return masterDataRepo.getMasterParcelProperties(serviceTypeId, sizingId)
            .scheduleBy(scheduler)
            .map {
                it.id to (it.parcelSizingPrice ?: -1.0)
            }
    }

    fun calculateCodFee(codAmount: Double, customerCode: String) =
        masterCalculatorRepo.codFee(codAmount, customerCode).scheduleBy(scheduler)

    fun deleteAllPendingReceipt() = offlineRepo.deleteAllPendingReceipt()

    fun deleteSubmitTracking(
        taskId: String,
        trackingId: String
    ): Completable {
        return Completable.create { emitter ->
            val todo = mutableListOf<Completable>()
            offlineRepo.deleteTracking(taskId, trackingId).also { todo.add(it) }
            Completable.concat(todo).thenEmit(emitter)
        }.scheduleBy(scheduler)
    }

    //Scanning Track Flow

    fun getScanningTracking() = pickupDao.getScanningTracking().scheduleBy(scheduler)

    fun addScanningTracking(tracking: PickupScanningTrackingEntity): Completable {
        return Completable.create { emitter ->
            try {
                pickupDao.insertScanningTracking(tracking)
                Timber.d("REPO.addScanningTracking $tracking")
                emitter.onComplete()
            } catch (e: Exception) {
                Timber.e(e)
                emitter.onError(e)
            }
        }.scheduleBy(scheduler)
    }

    fun addScanningTracking(trackings: List<PickupScanningTrackingEntity>): Completable {
        return Completable.create { emitter ->
            try {
                pickupDao.insertScanningTracking(trackings)
                Timber.d("REPO.addScanningTracking")
                emitter.onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(e)
            }
        }.scheduleBy(scheduler)
    }

    fun deleteScanningTracking(trackingCode: String): Completable {
        return Completable.create { emitter ->
            pickupDao.deleteScanningTracking(trackingCode)
            emitter.onComplete()
        }.scheduleBy(scheduler)
    }

    fun deleteAllScanningTracking(): Completable {
        return Completable.create { emitter ->
            pickupDao.truncateScanningTracking()
            emitter.onComplete()
        }.scheduleBy(scheduler)
    }

    fun splitScanningTrackingByType(scanningTracking: List<PickupScanningTrackingEntity>): Map<ReceiptType, List<PickupScanningTrackingEntity>> {

        fun src() =
            mutableListOf<PickupScanningTrackingEntity>().apply { addAll(scanningTracking.map { it.copy() }) }

        val all = ReceiptType.Normal to src().map {
            it.apply {
                codFee = null
                cartonFee = null
            }
        }.toList()
        val cod = ReceiptType.Cod to src().filter { it.codFee != null && it.codFee!! > .0 }.map {
            it.apply {
                deliveryFee = .0
                cartonFee = null
            }
        }.toList()
        val carton =
            ReceiptType.Carton to src().filter { it.cartonFee != null && it.cartonFee!! > .0 }.map {
                it.apply {
                    deliveryFee = .0
                    codFee = null
                }
            }.toList()

        val splitList = mapOf(all, cod, carton).filter { (_, list) ->
            list.isNotEmpty()
        }

        Timber.d("splitScanningTrackingByType all = ${all.second.size} $all")
        Timber.d("splitScanningTrackingByType cod = ${cod.second.size} $cod")
        Timber.d("splitScanningTrackingByType carton = ${carton.second.size} $carton")
        Timber.d("splitScanningTrackingByType final list = ${splitList.size} $splitList")

        return splitList
    }

    fun submitScanningTracking(
        taskId: String,
        userId: String,
        customerCode: String,
        paymentMethodId: Int,
        contactTel: String,
        contactEmail: String,
        latLong: Pair<Double?, Double?>,
        scanningTracking: List<PickupScanningTrackingEntity>
    ): Single<List<SubmitReceiptResponse>> {
        val splitList = splitScanningTrackingByType(scanningTracking)
        val receiptCodes = splitList.map { (type, _) ->
            val datetime = Utils.getCurrentTimestamp().toString().substring(0, 10)
            val pickupReceiptRunningNumber = pickupPreference.nextRunningNumber()
            type to generateReceiptCode(
                type,
                userId,
                datetime,
                pickupReceiptRunningNumber
            )
        }.toMap()

        Timber.d("REPO.submitScanningTracking = receipt=$splitList, receiptCodes=$receiptCodes, taskId=$taskId, payment=$paymentMethodId, tel=$contactTel, email=$contactEmail, $scanningTracking")

        val submitter = splitList.toSortedMap().map { (type, list) ->
            val response = submitScanningTracking(
                type,
                receiptCodes,
                taskId,
                customerCode,
                list.size,
                paymentMethodId,
                contactTel,
                contactEmail,
                latLong,
                list
            )
            response
        }

        return Single.concat(submitter)
            .onErrorResumeNext(Flowable.empty())
            .toList()
            .flatMap {
                Timber.d("TEST. call --> deleteAllScanningTracking")
                deleteAllScanningTracking() thenPass it
            }
            .flatMap {
                Timber.d("TEST. call --> moveTrackingToSubmitTracking")
                offlineRepo.moveTrackingToSubmitTracking(scanningTracking) thenPass it
            }
    }

    private fun submitScanningTracking(
        receiptType: ReceiptType,
        receiptCodes: Map<ReceiptType, String>,
        taskId: String,
        customerCode: String,
        totalCountTracking: Int,
        paymentMethodId: Int,
        contactTel: String,
        contactEmail: String,
        latLong: Pair<Double?, Double?>,
        scanningTracking: List<PickupScanningTrackingEntity>
    ): Single<SubmitReceiptResponse> {

        val receiptCode =
            receiptCodes[receiptType] ?: error("$receiptType not found from $receiptCodes")
        val receiptCodeNormal = receiptCodes[ReceiptType.Normal]
            ?: error("ReceiptType.Normal not found from $receiptCodes")

        Timber.d("receiptType:$receiptType - receiptCode=$receiptCode receiptCodeNormal=$receiptCodeNormal list-size=${scanningTracking.size}")

        check(scanningTracking.isNotEmpty()) {
            Timber.e("scanning tracking cannot be empty scanning task: $scanningTracking")
            "scanning tracking cannot be empty"
        }

        return offlineRepo.createPendingReceipt(
            taskId,
            receiptCode,
            receiptCodeNormal,
            customerCode,
            totalCountTracking,
            paymentMethodId,
            contactTel,
            contactEmail,
            latLong,
            scanningTracking
        )
            .thenSingle {
                offlineRepo.createReceipt(taskId, receiptCode) thenPass 0
            }
            .flatMap {
                Timber.d("TEST. call --> saveScanningTrackingToSubmitTracking")
                saveScanningTrackingToSubmitTracking(
                    taskId,
                    receiptCode,
                    scanningTracking
                ) thenPass 0
            }
            .flatMap {
                Timber.d("TEST. call --> sendSubmitTrackingToApi")
                sendSubmitTrackingToApi(receiptCode).onErrorReturn {
                    SubmitReceiptResponse("", "", null, null)
                }
            }
            .flatMap {
                Timber.d("TEST. call --> increment pickup count of task")
                offlineRepo.incrementTaskPickupCount(
                    taskId,
                    if (receiptType == ReceiptType.Normal) totalCountTracking else 0
                ) thenPass it
            }
            .flatMap {
                Timber.d("TEST. call --> updateTaskStatusToCompleted")
                offlineRepo.updateTaskStatusToCompleted(taskId) thenPass it
            }
            .flatMap {
                Timber.d("TEST. call --> offlineRepo.updatePendingReceiptToSynced")
                when {
                    it.isSuccess() -> offlineRepo.updatePendingReceiptToSynced(
                        receiptCode,
                        it.receiptId
                    ) thenPass it
                    else -> Single.just(it)
                }
            }
//            .doOnError {
//                if (it is NoConnectivityException) {
//                    Timber.d("TEST. call --> NoConnectivityException with $it")
//                    offlineRepo.updateTaskStatusToCompleted(taskId)()
//                    //throw TaskFailButSavedOfflineException()
//                }
//                //throw it
//            }
    }

    private fun saveScanningTrackingToSubmitTracking(
        taskId: String,
        receiptCode: String,
        scanningTracking: List<PickupScanningTrackingEntity>,
        createdAt: Long = Utils.getCurrentTimestamp()
    ): Completable {
        return Completable.create { emitter ->

            Completable.fromAction {
                pickupDao.insertSubmitTracking(scanningTracking.map {
                    val entity = it.mapToSubmitTracking().toEntity(taskId, receiptCode, createdAt)
                    Timber.d("write submit taskId=$taskId, receiptCode=$receiptCode, createdAt=$createdAt, $it")
                    Timber.d("write submit tracking to db = $entity")
                    entity
                })
            }.thenEmit(emitter, scheduler)

        }.scheduleBy(scheduler)
    }

    private fun PickupScanningTrackingEntity.mapToSubmitTracking() =
        SubmitTracking(
            tracking,
            senderImgUrl,
            senderImgPath,
            receiverImgUrl,
            receiverImgPath,
            zipcode ?: "",
            serviceId ?: 0,
            sizeId ?: 0,
            deliveryFee,
            cartonFee,
            codFee,
            codAmount,
            false,
            "",
            "",
            receiptCode,
            isExtra
        )


    private fun sendSubmitTrackingToApi(receiptCode: String): Single<SubmitReceiptResponse> {
        Timber.d("R.sendSubmitTrackingToApi receiptCode=$receiptCode")
        return pickupDao.getPendingReceiptByReceiptCode(receiptCode)
            .flatMap { pendingReceipt ->
                Timber.d("R.getPendingReceiptByReceiptCode pendingReceipt=$pendingReceipt")
                getSubmitReceipt(pendingReceipt).flatMap { submitReceipt ->
                    Timber.d("R.getSubmitReceipt - submitReceipt=$submitReceipt")
                    pickupService.submitReceipt(pendingReceipt.taskId, submitReceipt)
                }
            }
            .scheduleBy(scheduler)
    }

    private fun getSubmitReceipt(pendingReceiptEntity: PickupPendingReceiptEntity): Single<SubmitReceipt> {
        Timber.d("R.getSubmitReceipt pendingReceiptEntity=$pendingReceiptEntity")
        return pickupDao.getSubmitTrackingByReceiptCode(pendingReceiptEntity.receiptCode).toSingle()
            .flatMap { submitTracking ->
                val submitReceipt = pendingReceiptEntity.toModel()
                submitReceipt.submitTracking = submitTracking.map { it.toModel() }
                Single.just(submitReceipt)
            }
    }

    fun updateScanningTrackingImageUrl(
        trackingCode: String,
        senderImageUrl: String?,
        receiverImageUrl: String?
    ): Single<Int> {
        return Single.fromCallable {
            pickupDao.updateScanningTrackingImageUrl(trackingCode, senderImageUrl, receiverImageUrl)
        }.scheduleBy(scheduler)
    }

    fun uploadAdditionalPhoto(
        uploader: FileUploader,
        photoFileSender: File?,
        photoFileRecipient: File?,
        trackingCode: String,
        userId: String
    ): Single<Pair<String?, String?>> {
        Timber.d("-PickupRepository::uploadAdditionalPhoto($photoFileSender, $photoFileRecipient, $trackingCode, $userId)")

        val currentTime = Utils.getCurrentDateInMills()
        val senderUploadPath =
            "pickup_parcel/${trackingCode}/$currentTime-${userId}-sender.jpg"
        val recipientUploadPath =
            "pickup_parcel/${trackingCode}/$currentTime-${userId}-recipient.jpg"

        fun uploadPhoto(uploader: FileUploader, file: File?, uploadPath: String): Single<String?> {
            if (file == null || !file.exists()) {
                return Single.just("")
            }
            return try {
                uploader.uploadFile(file, uploadPath)
            } catch (e: IOException) {
                e.printStackTrace()
                Single.just(null)
            }
        }

        val uploadSenderFile = uploadPhoto(uploader, photoFileSender, senderUploadPath)
        val uploadRecipientFile = uploadPhoto(uploader, photoFileRecipient, recipientUploadPath)

        return Single.zip(
            uploadSenderFile,
            uploadRecipientFile,
            BiFunction { sender: String?, recipient: String? ->
                Pair(sender, recipient)
            })
    }

    //TODO: query for test
    fun _getAllPendingReceipt() = pickupDao.getPendingReceipt()

    fun _getAllReceipt() = pickupDao.getReceipt()

}

open class ContextWrapper(val context: Context? = null) {
    open fun getStringArray(res: Int): Array<String> = context?.resources?.getStringArray(res)
        ?: emptyArray()

    open fun getIntArray(res: Int): IntArray = context?.resources?.getIntArray(res) ?: intArrayOf()
}
