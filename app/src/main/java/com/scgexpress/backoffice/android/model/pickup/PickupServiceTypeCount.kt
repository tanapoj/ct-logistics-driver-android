package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PickupServiceTypeCount(
    @SerializedName("normal") val normal: Int? = 0,
    @SerializedName("chilled") val chilled: Int? = 0,
    @SerializedName("frozen") val frozen: Int? = 0
) : Serializable