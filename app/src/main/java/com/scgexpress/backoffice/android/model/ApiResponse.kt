package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
        @SerializedName("message") val message: String? = ""
)