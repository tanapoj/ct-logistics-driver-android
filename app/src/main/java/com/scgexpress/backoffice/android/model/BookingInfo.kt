package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BookingInfo(
        @SerializedName("assign_id") val assignID: String = "",
        @SerializedName("booking_id") val bookingID: String = "",
        @SerializedName("personal_id") val personalID: String = "",
        @SerializedName("saledriver_id") val saledriverID: String = "",
        @SerializedName("saledriver_name") val saledriverName: String = "",
        @SerializedName("status_from_callcenter") val statusFromCallcenter: String = "",
        @SerializedName("assign_status") val assignStatus: String = "",
        @SerializedName("permission") val permission: String = "",
        @SerializedName("date_created") val dateCreated: String = "",
        @SerializedName("OFD_code") val OfdCode: String = "",
        @SerializedName("sender_code") val senderCode: String = "",
        @SerializedName("sender_name") val senderName: String = "",
        @SerializedName("sender_tel") val senderTel: String = "",
        @SerializedName("sender_address") val senderAddress: String = "",
        @SerializedName("contact_address") val contactAddress: String = "",
        @SerializedName("success_push_status") val successPushStatus: String = "",
        @SerializedName("datetime_success_push_status") val datetimeSuccessPushStatus: String = "",
        @SerializedName("tel") val tel: String = "",
        @SerializedName("description") val description: String= "",
        @SerializedName("chilled_amount") val chilledAmount: String = "",
        @SerializedName("frozen_amount") val frozenAmount: String = "",
        @SerializedName("normal_amount") val normalAmount: String = "",
        @SerializedName("pickup_request_date") val pickupRequestDate: String = "",
        @SerializedName("pickup_request_time") val pickupRequestTime: String = "",
        @SerializedName("remark") val remark: String = "") : Serializable