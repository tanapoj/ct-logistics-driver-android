package com.scgexpress.backoffice.android.model.tracking

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.db.entity.pickup.PickupTrackingEntity
import java.io.Serializable

data class Tracking(
        @SerializedName("tracking") var tracking: String = "",
        @SerializedName("order_id") var orderId: String = "",
        @SerializedName("pinbox") var pinbox: String? = null,
        @SerializedName("service") var service: Int? = null,
        @SerializedName("size") var size: Int? = null,
        @SerializedName("zipcode") var zipcode: String? = null,

        /**
         * set true, if data loaded from local db
         */
        var isOfflineData: Boolean = false,

        /**
         * set true, if this tracking add to pickup list but not include in pickup-task-tracking
         */
        var isExtra: Boolean = false
) : Serializable {
    fun toEntity(taskId: String): PickupTrackingEntity =
            unserializeTo(PickupTrackingEntity::class.java) { annotation, _, _ ->
                when (annotation) {
                    "task_id" -> taskId
                    else -> null
                }
            }
}