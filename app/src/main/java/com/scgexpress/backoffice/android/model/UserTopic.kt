package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class UserTopic(
    @SerializedName("accessToken") var accessToken: String = "",
    @SerializedName("user_id") val id: String = "",
    @SerializedName("username") val username: String = "",
    @SerializedName("name") val name: String = ""
)