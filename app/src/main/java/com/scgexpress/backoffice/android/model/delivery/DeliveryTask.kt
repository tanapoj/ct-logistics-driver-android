package com.scgexpress.backoffice.android.model.delivery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.db.entity.delivery.DeliveryTaskEntity
import com.scgexpress.backoffice.android.model.Location
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class DeliveryTask(
    @SerializedName("id") var id: String = "",
    @SerializedName("tracking_code") var trackingCode: String = "",
    @SerializedName("order_code") var orderCode: String = "",
    @SerializedName("service") var service: Int = -1,
    @SerializedName("size") var size: Int = -1,
    @SerializedName("recipient_code") var recipientCode: String = "",
    @SerializedName("recipient_name") var recipientName: String = "",
    @SerializedName("recipient_tel") var recipientTel: String = "",
    @SerializedName("shipper_code") var shipperCode: String = "",
    @SerializedName("shipper_name") var shipperName: String = "",
    @SerializedName("sender_code") var senderCode: String = "",
    @SerializedName("sender_name") var senderName: String = "",
    @SerializedName("sender_tel") var senderTel: String = "",
    @SerializedName("recipient_location") var recipientLocation: Location = Location(),
    @SerializedName("remark") var remark: String = "",
    @SerializedName("product_img_url") var productImgUrl: String? = null,
    @SerializedName("product_img_path") var productImgPath: String? = null,
    @SerializedName("recipient_signature_img_url") var recipientSignatureImgUrl: String? = null,
    @SerializedName("recipient_signature_img_path") var recipientSignatureImgPath: String? = null,
    @SerializedName("recipient_signed_name") var recipientSignedName: String = "",
    @SerializedName("delivery_status") var deliveryStatus: String = "",
    @SerializedName("product_reverse_status") var productReverseStatus: String = "",
    @SerializedName("retention") var retention: Retention = Retention(),
    @SerializedName("cod_fee") var codFee: Double? = .0,
    @SerializedName("cod_amount") var codAmount: Double? = .0,
    @SerializedName("cod_currency_unit") var codCurrencyUnit: String? = null,
    @SerializedName("created_at") var createdAt: String = "",
    @SerializedName("delivery_status_change_at") var deliveryStatusChangeAt: String? = null,
    @SerializedName("delivery_at") var deliveryAt: String = "",
    var isOfflineData: Boolean = false,
    var serviceName: String = "",
    var sizeName: String = ""
) : Serializable {
    fun toEntity(): DeliveryTaskEntity = unserializeTo(DeliveryTaskEntity::class.java)
}

data class DeliveryTaskList(
    @SerializedName("tasks") var items: List<DeliveryTask>? = null
) : Serializable

enum class Status {
    IN_PROGRESS, DELIVERED, RETENTION
}

enum class ReverseStatus {
    NONE, HAS_ITEM_TO_PICKUP, RECEIVED
}