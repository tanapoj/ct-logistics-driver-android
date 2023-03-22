package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("accessToken") var accessToken: String = "",
        @SerializedName("user_id") val id: String = "",
        @SerializedName("username") val username: String = "",
        @SerializedName("personal_id") val personalId: String = "",
        @SerializedName("name") val name: String = "",
        @SerializedName("application") val application: String = "",
        @SerializedName("branch_id") val branchId: String = "",
        @SerializedName("branch_code") val branchCode: String = ""
)

data class Identity(
        @SerializedName("IdentityId") var identityId: String = "",
        @SerializedName("Token") val token: String = ""
)