package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.db.entity.pickup.PickupTaskEntity
import com.scgexpress.backoffice.android.model.Location
import java.io.Serializable

data class PickupTask(
    @SerializedName("id") var id: String = "",
    @SerializedName("is_new_generate_task") var isNewGenerateTask: Boolean = false,
    @SerializedName("booking_code") var bookingCode: String? = null,
    @SerializedName("customer_code") var customerCode: String = "",
    @SerializedName("customer_name") var customerName: String = "",
    @SerializedName("sender_code") var senderCode: String = "",
    @SerializedName("sender_name") var senderName: String = "",
    @SerializedName("tel") var tel: String = "",
    @SerializedName("location") var location: Location = Location(),
    @SerializedName("total_count") var totalCount: Int = 0,
    @SerializedName("pickuped_count") var pickupedCount: Int = 0,
    @SerializedName("service_type_count") var serviceTypeCount: PickupServiceTypeCount = PickupServiceTypeCount(),
    @SerializedName("remark") var remark: String = "",
    @SerializedName("status") var status: String = "",
    @SerializedName("created_at") var createdAt: String = "",
    @SerializedName("update_at") var updatedAt: String = "",
    @SerializedName("pickup_at_string") var pickupedAt: String = "",
    //@SerializedName("timestamp") var timestamp: Long = Utils.getCurrentTimestamp(),
    @SerializedName("offline_data") var trackingInfo: TaskTrackingInfo? = null,
    var isOfflineData: Boolean = false
) : Serializable {
    fun toEntity(): PickupTaskEntity = unserializeTo(PickupTaskEntity::class.java)
    fun isNewBookingStatus() = status == "NEW_BOOKING"
}

data class PickupTaskList(
    @SerializedName("tasks") var items: List<PickupTask>? = null
) : Serializable

enum class Status {
    UNKNOWN, NEW_BOOKING, IN_PROGRESS, COMPLETED
}