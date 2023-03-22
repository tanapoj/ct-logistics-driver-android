package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.db.entity.pickup.PickupSubmitTrackingEntity
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking

data class SubmitReceipt(
        @SerializedName("task_id") var taskId: String = "",
        @SerializedName("is_new_generate_task") var isNewGenerateTask: Boolean = false,
        @SerializedName("receipt_code") var receiptCode: String = "",
        @SerializedName("receipt_code_normal") var receiptCodeNormal: String = "",
        @SerializedName("customer_code") var customerCode: String = "",
        @SerializedName("tracking_registered") var trackingRegistered: Int = 0,
        @SerializedName("tracking_total") var trackingTotal: Int = 0,
        @SerializedName("total_fee") var totalFee: Double = .0,
        @SerializedName("total_delivery_fee") var totalDeliveryFee: Double = .0,
        @SerializedName("total_cod_fee") var totalCodFee: Double = .0,
        @SerializedName("total_carton_fee") var totalCartonFee: Double = .0,
        @SerializedName("payment_method") var paymentMethod: Int = 0,
        @SerializedName("receipt_contact_tel") var receiptContactTel: String = "",
        @SerializedName("receipt_contact_email") var receiptContactEmail: String = "",
        @SerializedName("submit_at") var submitAt: String = "",
        @SerializedName("location") var location: Location = Location(),
        @SerializedName("submit_trackings") var submitTracking: List<SubmitTracking> = mutableListOf()
) {
    fun updateSummaryData() {
        trackingRegistered = submitTracking.size
        totalDeliveryFee = submitTracking.sumByDouble { it.deliveryFee }
        totalCodFee = submitTracking.sumByDouble { it.codFee ?: .0 }
        totalCartonFee = submitTracking.sumByDouble { it.cartonFee ?: .0 }
        totalFee = totalDeliveryFee + totalCodFee + totalCartonFee
    }

    fun toPendingEntity(taskId: String, createdAt: Long): PickupSubmitTrackingEntity =
            unserializeTo(PickupSubmitTrackingEntity::class.java) { annotation, _, _ ->
                when (annotation) {
                    "task_id" -> taskId
                    "created_at" -> createdAt
                    else -> null
                }
            }
}