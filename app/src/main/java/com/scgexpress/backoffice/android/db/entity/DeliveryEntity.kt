package com.scgexpress.backoffice.android.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "delivery", primaryKeys = ["groupID", "userId", "manifestID", "trackingNumber"])
data class DeliveryEntity(
        @ColumnInfo(name = "groupID") var groupID: String = "",
        @ColumnInfo(name = "userId") var userId: String = "",
        @ColumnInfo(name = "manifestID") var manifestID: String = "",
        @ColumnInfo(name = "trackingNumber") var trackingNumber: String = "",
        @ColumnInfo(name = "senderCode") var senderCode: String? = "",
        @ColumnInfo(name = "senderName") var senderName: String? = "",
        @ColumnInfo(name = "statusID") var statusID: String? = "",
        @ColumnInfo(name = "codAmount") var codAmount: Double? = 0.0,
        @ColumnInfo(name = "codCollected") var codCollected: Double? = 0.0,
        @ColumnInfo(name = "orderDate") var orderDate: String? = "",
        @ColumnInfo(name = "deleted") var deleted: Boolean? = false,
        @ColumnInfo(name = "sync") var sync: Boolean? = false,
        @ColumnInfo(name = "timestamp") var timestamp: Long? = 0,
        @ColumnInfo(name = "note") var note: String? = ""
)