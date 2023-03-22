package com.scgexpress.backoffice.android.ui.pickup.summary

data class PickupSummaryItem(
        val id: String,
        val trackingCode: String,
        val zipCode: String,
        val size: String,
        val price: String
)

data class PickupSummaryTitle(
        val id: String
)

data class PickupPaymentMethod(
        val id: String,
        val title: String
)