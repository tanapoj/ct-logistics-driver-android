package com.scgexpress.backoffice.android.db.entity.delivery

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.delivery.SentSubmit
import com.scgexpress.backoffice.android.model.delivery.SentSubmitTracking


@Entity(tableName = "delivery_sent_tracking", primaryKeys = ["tracking_code"])
data class DeliverySentTrackingEntity(

    @SerializedName("tracking_code")
    @ColumnInfo(name = "tracking_code") var trackingCode: String = "",

    //FK to pending_sent(id)
    @ColumnInfo(name = "pending_id") var pendingId: Int = 0,

    @SerializedName("cod_amount")
    @ColumnInfo(name = "cod_amount") var codAmount: Double? = null,

    @SerializedName("cod_receive")
    @ColumnInfo(name = "cod_receive") var codReceive: Double? = null,

    @SerializedName("product_img_url")
    @ColumnInfo(name = "product_img_url") var productImageUrl: String? = null,

    @SerializedName("product_img_path")
    @ColumnInfo(name = "product_img_path") var productImagePath: String? = null
) {
    fun toModel(): SentSubmitTracking = unserializeTo(SentSubmitTracking::class.java)
}