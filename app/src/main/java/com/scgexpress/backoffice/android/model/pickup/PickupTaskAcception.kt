package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName

data class PickupTaskAcception(
    @SerializedName("action") var action: String? = null,
    @SerializedName("reason_code") var reasonCode: Int? = null,
    @SerializedName("reason_msg") var reasonMsg: String? = null
)