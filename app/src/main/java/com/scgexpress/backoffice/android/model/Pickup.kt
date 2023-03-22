package com.scgexpress.backoffice.android.model

import com.scgexpress.backoffice.android.common.Utils

data class Pickup(
        var userId: String = "",
        var groupId: String = "",
        var customerId: String = "",
        var assignmentId: String = "",
        var bookingId: String = "",
        var trackingCode: String = "",
        var services: String = "",
        var sizes: String = "",
        var zipCode: String = "",
        var deliveryFee: Double = 0.0,
        var serviceCharge: Double = 0.0,
        var codAmount: Double = 0.0,
        var codFee: Double = 0.0,
        var timestamp: Long = Utils.getCurrentTimestamp(),
        var payment: String = "",
        var tel: String = "",
        var email: String = "",
        var save: Boolean = false,
        var sync: Boolean = false,
        var receiptCode: String = "",
        var receiptId: String = "",
        var photoSenderFilePath: String? = null,
        var photoRecipientFilePath: String? = null,
        var photoSenderUrl: String? = null,
        var photoRecipientUrl: String? = null,
        var idParcelSizing: Int? = null,
        var parcelSizingPrice: Double? = .0,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
)