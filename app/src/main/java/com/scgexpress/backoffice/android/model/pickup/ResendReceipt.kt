package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName

data class ResendReceipt(
    @SerializedName("action") var action: String? = "",
    @SerializedName("receipt_id") var receiptId: String? = "",
    @SerializedName("recript_contact_tel") var tel: String? = "",
    @SerializedName("recript_contact_email") var email: String? = ""
)