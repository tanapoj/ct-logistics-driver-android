package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName

data class SubmitReceiptResponse(
        @SerializedName("status") var status: String,
        @SerializedName("receipt_id") var receiptId: String,
        @SerializedName("fields") var fields: SubmitReceiptErrorField?,
        @SerializedName("msg") var message: String?
) {
    fun isSuccess() = status == "TASK_SUMMARY_SUBMITED"
}

data class SubmitReceiptErrorField(
        @SerializedName("field") var field: String,
        @SerializedName("msg") var message: String
)