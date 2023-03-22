package com.scgexpress.backoffice.android.db.entity.delivery

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.model.delivery.Retention

@Entity(tableName = "delivery_task", primaryKeys = ["id"])
data class DeliveryTaskEntity(

    @SerializedName("id")
    @ColumnInfo(name = "id") var id: String = "",

    @SerializedName("tracking_code")
    @ColumnInfo(name = "tracking_code") var trackingCode: String = "",

    @SerializedName("order_code")
    @ColumnInfo(name = "order_code") var orderCode: String = "",

    @SerializedName("service")
    @ColumnInfo(name = "service") var service: Int = -1,

    @SerializedName("size")
    @ColumnInfo(name = "size") var size: Int = -1,

    @SerializedName("recipient_code")
    @ColumnInfo(name = "recipient_code") var recipientCode: String = "",

    @SerializedName("recipient_name")
    @ColumnInfo(name = "recipient_name") var recipientName: String = "",

    @SerializedName("recipient_tel")
    @ColumnInfo(name = "recipient_tel") var recipientTel: String = "",

    @SerializedName("shipper_code")
    @ColumnInfo(name = "shipper_code") var shipperCode: String = "",

    @SerializedName("shipper_name")
    @ColumnInfo(name = "shipper_name") var shipperName: String = "",

    @SerializedName("sender_code")
    @ColumnInfo(name = "sender_code") var senderCode: String = "",

    @SerializedName("sender_name")
    @ColumnInfo(name = "sender_name") var senderName: String = "",

    @SerializedName("sender_tel")
    @ColumnInfo(name = "sender_tel") var senderTel: String = "",

    @SerializedName("recipient_location")
    @Embedded var recipientLocation: Location = Location(),

    @SerializedName("remark")
    @ColumnInfo(name = "remark") var remark: String = "",

    @SerializedName("product_img_url")
    @ColumnInfo(name = "product_img_url") var productImgUrl: String? = null,

    @SerializedName("product_img_path")
    @ColumnInfo(name = "product_img_path") var productImgPath: String? = null,

    @SerializedName("recipient_signature_img_url")
    @ColumnInfo(name = "recipient_signature_img_url") var recipientSignatureImgUrl: String? = null,

    @SerializedName("recipient_signature_img_path")
    @ColumnInfo(name = "recipient_signature_img_path") var recipientSignatureImgPath: String? = null,

    @SerializedName("recipient_signed_name")
    @ColumnInfo(name = "recipient_signed_name") var recipientSignedName: String = "",

    @SerializedName("delivery_status")
    @ColumnInfo(name = "delivery_status") var deliveryStatus: String = "",

    @SerializedName("product_reverse_status")
    @ColumnInfo(name = "product_reverse_status") var productReverseStatus: String = "",

    @SerializedName("retention")
    @Embedded var retention: Retention = Retention(),

    @SerializedName("cod_fee")
    @ColumnInfo(name = "cod_fee") var codFee: Double? = .0,

    @SerializedName("cod_amount")
    @ColumnInfo(name = "cod_amount") var codAmount: Double? = .0,

    @SerializedName("cod_currency_unit")
    @ColumnInfo(name = "cod_currency_unit") var codCurrencyUnit: String? = null,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at") var createdAt: String = "",

    @SerializedName("delivery_status_change_at")
    @ColumnInfo(name = "delivery_status_change_at") var deliveryStatusChangeAt: String? = null,

    @SerializedName("delivery_at")
    @ColumnInfo(name = "delivery_at") var deliveryAt: String = ""
) {
    fun toModel(): DeliveryTask = unserializeTo(DeliveryTask::class.java)
}