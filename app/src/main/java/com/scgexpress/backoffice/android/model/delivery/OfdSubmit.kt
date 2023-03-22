package com.scgexpress.backoffice.android.model.delivery

import com.google.gson.annotations.SerializedName

data class OfdSubmit(
    @SerializedName("tracking_codes") var trackingCodeList: List<String>,
    @SerializedName("ofd_code") var ofdCode: String,
    @SerializedName("submit_at") var submitAt: String
)