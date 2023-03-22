package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.db.entity.pickup.PickupReceiptEntity
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking
import java.io.Serializable

data class Receipt(
    @SerializedName("receipt_id") var receiptId: String = "",
    @SerializedName("receipt_code") var receiptCode: String = "",
    @SerializedName("submit_trackings") var submitTrackings: List<SubmitTracking> = mutableListOf(),
    var isOfflineData: Boolean = false
) : Serializable {
    fun toEntity(taskId: String, createdAt: Long): PickupReceiptEntity =
        unserializeTo(PickupReceiptEntity::class.java) { annotation, _, _ ->
            when (annotation) {
                "task_id" -> taskId
                "created_at" -> createdAt
                else -> null
            }
        }
}

