package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName

data class PickupTaskAcceptionResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("status") var status: String? = null
)