package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class DeliveryOfdScan(
        @SerializedName("tracking") var tracking: String = "",
        @SerializedName("sender") var sender: String = "",
        @SerializedName("cod") var cod: Double = 0.0,
        @SerializedName("order_date") var orderDate: String = "",
        @SerializedName("status") var status: String = ""
)

data class DeliveryOfdScanTitle(
        val id: String
)