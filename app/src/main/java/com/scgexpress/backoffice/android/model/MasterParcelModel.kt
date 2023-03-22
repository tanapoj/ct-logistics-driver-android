package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class MasterParcelModel(
        @SerializedName("id") val id: String? = "",
        @SerializedName("name") val name: String? = "",
        @SerializedName("description") val description: String? = "")