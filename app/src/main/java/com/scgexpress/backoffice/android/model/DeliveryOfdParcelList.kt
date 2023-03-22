package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeliveryOfdParcel(
        @SerializedName("tracking_id") var trackingId: String = "",
        @SerializedName("status_id") var statusCode: String = "7",
        @SerializedName("cod_collected") var codCollected: Double = 0.0,
        @SerializedName("datetime_input") var datetimeInput: String = "",
        @SerializedName("signature_image") var signatureImage: String = "",
        @SerializedName("recipient_name") var recipientName: String = "",
        @SerializedName("ofd_note") var ofdNote: String = "",
        @SerializedName("lat") var latitude: Double = 0.0,
        @SerializedName("lng") var longitude: Double = 0.0
) : Serializable

data class DeliveryOfdParcelList(
        @SerializedName("items") var items: ArrayList<DeliveryOfdParcel> = arrayListOf()
) : Serializable

data class DeliveryOfdParcelResponse(
        @SerializedName("tracking_id") var trackingId: String = "",
        @SerializedName("status") var status: String = "",
        @SerializedName("parcels_info") var parcelsInfo: DeliveryOfdParcelsInfo = DeliveryOfdParcelsInfo()
) : Serializable

data class DeliveryOfdParcelsInfo(
        @SerializedName("sender_code") var senderCode: String = "",
        @SerializedName("sender_name") var senderName: String = "",
        @SerializedName("cod_amount") var codAmount: Double = 0.0,
        @SerializedName("order_date") var userOrderDate: String = ""
) : Serializable

data class DeliveryOfdParcelResponseList(
        @SerializedName("items") var items: ArrayList<DeliveryOfdParcelResponse> = arrayListOf()
) : Serializable