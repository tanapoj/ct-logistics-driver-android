package com.scgexpress.backoffice.android.ui.delivery.ofd

data class OfdModel(
        val id: String,
        val status: OfdStatus,
        val date: String,

        val pickupRemain: Int,
        val pickupPicked: Int,
        val pickupTotal: Int,

        val deliveryRemain: Int,
        val deliveryDelivered: Int,
        val deliveryRetention: Int,
        val deliveryTotal: Int

)

enum class OfdStatus {
    COMPLETED,
    INCOMPLETE
}