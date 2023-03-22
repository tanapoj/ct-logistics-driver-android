package com.scgexpress.backoffice.android.repository.pickup

import android.util.Log
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.common.thenEmit
import com.scgexpress.backoffice.android.common.trimTrackingCode
import com.scgexpress.backoffice.android.db.dao.PickupDao
import com.scgexpress.backoffice.android.db.entity.pickup.PickupPendingReceiptEntity
import com.scgexpress.backoffice.android.db.entity.pickup.PickupSubmitTrackingEntity
import com.scgexpress.backoffice.android.db.entity.pickup.PickupReceiptEntity
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.pickup.Receipt
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking
import com.scgexpress.backoffice.android.model.tracking.Tracking
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRepository @Inject
constructor(
    private val scheduler: RxThreadScheduler,
    private val dao: PickupDao,
    private val calculatorRepo: CalculatorRepository
) {

    private val TAG = "-pickup_offline_repo"

    fun clearTaskData(): Completable {
        return Completable.create {
            dao.truncateSubmitTracking()
            dao.truncateTrackings()
            dao.truncateReceipt()
            dao.truncateTasks()
            it.onComplete()
        }.scheduleBy(scheduler)
    }

    fun saveTask(tasks: List<PickupTask>): Completable {
        val entities = tasks.map { it.toEntity() }
        return Completable.create {
            dao.insertTasks(entities)
            it.onComplete()
        }.scheduleBy(scheduler)
    }

    fun saveTracking(taskId: String, trackings: List<Tracking>): Completable {
        val entities = trackings.map { it.toEntity(taskId) }
        return Completable.create {
            dao.insertTrackings(entities)
            it.onComplete()
        }.scheduleBy(scheduler)
    }

    fun saveReceipt(taskId: String, receipts: List<Receipt>, createdAt: Long? = null): Completable {
        val createdAt = createdAt ?: Utils.getCurrentTimestamp()
        val entities = receipts.map { it.toEntity(taskId, createdAt) }
        return Completable.create {
            dao.insertReceipt(entities)
            it.onComplete()
        }.scheduleBy(scheduler)
    }

    fun saveSubmitTracking(
        taskId: String,
        receiptCode: String,
        submitTrackings: List<SubmitTracking>,
        createdAt: Long = 0
    ): Completable {
        //val createdAt = createdAt ?: Utils.run { getServerDateTimeFormat(getCurrentTimestamp()) }
        val entities = submitTrackings.map {
            it.toEntity(taskId, receiptCode, createdAt)
        }.map {
            it.tracking = it.tracking.trimTrackingCode()
            it
        }
        return Completable.create {
            dao.insertSubmitTracking(entities)
            it.onComplete()
        }.scheduleBy(scheduler)
    }

    fun getTask(taskId: String): Single<PickupTask> {
        return dao.getTask(taskId).scheduleBy(scheduler).map { item ->
            Timber.d("get task $taskId from db: $item")
            item.toModel().also {
                it.isOfflineData = true
            }
        }
    }

    fun getAllTask(): Flowable<List<PickupTask>> {
        return dao.tasks.scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun getTaskByIds(ids: List<String>): Flowable<List<PickupTask>> {
        return dao.getTaskByIds(ids).scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun getTracking(taskId: String): Single<Tracking> {
        return dao.getTracking(taskId).scheduleBy(scheduler).map { item ->
            item.toModel().also { it.isOfflineData = true }
        }
    }

    fun getTrackingsOf(taskId: String): Single<List<Tracking>> {
        return dao.getTrackingsOf(taskId).scheduleBy(scheduler).map { list ->
            Log.i("aaa", "REPO offline.getTrackingsOf: $list")
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun getSubmitTracking(): Flowable<List<SubmitTracking>> {
        return dao.getSubmitTracking().scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun getSubmitTracking(trackingId: String): Single<SubmitTracking> {
        return dao.getSubmitTracking(trackingId).scheduleBy(scheduler).map { item ->
            item.toModel().also { it.isOfflineData = true }
        }
    }

    fun getSubmitTrackingsOf(taskId: String): Flowable<List<SubmitTracking>> {
        return dao.getSubmitTrackingByTaskId(taskId).scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun getReceipt(receiptCode: String): Single<Receipt> {
        return dao.getReceipt(receiptCode).scheduleBy(scheduler).map { item ->
            item.toModel().also { it.isOfflineData = true }
        }
    }

    fun getReceiptsOf(taskId: String): Single<List<Receipt>> {
        return dao.getReceiptsOf(taskId).scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel().also { it.isOfflineData = true }
            }
        }
    }

    fun searchTask(codeOrTel: String): Flowable<List<PickupTask>> {
        return dao.getTaskByCodeOrTel(codeOrTel).scheduleBy(scheduler).map { list ->
            list.map { item ->
                item.toModel()
            }
        }
    }

    fun deleteTracking(taskId: String, trackingId: String): Completable {
        return Completable.create {
            dao.deleteSubmitTracking(taskId, trackingId)
            it.onComplete()
        }.scheduleBy(scheduler)
    }

    fun updatePendingReceiptToSynced(receiptCode: String, receiptId: String): Completable {
        return Completable.create { emitter ->
            dao.updatePendingReceiptToDone(receiptCode, receiptId, true)
            emitter.onComplete()
        }.scheduleBy(scheduler)
    }

    fun deleteAllPendingReceipt(): Completable {
        return Completable.create { emitter ->
            dao.truncatePendingReceipt()
            emitter.onComplete()
        }.scheduleBy(scheduler)
    }

    fun createPendingReceipt(
        taskId: String,
        receiptCode: String,
        receiptCodeNormal: String,
        customerCode: String,
        totalTrackingCount: Int,
        paymentMethodId: Int,
        contactTel: String,
        contactEmail: String,
        latLong: Pair<Double?, Double?>,
        scanningTracking: List<PickupScanningTrackingEntity>
    ): Completable {
        return Completable.create { emitter ->

            Timber.d("create pending receipt with scanningTracking=$scanningTracking")
            check(scanningTracking.isNotEmpty()) { "cannot create pending receipt with empty trackingList" }

            //calculate fee
            val (delivery, cod, carton, total) = calculatorRepo.calculateSummaryFee(
                customerCode,
                scanningTracking
            )
            val (latitude, longitude) = latLong
            val time = Utils.getCurrentTimestamp()

            dao.getTask(taskId).map {
                val pendingReceipt = PickupPendingReceiptEntity(
                    receiptCode,
                    receiptCodeNormal,
                    "",
                    it.isNewGenerateTask,
                    customerCode,
                    false,
                    scanningTracking.size,
                    totalTrackingCount,
                    total,
                    delivery,
                    cod,
                    carton,
                    paymentMethodId,
                    contactTel,
                    contactEmail,
                    time,
                    Location(latitude = latitude?.toString(), longitude = longitude?.toString()),
                    taskId
                )

                Timber.d("1-pendingReceipt=$pendingReceipt")
                pendingReceipt.fromScanningTrackingEntity(scanningTracking)
                Timber.d("2-pendingReceipt=$pendingReceipt")
                dao.insertPendingReceipt(listOf(pendingReceipt))
                it
            }.thenEmit(emitter, scheduler)
        }.scheduleBy(scheduler)
    }

    fun createReceipt(taskId: String, receiptCode: String): Completable {
        return Completable.create { emitter ->
            val receipt = PickupReceiptEntity(receiptCode, "-", 0, taskId)
            dao.insertReceipt(listOf(receipt))
            emitter.onComplete()
        }.scheduleBy(scheduler)
    }

    fun updateTaskStatusToCompleted(taskId: String): Completable {
        return Completable.create { emitter ->
            val ef = dao.updateTaskStatus(taskId, "COMPLETED")
            Timber.d("OffRepoupdateTaskStatusToCompleted taskId=$taskId ef=$ef")
            emitter.onComplete()
        }.scheduleBy(scheduler)
    }

    fun incrementTaskPickupCount(taskId: String, incrementBy: Int): Completable {
        return when {
            incrementBy > 0 -> dao.getTask(taskId)
                .map {
                    dao.updateTaskPickupCount(taskId, it.pickupedCount + incrementBy)
                }.toCompletable().scheduleBy(scheduler)
            else -> Completable.complete()
        }
    }

    fun moveTrackingToSubmitTracking(scanningTrackingEntities: List<PickupScanningTrackingEntity>): Completable {
        return Completable.concat(scanningTrackingEntities.map {
            moveTrackingToSubmitTracking(it)
        }).scheduleBy(scheduler)
    }

    fun moveTrackingToSubmitTracking(scanningTrackingEntity: PickupScanningTrackingEntity): Completable {
        return Completable.create { emitter ->
            dao.getTracking(scanningTrackingEntity.tracking).subscribe({
                val submitTracking = with(scanningTrackingEntity) {
                    PickupSubmitTrackingEntity(
                        taskId,
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
                        receiptCode,
                        Utils.getCurrentTimestamp(),
                        isExtra
                    )
                }
                dao.insertSubmitTracking(submitTracking)
                dao.deleteTracking(scanningTrackingEntity.tracking)
                emitter.onComplete()
            }, emitter::onError)
        }.scheduleBy(scheduler)
    }

}