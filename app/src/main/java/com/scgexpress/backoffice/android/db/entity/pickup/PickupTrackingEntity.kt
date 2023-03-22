package com.scgexpress.backoffice.android.db.entity.pickup

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.tracking.Tracking

@Entity(tableName = "pickup_tracking", primaryKeys = ["tracking"])
data class PickupTrackingEntity(


    @SerializedName("task_id")
    @ColumnInfo(name = "task_id") var taskId: String = "",

    @SerializedName("tracking")
    @ColumnInfo(name = "tracking") var tracking: String = "",

    @SerializedName("order_id")
    @ColumnInfo(name = "order_id") var orderId: String = "",

    @SerializedName("pinbox")
    @ColumnInfo(name = "pinbox") var pinbox: String? = null,

    @SerializedName("service")
    @ColumnInfo(name = "service") var service: Int = 0,

    @SerializedName("size")
    @ColumnInfo(name = "size") var size: Int = 0,

    @SerializedName("zipcode")
    @ColumnInfo(name = "zipcode") var zipcode: String = ""
){
    fun toModel(): Tracking = unserializeTo(Tracking::class.java)
}