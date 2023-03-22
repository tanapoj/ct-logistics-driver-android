package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.pickup.*
import io.reactivex.Flowable
import io.reactivex.Single


@Dao
interface PickupDao {

    // Task

    @get:Query("SELECT * FROM pickup_task")
    val tasks: Flowable<List<PickupTaskEntity>>

    @Query("SELECT * FROM pickup_task WHERE is_new_generate_task = :isNewGenerateTask AND (customer_code like :codeOrTel OR sender_code like :codeOrTel OR tel like :codeOrTel)")
    fun getTaskByCodeOrTel(codeOrTel: String, isNewGenerateTask: Boolean = false): Flowable<List<PickupTaskEntity>>

    @Query("SELECT * FROM pickup_task WHERE id IN (:ids)")
    fun getTaskByIds(ids: List<String>): Flowable<List<PickupTaskEntity>>

    @Query("SELECT * FROM pickup_task WHERE id = :id")
    fun getTask(id: String): Single<PickupTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(tasks: List<PickupTaskEntity>)

    @Query("UPDATE pickup_task SET status = :status WHERE id = :taskId")
    fun updateTaskStatus(taskId: String, status: String): Int

    @Query("UPDATE pickup_task SET pickuped_count = :pickupCount WHERE id = :taskId")
    fun updateTaskPickupCount(taskId: String, pickupCount: Int): Int

    @Query("DELETE FROM pickup_task")
    fun truncateTasks()

    //Tracking

    @Query("SELECT * FROM pickup_tracking WHERE tracking = :trackingId")
    fun getTracking(trackingId: String): Single<PickupTrackingEntity>

    @Query("SELECT * FROM pickup_tracking WHERE task_id = :taskId")
    fun getTrackingsOf(taskId: String): Single<List<PickupTrackingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrackings(trackings: List<PickupTrackingEntity>)

    @Query("DELETE FROM pickup_tracking")
    fun truncateTrackings()

    @Query("DELETE FROM pickup_tracking WHERE tracking =:trackingCode")
    fun deleteTracking(trackingCode: String)

    //Submit Tracking

    @Query("SELECT * FROM pickup_submit_tracking")
    fun getSubmitTracking(): Flowable<List<PickupSubmitTrackingEntity>>

    @Query("SELECT * FROM pickup_submit_tracking WHERE tracking = :trackingId")
    fun getSubmitTracking(trackingId: String): Single<PickupSubmitTrackingEntity>

    @Query("SELECT * FROM pickup_submit_tracking WHERE task_id = :taskId")
    fun getSubmitTrackingByTaskId(taskId: String): Flowable<List<PickupSubmitTrackingEntity>>

    @Query("SELECT * FROM pickup_submit_tracking WHERE receipt_code = :receiptCode")
    fun getSubmitTrackingByReceiptCode(receiptCode: String): Flowable<List<PickupSubmitTrackingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubmitTracking(tracking: PickupSubmitTrackingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubmitTracking(trackings: List<PickupSubmitTrackingEntity>)

    @Query("DELETE FROM pickup_submit_tracking WHERE task_id = :taskId")
    fun deleteSubmitTrackingByTaskId(taskId: String)

    @Query("DELETE FROM pickup_submit_tracking WHERE task_id = :taskId AND tracking =:trackingCode")
    fun deleteSubmitTracking(taskId: String, trackingCode: String)

    @Query("DELETE FROM pickup_submit_tracking")
    fun truncateSubmitTracking()

    //Receipt

    @Query("SELECT * FROM pickup_receipt")
    fun getReceipt(): Flowable<List<PickupReceiptEntity>>

    @Query("SELECT * FROM pickup_receipt WHERE receipt_code = :receiptCode")
    fun getReceipt(receiptCode: String): Single<PickupReceiptEntity>

    @Query("SELECT * FROM pickup_receipt WHERE task_id = :taskId")
    fun getReceiptsOf(taskId: String): Single<List<PickupReceiptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceipt(receipts: List<PickupReceiptEntity>)

    @Query("DELETE FROM pickup_receipt")
    fun truncateReceipt()

    //Pending Receipt

    @Query("SELECT * FROM pending_receipt")
    fun getPendingReceipt(): Flowable<List<PickupPendingReceiptEntity>>

    @Query("SELECT * FROM pending_receipt WHERE sync = :sync")
    fun getPendingReceipt(sync: Boolean): Flowable<List<PickupPendingReceiptEntity>>

    @Query("SELECT * FROM pending_receipt WHERE task_id = :taskId")
    fun getPendingReceipt(taskId: String): Single<List<PickupPendingReceiptEntity>>

    @Query("SELECT * FROM pending_receipt WHERE receipt_code = :receiptCode")
    fun getPendingReceiptByReceiptCode(receiptCode: String): Single<PickupPendingReceiptEntity>

    @Query("UPDATE pending_receipt SET receipt_id = :receiptId, sync = :sync WHERE receipt_code = :receiptCode")
    fun updatePendingReceiptToDone(
        receiptCode: String,
        receiptId: String,
        sync: Boolean = true
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPendingReceipt(receipts: List<PickupPendingReceiptEntity>)

    @Query("DELETE FROM pending_receipt")
    fun truncatePendingReceipt()

    @Query("SELECT COUNT(receipt_id) FROM pending_receipt WHERE sync = :sync")
    fun countPendingReceipt(sync: Boolean = false): Int


    //Scanning Tracking

    @Query("SELECT * FROM pickup_scanning_tracking")
    fun getScanningTracking(): Flowable<List<PickupScanningTrackingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScanningTracking(tracking: PickupScanningTrackingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScanningTracking(tracking: List<PickupScanningTrackingEntity>)

    @Query("UPDATE pickup_scanning_tracking SET sender_img_url = :senderImageUrl, receiver_img_url = :receiverImageUrl WHERE tracking = :trackingCode")
    fun updateScanningTrackingImageUrl(
        trackingCode: String,
        senderImageUrl: String?,
        receiverImageUrl: String?
    ): Int

    @Query("DELETE FROM pickup_scanning_tracking WHERE tracking = :trackingCode")
    fun deleteScanningTracking(trackingCode: String)

    @Query("DELETE FROM pickup_scanning_tracking")
    fun truncateScanningTracking()
}