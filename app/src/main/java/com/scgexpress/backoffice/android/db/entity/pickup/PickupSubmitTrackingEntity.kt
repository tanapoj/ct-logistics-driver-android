package com.scgexpress.backoffice.android.db.entity.pickup

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking

@Entity(tableName = "pickup_submit_tracking", primaryKeys = ["tracking", "receipt_code"])
data class PickupSubmitTrackingEntity(

    @SerializedName("task_id")
    @ColumnInfo(name = "task_id") var taskId: String = "",

    @SerializedName("tracking")
    @ColumnInfo(name = "tracking") var tracking: String = "",

    @SerializedName("sender_img_url")
    @ColumnInfo(name = "sender_img_url") var senderImgUrl: String? = null,

    @SerializedName("sender_img_path")
    @ColumnInfo(name = "sender_img_path") var senderImgPath: String? = null,

    @SerializedName("receiver_img_url")
    @ColumnInfo(name = "receiver_img_url") var receiverImgUrl: String? = null,

    @SerializedName("receiver_img_path")
    @ColumnInfo(name = "receiver_img_path") var receiverImgPath: String? = null,

    @SerializedName("zipcode")
    @ColumnInfo(name = "zipcode") var zipcode: String = "",

    @SerializedName("service")
    @ColumnInfo(name = "service_id") var serviceId: Int = 0,

    @SerializedName("size")
    @ColumnInfo(name = "size_id") var sizeId: Int = 0,

    @SerializedName("delivery_fee")
    @ColumnInfo(name = "delivery_fee") var deliveryFee: Double = .0,

    @SerializedName("carton_fee")
    @ColumnInfo(name = "carton_fee") var cartonFee: Double? = .0,

    @SerializedName("cod_fee")
    @ColumnInfo(name = "cod_fee") var codFee: Double? = .0,

    @SerializedName("cod_amount")
    @ColumnInfo(name = "cod_amount") var codAmount: Double? = .0,

    @SerializedName("receipt_code")
    @ColumnInfo(name = "receipt_code") var receiptCode: String = "",

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at") var createdAt: Long = 0,

    @SerializedName("is_extra")
    @ColumnInfo(name = "is_extra") var isExtra: Boolean = false
) {
    fun toModel(): SubmitTracking = unserializeTo(SubmitTracking::class.java)
}