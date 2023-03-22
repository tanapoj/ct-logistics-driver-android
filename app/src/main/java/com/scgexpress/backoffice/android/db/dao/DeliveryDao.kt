package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.DeliveryEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface DeliveryDao {
    @get:Query("SELECT * FROM delivery ORDER BY timestamp DESC")
    val delivery: Flowable<List<DeliveryEntity>>

    @get:Query("SELECT groupID FROM delivery WHERE groupID != '' ORDER BY timestamp DESC LIMIT 1")
    val lastGroupID: Single<String>

    @Query("SELECT * FROM delivery WHERE userId = :userId AND sync = :synced")
    fun getUnSyncedDelivery(userId: String, synced: Boolean = false): Flowable<List<DeliveryEntity>>

    @Query("SELECT * FROM delivery WHERE userId = :userId AND manifestID = :manifestID AND groupID = :groupID AND deleted = :deleted ORDER BY timestamp DESC")
    fun getDelivery(userId: String, manifestID: String, groupID: String, deleted: Boolean): Flowable<List<DeliveryEntity>>

    @Query("SELECT * FROM delivery WHERE userId = :userId AND manifestID = :manifestID AND deleted = :deleted ORDER BY timestamp DESC")
    fun getDelivery(userId: String, manifestID: String, deleted: Boolean): Flowable<List<DeliveryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDelivery(deliveryEntityList: List<DeliveryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDelivery(deliveryEntity: DeliveryEntity)

    @Query("DELETE FROM delivery WHERE userId = :userId AND manifestID = :manifestID AND groupID = :groupID AND statusID = :statusID")
    fun deleteDelivery(userId: String, manifestID: String, groupID: String, statusID: String)

    @Query("DELETE FROM delivery")
    fun deleteDelivery()
}