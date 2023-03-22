package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OfdItemReStatus(
        @SerializedName("id_status") var idStatus: String? = "",
        @SerializedName("id_sub_cancel_reason") var idSubCancelReason: String? = "",
        @SerializedName("tracking_number") var trackingNumber: String? = "",
        @SerializedName("id_cancel_reason") var idCancelReason: String? = "",
        @SerializedName("note") var note: String? = ""
) : Serializable