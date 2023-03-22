package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class MasterDataVersion(
        @SerializedName("filename") val filename: String? = "",
        @SerializedName("bucketname") val bucketname: String? = ""
)