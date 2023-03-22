package com.scgexpress.backoffice.android.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "trackingPosition", primaryKeys = ["userId", "manifestID", "trackingNumber"])
data class TrackingPositionEntity(
    @ColumnInfo(name = "userId") var userId: String = "",
    @ColumnInfo(name = "manifestID") var manifestID: String = "",
    @ColumnInfo(name = "trackingNumber") var trackingNumber: String = "",
    @ColumnInfo(name = "position") var position: Int? = 0
)