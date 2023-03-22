package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OfdItemInfoList(
        @SerializedName("ofdItemInfoList") var item: List<Any> = arrayListOf(),
        @SerializedName("position") var position: Int = 0) : Serializable