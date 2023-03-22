package com.scgexpress.backoffice.android.repository.delivery

import android.content.Context
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.DeliveryService
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.common.time.DateTime
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.entity.delivery.DeliveryPendingSentEntity
import com.scgexpress.backoffice.android.db.entity.delivery.DeliverySentTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.delivery.*
import com.scgexpress.backoffice.android.preferrence.DeliveryPreference
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

typealias IdAndValue = Pair<Int, String>

@Singleton
class DeliveryRepository @Inject
constructor(
    private val scheduler: RxThreadScheduler,
    private val masterDao: MasterDataDao,
    private val deliveryService: DeliveryService,
    private val offlineRepo: OfflineRepository,
    private val masterDataRepo: DataRepository,
    private val masterCalculatorRepo: CalculatorRepository,
    private val deliveryPreference: DeliveryPreference
) {

    companion object {
        val DELIVERY_STATUS_IN_PROGRESS = "IN_PROGRESS"
        val DELIVERY_STATUS_DELIVERED = "DELIVERED"
        val DELIVERY_STATUS_RETENTION = "RETENTION"

        fun getUploadPathSignature(ofdCode: String) = "delivery/signature/${ofdCode}.jpg"
        fun getUploadPathTracking(trackingCode: String) = "delivery/tracking/${trackingCode}.jpg"
    }

    fun initFetchTask(): Single<List<DeliveryTask>> {
        return loadTask().scheduleBy(scheduler)
    }

    private fun loadTask(): Single<List<DeliveryTask>> {
        return deliveryService.getDeliveryTasks()
            .toSingle()
            .map { it.items }
            .flatMap {
                offlineRepo.deleteAllTask() thenPass it
            }
            .flatMap {
                offlineRepo.saveTask(it) thenPass it
            }
            .scheduleBy(scheduler)
    }

    fun getAllTask(): Flowable<List<DeliveryTask>> {
        return deliveryService.getDeliveryTasks()
            .map {
                it.items ?: throw IllegalStateException("delivery items cannot be null")
            }
            .onErrorResumeNext { e: Throwable ->
                Timber.e(e)
                offlineRepo.getAllTask()
            }.scheduleBy(scheduler)
    }

    fun getTask(id: String): Single<DeliveryTask> {
        return deliveryService.getDeliveryTask(id)
            .onErrorResumeNext { e: Throwable ->
                Timber.e(e)
                offlineRepo.getTask(id)
            }.scheduleBy(scheduler)
    }

    fun getTaskByTrackingCode(trackingCode: String): Single<DeliveryTask> {
        return Single.just(
            DeliveryTask(
                trackingCode = trackingCode,
                senderName = "ทดสอบ",
                senderCode = "1234",
                createdAt = "2019-10-10 12:34:56"
            )
        )
    }

    fun submitOfd(
        trackingCodeList: List<String>,
        ofdCode: String? = null,
        submitAt: String? = null
    ): Single<OfdSubmitResponse> {
        val ofdCode = ofdCode ?: deliveryPreference.ofdCode
        ?: throw IllegalStateException("no OFD-Code was set")
        val submitAt = submitAt ?: Utils.getCurrentTimestamp().toString()
        val ofd = OfdSubmit(trackingCodeList, ofdCode, submitAt)
        return deliveryService.submitOfd(ofd).scheduleBy(scheduler).toSingle()
    }

    fun getTaskById(taskID: String): Single<DeliveryTask> {
        return Single.create { emitter ->
            deliveryService.getDeliveryTask(taskID).scheduleBy(scheduler).onErrorResumeNext {
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

    fun submitSentDelivery(
        fileUploader: FileUploader,
        ofdCode: String,
        paymentMethod: Int,
        signedName: String,
        signatureImagePath: String,
        scanTrackingList: List<DeliverySentTrackingEntity>,
        submitAt: DateTime = DateTime.now()
    ): Single<SentSubmitResponse> {
        return offlineRepo.createPendingSent(
            ofdCode,
            paymentMethod,
            signedName,
            null,
            signatureImagePath,
            submitAt.timestamp
        ).flatMap { pendingId ->
            offlineRepo.setSentPendingIdToScanningTracking(
                pendingId,
                scanTrackingList.map { it.trackingCode }
            ) thenPassWith pendingId
        }.flatMap { (affectedRows, pendingId) ->
            check(affectedRows > 0) { "no scanning tracking (affectedRows = 0)" }
            Single.just(pendingId)
        }.flatMap { pendingId ->
            syncPendingSent(fileUploader, pendingId)
        }.scheduleBy(scheduler)
    }

    fun syncAll(fileUploader: FileUploader) {

    }

    fun syncAllPendingSent(fileUploader: FileUploader): Completable {
        return offlineRepo.getUnsyncPendingSent().flatMap { list ->
            list.map {
                syncPendingSent(fileUploader, it.id)
            }.let {
                Single.merge(it).toSingle()
            }
        }.toCompletable().scheduleBy(scheduler)
    }

    private fun syncPendingSent(
        fileUploader: FileUploader,
        pendingId: Long
    ): Single<SentSubmitResponse> {
        return offlineRepo.getPendingSent(pendingId).flatMap { pendingSent ->
            offlineRepo.getSentTracking(pendingId) thenPassWith pendingSent
        }.flatMap { (trackingList, pendingSent) ->
            syncPendingSentPhoto(
                fileUploader,
                pendingId,
                pendingSent,
                trackingList
            ) thenPass trackingList
        }.flatMap { trackingList ->
            sendSentDeliveryToApi(pendingId).onErrorReturn {
                SentSubmitResponse("")
            } thenPassWith trackingList
        }.flatMap { (response, trackingList) ->
            val trackingCodeList = trackingList.map { it.trackingCode }
            offlineRepo.updateTaskToComplete(trackingCodeList) thenPass response
        }.flatMap { response ->
            when (response.isAccept()) {
                true -> offlineRepo.updatePendingSentToSynced(pendingId) thenPass response
                false -> Single.just(response)
            }
        }.scheduleBy(scheduler)
    }

    private fun syncPendingSentPhoto(
        fileUploader: FileUploader,
        pendingId: Long,
        pendingSent: DeliveryPendingSentEntity,
        scanTrackingList: List<DeliverySentTrackingEntity>
    ): Completable {
        return uploadSignaturePhotoToS3(
            fileUploader,
            pendingSent
        ).flatMap { url ->
            offlineRepo.setSignaturePhotoUrlToEntity(pendingId, url) thenPass Const.NONE
        }.thenCompletable {
            scanTrackingList.map {
                uploadTrackingPhotoToS3(fileUploader, it).flatMap { url ->
                    when (url.isBlank()) {
                        true -> Single.just(url)
                        false -> offlineRepo.setTrackingPhotoUrlToEntity(
                            pendingId,
                            url
                        ) thenPass url
                    }
                }.toCompletable()
            }.let {
                Completable.concat(it)
            }
        }
    }

    private fun uploadSignaturePhotoToS3(
        fileUploader: FileUploader,
        pendingSent: DeliveryPendingSentEntity
    ): Single<String> {
        val path = pendingSent.recipientSignatureImagePath ?: return Single.just("")
        return FileSyncable(
            fileUploader,
            path,
            getUploadPathSignature(pendingSent.ofdCode)
        ).apply {
            url = pendingSent.recipientSignatureImageUrl
        }.sync()
    }

    private fun uploadTrackingPhotoToS3(
        fileUploader: FileUploader,
        scanTracking: DeliverySentTrackingEntity
    ): Single<String> {
        val path = scanTracking.productImagePath ?: return Single.just("")
        return FileSyncable(
            fileUploader,
            path,
            getUploadPathTracking(scanTracking.trackingCode)
        ).apply {
            url = scanTracking.productImageUrl
        }.sync()
    }

    private fun sendSentDeliveryToApi(pendingId: Long): Single<SentSubmitResponse> {
        val loadPendingSent = offlineRepo.getPendingSent(pendingId)
        val loadSentTracking = offlineRepo.getSentTracking(pendingId)
        return Single.zip(
            loadPendingSent,
            loadSentTracking,
            BiFunction { pending: DeliveryPendingSentEntity, tracking: List<DeliverySentTrackingEntity> ->
                pending to tracking
            })
            .flatMap { (entity, tracking) ->
                val signatureUrl = entity.recipientSignatureImageUrl ?: throw IllegalStateException(
                    "submit sent-delivery to API but signature url is null"
                )
                val model = SentSubmit(
                    tracking.map { it.toModel() },
                    entity.ofdCode,
                    entity.paymentMethod,
                    entity.recipientSignedName,
                    signatureUrl,
                    DateTime.from(entity.submitAt).toISO8601()
                )
                deliveryService.submitSentDelivery(model).toSingle()
            }.scheduleBy(scheduler)
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

    fun getRetentionMainReason(context: ContextWrapper): Single<List<RetentionScanReason>> {
        val reason = context.getStringArray(R.array.main_reason_list)
        val data = reason.map {
            val (id, name) = it.split(",")
            id to name
            RetentionScanReason(id, name)
        }
        return Single.just(data)
    }

    fun getRetentionSubReason(context: ContextWrapper): Single<List<RetentionScanSubReason>> {
        val reason = context.getStringArray(R.array.sub_reason_list)
        val data = reason.map {
            val (id, name, type, allowType) = it.split(",")
            RetentionScanSubReason(id, name, type, allowType)
        }
        return Single.just(data)
    }
}

open class ContextWrapper(val context: Context? = null) {
    open fun getStringArray(res: Int): Array<String> = context?.resources?.getStringArray(res)
        ?: emptyArray()

    open fun getIntArray(res: Int): IntArray = context?.resources?.getIntArray(res) ?: intArrayOf()
}