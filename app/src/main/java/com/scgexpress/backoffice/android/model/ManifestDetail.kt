package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class ManifestDetail(
        @SerializedName("tracking_info") val trackingList: List<TrackingInfo> = listOf(),
        @SerializedName("bookings_info") val bookingList: List<BookingInfo> = listOf()
)