package com.scgexpress.backoffice.android.ui.delivery.booking

data class NewBookingModel(
        val id: String,
        val status: BookingStatus,
        val address: String,
        val date: String,
        val tel: String
)

enum class BookingStatus{
    INCOMING
}