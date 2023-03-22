package com.scgexpress.backoffice.android.db.entity.pickup

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.pickup.SubmitReceipt

@Entity(tableName = "pending_receipt", primaryKeys = ["receipt_code"])
data class PickupPendingReceiptEntity(

        @SerializedName("receipt_code")
        @ColumnInfo(name = "receipt_code") var receiptCode: String,

        //save normal receipt-code (from normal (P-SDF), cod (P-SCA), box (P-BXC)) for SCG API referencing
        @SerializedName("receipt_code_normal")
        @ColumnInfo(name = "receipt_code_normal") var receiptCodeNormal: String,

        @SerializedName("receipt_id")
        @ColumnInfo(name = "receipt_id") var receiptId: String,

        @SerializedName("is_new_generate_task")
        @ColumnInfo(name = "is_new_generate_task") var isNewGenerateTask: Boolean = false,

        @SerializedName("customer_code")
        @ColumnInfo(name = "customer_code") var customerCode: String,

        @SerializedName("sync")
        @ColumnInfo(name = "sync") var sync: Boolean,

        @SerializedName("tracking_registered")
        @ColumnInfo(name = "tracking_registered") var trackingRegistered: Int,

        @SerializedName("tracking_total")
        @ColumnInfo(name = "tracking_total") var trackingTotal: Int,

        @SerializedName("total_fee")
        @ColumnInfo(name = "total_fee") var totalFee: Double,

        @SerializedName("total_delivery_fee")
        @ColumnInfo(name = "total_delivery_fee") var totalDeliveryFee: Double,

        @SerializedName("total_cod_fee")
        @ColumnInfo(name = "total_cod_fee") var totalCodFee: Double,

        @SerializedName("total_carton_fee")
        @ColumnInfo(name = "total_carton_fee") var totalCartonFee: Double,

        @SerializedName("payment_method")
        @ColumnInfo(name = "payment_method") var paymentMethod: Int,

        @SerializedName("receipt_contact_tel")
        @ColumnInfo(name = "receipt_contact_tel") var receiptContactTel: String,

        @SerializedName("receipt_contact_email")
        @ColumnInfo(name = "receipt_contact_email") var receiptContactEmail: String,

        @SerializedName("submit_at")
        @ColumnInfo(name = "submit_at") var submitAt: Long,

        @SerializedName("location")
        @Embedded(prefix = "location_") var location: Location,

        @SerializedName("task_id")
        @ColumnInfo(name = "task_id") var taskId: String
) {
    fun toModel(): SubmitReceipt {
        val model = unserializeTo(SubmitReceipt::class.java)
        model.submitAt = Utils.getServerDateTimeFormat(submitAt)
        model.location = with(model.location) {
            fun removeNullString(str: String?) = if(str == "null") null else str
            Location(address, removeNullString(zipcode), removeNullString(latitude), removeNullString(longitude))
        }
        return model
    }
    fun fromScanningTrackingEntity(scanningTrackingEntity: List<PickupScanningTrackingEntity>) = with(scanningTrackingEntity) {

        fun sum(a: Double, b: Double) = a + b

        trackingRegistered = size
        totalDeliveryFee = map { it.deliveryFee }.fold(.0, ::sum)
        totalCodFee = map { it.codFee ?: .0 }.fold(.0, ::sum)
        totalCartonFee = map { it.cartonFee ?: .0 }.fold(.0, ::sum)
        totalFee = totalDeliveryFee + totalCodFee + totalCartonFee
    }
}