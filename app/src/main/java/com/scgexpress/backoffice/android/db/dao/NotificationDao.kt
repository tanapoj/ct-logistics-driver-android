package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.NotificationEntity
import io.reactivex.Flowable

@Dao
interface NotificationDao {
    @get:Query("SELECT * FROM notification ORDER BY timestamp DESC")
    val notifications: Flowable<List<NotificationEntity>>

    @Query("SELECT * FROM notification WHERE userID = :userID ORDER BY timestamp DESC")
    fun getNotificationByUser(userID: String): Flowable<List<NotificationEntity>>

    @Query("SELECT * FROM notification WHERE id = :notificationId")
    fun getNotification(notificationId: String): Flowable<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceNotification(notificationEntityList: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceNotification(notificationEntity: NotificationEntity)

    @Query("DELETE FROM notification WHERE metaID = :metaID")
    fun deleteNotification(metaID: String)

    @Query("DELETE FROM notification")
    fun deleteNotification()
}