package com.scgexpress.backoffice.android.repository.delivery

import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.common.toSingle
import com.scgexpress.backoffice.android.db.dao.DeliveryDao
import com.scgexpress.backoffice.android.db.entity.delivery.DeliveryPendingSentEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRepository @Inject
constructor(
    private val scheduler: RxThreadScheduler,
    private val dao: DeliveryDao
) {
    fun getAllTask(): Flowable<List<DeliveryTask>> {
        return dao.tasks.scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun getTask(id: String): Single<DeliveryTask> {
        return dao.getDeliveryTask(id).scheduleBy(scheduler).map { item ->
            item.toModel().also { it.isOfflineData = true }
        }
    }

    fun saveTask(tasks: List<DeliveryTask>) = Completable.create { emitter ->
        dao.insertOrReplaceDeliveries(tasks.map { it.toEntity() })
        emitter.onComplete()
    }.scheduleBy(scheduler)

    fun deleteAllTask() = Completable.create { emitter ->
        dao.truncateTasks()
        emitter.onComplete()
    }.scheduleBy(scheduler)

    fun createPendingSent(
        ofdCode: String,
        paymentMethod: Int,
        signedName: String,
        signatureImageUrl: String?,
        signatureImagePath: String,
        submitAt: Long
    ) = Single.fromCallable<Long> {
        val pendingDeliveringEntity = DeliveryPendingSentEntity(
            0,
            ofdCode,
            paymentMethod,
            signedName,
            signatureImageUrl,
            signatureImagePath,
            submitAt
        )
        val id = dao.insertOrReplacePendingSent(pendingDeliveringEntity)
        id
    }.scheduleBy(scheduler)

    fun setSentPendingIdToScanningTracking(pendingId: Long, scanningTrackingCode: List<String>) =
        Single.fromCallable<Int> {
            dao.setPendingSentIdToTracking(pendingId, scanningTrackingCode)
        }.scheduleBy(scheduler)

    fun getUnsyncPendingSent() =
        dao.getPendingSentBySyncStatus().toSingle().scheduleBy(scheduler)

    fun getPendingSent(pendingId: Long) =
        dao.getPendingSent(pendingId).toSingle().scheduleBy(scheduler)

    fun getSentTracking(pendingId: Long) =
        dao.getSentTracking(pendingId).toSingle().scheduleBy(scheduler)

    fun setSignaturePhotoUrlToEntity(
        pendingId: Long,
        url: String
    ) = Completable.fromAction {
        dao.getPendingSent(pendingId).map { pendingSentEntity ->
            pendingSentEntity.apply {
                recipientSignatureImageUrl = url
            }
            dao.insertOrReplacePendingSent(pendingSentEntity)
        }
    }.scheduleBy(scheduler)

    fun setTrackingPhotoUrlToEntity(
        pendingId: Long,
        url: String
    ) = Completable.fromAction {
        dao.getSentTracking(pendingId).map { sentTracking ->
            sentTracking.map {
                it.apply {
                    productImageUrl = url
                }
            }
        }.map {
            for (item in it) {
                dao.insertOrReplaceSentTracking(item)
            }
        }
    }.scheduleBy(scheduler)

    fun updateTaskToComplete(trackingCodeList: List<String>): Completable {
        return Completable.fromCallable {
            for (trackingCode in trackingCodeList) {
                dao.setDeliveryTaskStatus(
                    trackingCode,
                    DeliveryRepository.DELIVERY_STATUS_DELIVERED
                )
            }
        }.scheduleBy(scheduler)
    }

    fun updatePendingSentToSynced(pendingId: Long): Completable {
        return Completable.fromCallable {
            dao.setPendingSentSyncStatus(pendingId, true)
        }.scheduleBy(scheduler)
    }
}