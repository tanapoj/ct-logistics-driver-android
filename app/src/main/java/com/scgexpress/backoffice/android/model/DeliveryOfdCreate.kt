package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class DeliveryOfdCreate(
        @SerializedName("code") var code: String = "",
        @SerializedName("branch_id") val branchID: String = "",
        @SerializedName("branch_code") val branchCode: String = "",
        @SerializedName("input_create_datetime") val inputCreateDatetime: String = "",
        @SerializedName("vehicle") val vehicle: String = ""
)

data class DeliveryOfdCreateList(
        @SerializedName("manifests") var manifests: ArrayList<DeliveryOfdCreate> = arrayListOf()
)