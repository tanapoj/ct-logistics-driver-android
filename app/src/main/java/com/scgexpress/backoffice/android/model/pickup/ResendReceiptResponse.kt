package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName

data class ResendReceiptResponse(
        @SerializedName("status") var status: String,
        @SerializedName("receipt_id") var receiptId: String
) {
    fun isSuccess() = status == "resend"
}