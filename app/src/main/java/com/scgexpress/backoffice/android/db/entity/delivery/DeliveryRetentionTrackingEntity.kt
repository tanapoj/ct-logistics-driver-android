package com.scgexpress.backoffice.android.db.entity.delivery

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName


@Entity(tableName = "delivery_retention_tracking", primaryKeys = ["tracking_code"])
data class DeliveryRetentionTrackingEntity(

    @SerializedName("tracking_code")
    @ColumnInfo(name = "tracking_code") var trackingCode: String = "",

    //FK to pending_retention(id)
    @ColumnInfo(name = "pending_id") var pendingId: Int = 0,

    @SerializedName("reason_code")
    @ColumnInfo(name = "reason_code") var reasonCode: String = "",

    @SerializedName("reason_data")
    @ColumnInfo(name = "reason_data") var reasonData: String = ""
)