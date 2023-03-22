package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScanningTracking(

    @SerializedName("task_id") var taskId: String = "",

    @SerializedName("tracking") var tracking: String = "",
    @SerializedName("sender_img_url") var senderImgUrl: String? = null,
    @SerializedName("sender_img_path") var senderImgPath: String? = null,
    @SerializedName("receiver_img_url") var receiverImgUrl: String? = null,
    @SerializedName("receiver_img_path") var receiverImgPath: String? = null,
    @SerializedName("zipcode") var zipcode: String? = "",
    @SerializedName("service_id") var serviceId: Int? = 0,
    @SerializedName("size_id") var sizeId: Int? = 0,
    @SerializedName("delivery_fee") var deliveryFee: Double = .0,
    @SerializedName("carton_fee") var cartonFee: Double? = .0,
    @SerializedName("cod_fee") var codFee: Double? = .0,
    @SerializedName("cod_amount") var codAmount: Double? = .0,
    @SerializedName("receipt_code") var receiptCode: String = "",
    @SerializedName("scan_at") var scanAt: Long = 0,
    var serviceName: String = "",
    var sizeName: String = "",

//tracking
    @SerializedName("order_id") var orderId: String = "",
    @SerializedName("pinbox") var pinbox: String? = null,
    @SerializedName("is_extra") var isExtra: Boolean = false
) : Serializable
