package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class DeliveryOfdSent(
        @SerializedName("tracking") var tracking: String = "",
        @SerializedName("sender") var sender: String = "",
        @SerializedName("cod") var cod: Double = 0.0
)

data class DeliveryOfdSentTitle(
        val id: String
)