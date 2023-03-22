package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class TrackingNoByBooking(
        @SerializedName("id") var id: String? = "",
        @SerializedName("tracking_number") var trackingNumber: String? = "",
        @SerializedName("id_parcel") var idParcel: String? = "",
        @SerializedName("id_status") var idStatus: String? = "",
        @SerializedName("booking_id") var bookingId: String? = "",
        @SerializedName("id_manifest_sheet") var idManifestSheet: String? = "",
        @SerializedName("organization_id") var organizationId: String? = "",
        @SerializedName("id_receipt") var idReceipt: String? = "",
        @SerializedName("id_receipt_cod") var idReceiptCod: String? = ""
)