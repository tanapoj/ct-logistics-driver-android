package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

data class BookingSummaryItem(
        val id: String,
        val trackingCode: String,
        val zipCode: String,
        val size: String,
        val price: String
)

data class BookingSummaryTitle(
        val id: String
)