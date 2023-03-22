package com.scgexpress.backoffice.android.model.tracking

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.db.entity.pickup.PickupSubmitTrackingEntity
import java.io.Serializable

data class SubmitTracking(
    @SerializedName("tracking") var tracking: String = "",
    @SerializedName("sender_img_url") var senderImgUrl: String? = null,
    @SerializedName("sender_img_path") var senderImgPath: String? = null,
    @SerializedName("receiver_img_url") var receiverImgUrl: String? = null,
    @SerializedName("receiver_img_path") var receiverImgPath: String? = null,
    @SerializedName("zipcode") var zipcode: String = "",
    @SerializedName("service") var serviceId: Int = 0,
    @SerializedName("size") var sizeId: Int = 0,
    @SerializedName("delivery_fee") var deliveryFee: Double = .0,
    @SerializedName("carton_fee") var cartonFee: Double? = .0,
    @SerializedName("cod_fee") var codFee: Double? = .0,
    @SerializedName("cod_amount") var codAmount: Double? = .0,
    var isOfflineData: Boolean = false,
    var serviceName: String = "",
    var sizeName: String = "",

    /**
     * for read offline data, group by receipt (come from entity, not API)
     */
    @SerializedName("receipt_code") var receiptCode: String = "",
    /**
     * set true, if this tracking add to pickup list but not include in pickup-task-tracking
     */
    @SerializedName("is_extra") var isExtra: Boolean = false

) : Serializable {
    fun toEntity(taskId: String, receiptCode: String, createdAt: Long): PickupSubmitTrackingEntity {
        val entity = unserializeTo(PickupSubmitTrackingEntity::class.java) { annotation, _, _ ->
            when (annotation) {
                "task_id" -> taskId
                "created_at" -> createdAt
                else -> null
            }
        }
        entity.receiptCode = receiptCode
        return entity
    }
}