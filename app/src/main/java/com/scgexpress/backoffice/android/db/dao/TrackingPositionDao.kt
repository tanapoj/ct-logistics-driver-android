package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.TrackingPositionEntity
import io.reactivex.Flowable

@Dao
interface TrackingPositionDao {
    @get:Query("SELECT * FROM trackingPosition ORDER BY position ASC")
    val trackingPositionItems: Flowable<List<TrackingPositionEntity>>

    @Query("SELECT * FROM trackingPosition WHERE userId = :userId AND manifestID = :manifestID ORDER BY position ASC")
    fun getTrackingPositions(userId: String, manifestID: String): Flowable<List<TrackingPositionEntity>>

    @Query("DELETE FROM trackingPosition WHERE userId = :userId AND manifestID = :manifestID AND trackingNumber = :trackingNumber")
    fun deleteTrackingPosition(userId: String, manifestID: String, trackingNumber: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceTrackingPosition(deliveryEntityList: List<TrackingPositionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceTrackingPosition(deliveryEntity: TrackingPositionEntity)

    @Query("DELETE FROM trackingPosition")
    fun deleteTrackingPositions()
}