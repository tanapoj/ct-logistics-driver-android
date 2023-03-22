package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class MetaBooking(
        @SerializedName("bookingID") val bookingID: String = "",
        @SerializedName("assignmentID") val assignmentID: String = "",
        @SerializedName("userID") val userID: String = "",
        @SerializedName("customerID") val customerID: String = "",
        @SerializedName("customerCode") val customerCode: String = "",
        @SerializedName("customerName") val customerName: String = "",
        @SerializedName("description") val description: String = "",
        @SerializedName("type") val type: String = ""
)