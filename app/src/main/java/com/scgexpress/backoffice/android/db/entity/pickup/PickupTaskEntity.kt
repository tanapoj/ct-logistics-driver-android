package com.scgexpress.backoffice.android.db.entity.pickup

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.pickup.PickupServiceTypeCount
import com.scgexpress.backoffice.android.model.pickup.PickupTask

@Entity(tableName = "pickup_task", primaryKeys = ["id"])
data class PickupTaskEntity(

    @SerializedName("id")
    @ColumnInfo(name = "id") var id: String = "",

    @SerializedName("is_new_generate_task")
    @ColumnInfo(name = "is_new_generate_task") var isNewGenerateTask: Boolean = false,

    @SerializedName("booking_code")
    @ColumnInfo(name = "booking_code") var bookingCode: String? = null,

    @SerializedName("customer_code")
    @ColumnInfo(name = "customer_code") var customerCode: String = "",

    @SerializedName("customer_name")
    @ColumnInfo(name = "customer_name") var customerName: String = "",

    @SerializedName("sender_code")
    @ColumnInfo(name = "sender_code") var senderCode: String = "",

    @SerializedName("sender_name")
    @ColumnInfo(name = "sender_name") var senderName: String = "",

    @SerializedName("tel")
    @ColumnInfo(name = "tel") var tel: String = "",

    @SerializedName("location")
    @Embedded var location: Location = Location(),

    @SerializedName("total_count")
    @ColumnInfo(name = "total_count") var totalCount: Int = 0,

    @SerializedName("pickuped_count")
    @ColumnInfo(name = "pickuped_count") var pickupedCount: Int = 0,

    @SerializedName("service_type_count")
    @Embedded var serviceTypeCount: PickupServiceTypeCount = PickupServiceTypeCount(),

    @SerializedName("remark")
    @ColumnInfo(name = "remark") var remark: String = "",

    //NEW_BOOKING, IN_PROGRESS, COMPLETED
    @SerializedName("status")
    @ColumnInfo(name = "status") var status: String = "",

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at") var createdAt: Long = 0,

    @SerializedName("update_at")
    @ColumnInfo(name = "update_at") var updateAt: Long = 0,

    @SerializedName("pickup_at_string")
    @ColumnInfo(name = "pickup_at_string") var pickupAt: String = ""
) {
    fun toModel(): PickupTask = unserializeTo(PickupTask::class.java)
}