package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.DeliveryEntity
import com.scgexpress.backoffice.android.db.entity.delivery.DeliveryPendingSentEntity
import com.scgexpress.backoffice.android.db.entity.delivery.DeliverySentTrackingEntity
import com.scgexpress.backoffice.android.db.entity.delivery.DeliveryTaskEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface DeliveryDao {
    @get:Query("SELECT * FROM delivery_task ORDER BY created_at DESC")
    val tasks: Flowable<List<DeliveryTaskEntity>>

    @Query("SELECT * FROM delivery_task WHERE id = :id")
    fun getDeliveryTask(id: String): Single<DeliveryTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDeliveries(deliveryEntityList: List<DeliveryTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDelivery(deliveryEntity: DeliveryTaskEntity)

    @Query("DELETE FROM delivery_task")
    fun truncateTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplacePendingSent(deliveryEntityList: DeliveryPendingSentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceSentTracking(list: DeliverySentTrackingEntity)

    @Query("SELECT * FROM delivery_pending_sent WHERE id = :pendingId")
    fun getPendingSent(pendingId: Long): Flowable<DeliveryPendingSentEntity>

    @Query("SELECT * FROM delivery_pending_sent WHERE sync = :sync")
    fun getPendingSentBySyncStatus(sync: Boolean = false): Flowable<List<DeliveryPendingSentEntity>>

    @Query("SELECT * FROM delivery_sent_tracking WHERE pending_id = :pendingId")
    fun getSentTracking(pendingId: Long): Flowable<List<DeliverySentTrackingEntity>>

    @Query("SELECT * FROM delivery_sent_tracking WHERE pending_id = 0")
    fun getScanningSentTracking(): Flowable<List<DeliverySentTrackingEntity>>

    @Query("UPDATE delivery_sent_tracking SET pending_id = :pendingId WHERE tracking_code in (:scanningTrackingCode)")
    fun setPendingSentIdToTracking(pendingId: Long, scanningTrackingCode: List<String>): Int

    @Query("UPDATE delivery_pending_sent SET recipient_signature_img_url = :url WHERE id = :pendingId")
    fun setSignatureUrlToPendingSent(pendingId: Long, url: String): Int

    @Query("UPDATE delivery_sent_tracking SET product_img_url = :url WHERE tracking_code = :trackingCode")
    fun setSentTrackingUrl(trackingCode: String, url: String): Int

    @Query("UPDATE delivery_task SET delivery_status = :deliveryStatus WHERE tracking_code = :trackingCode")
    fun setDeliveryTaskStatus(trackingCode: String, deliveryStatus: String = "DELIVERED"): Int

    @Query("UPDATE delivery_pending_sent SET sync = :sync WHERE id = :pendingId")
    fun setPendingSentSyncStatus(
        pendingId: Long,
        sync: Boolean = true
    ): Int


    /* legacy delivery */

    @Deprecated("legacy delivery")
    @get:Query("SELECT groupID FROM delivery WHERE groupID != '' ORDER BY timestamp DESC LIMIT 1")
    val lastGroupID: Single<String>

    @Deprecated("legacy delivery")

    @Query("SELECT * FROM delivery_task WHERE id = :id")
    fun getTask(id: String): Single<DeliveryTaskEntity>

    @Query("SELECT * FROM delivery WHERE userId = :userId AND sync = :synced")
    fun getUnSyncedDelivery(userId: String, synced: Boolean = false): Flowable<List<DeliveryEntity>>

    @Deprecated("legacy delivery")
    @Query("SELECT * FROM delivery WHERE userId = :userId AND manifestID = :manifestID AND groupID = :groupID AND deleted = :deleted ORDER BY timestamp DESC")
    fun getDelivery(
        userId: String,
        manifestID: String,
        groupID: String,
        deleted: Boolean
    ): Flowable<List<DeliveryEntity>>

    @Deprecated("legacy delivery")
    @Query("SELECT * FROM delivery WHERE userId = :userId AND manifestID = :manifestID AND deleted = :deleted ORDER BY timestamp DESC")
    fun getDelivery(
        userId: String,
        manifestID: String,
        deleted: Boolean
    ): Flowable<List<DeliveryEntity>>

    @Deprecated("legacy delivery")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDelivery(deliveryEntityList: List<DeliveryEntity>)

    @Deprecated("legacy delivery")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDelivery(deliveryEntity: DeliveryEntity)

    @Deprecated("legacy delivery")
    @Query("DELETE FROM delivery WHERE userId = :userId AND manifestID = :manifestID AND groupID = :groupID AND statusID = :statusID")
    fun deleteDelivery(userId: String, manifestID: String, groupID: String, statusID: String)

    @Deprecated("legacy delivery")
    @Query("DELETE FROM delivery")
    fun deleteDelivery()
}