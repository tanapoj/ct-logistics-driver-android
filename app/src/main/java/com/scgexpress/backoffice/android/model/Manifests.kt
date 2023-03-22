package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class Manifests(
        @SerializedName("manifest") val manifestList: ArrayList<Manifest> = arrayListOf(),
        @SerializedName("bookings_info") val newBookingList: ArrayList<BookingInfo> = arrayListOf()
)