package com.scgexpress.backoffice.android.model.delivery

import com.google.gson.annotations.SerializedName

data class SentSubmit(
    @SerializedName("tracking_codes") var trackingCodeList: List<SentSubmitTracking>,
    @SerializedName("ofd_code") var ofdCode: String,
    @SerializedName("payment_method") var paymentMethod: Int,
    @SerializedName("recipient_signed_name") var recipientSignedName: String,
    @SerializedName("recipient_signature_img_url") var recipientSignatureImageUrl: String,
    @SerializedName("submit_at") var submitAt: String
)

data class SentSubmitTracking(
    @SerializedName("tracking_code") var trackingCode: String,
    @SerializedName("cod_amount") var codAmount: Double,
    @SerializedName("cod_receive") var codReceive: Double,
    @SerializedName("product_img_url") var productImageUrl: String
)