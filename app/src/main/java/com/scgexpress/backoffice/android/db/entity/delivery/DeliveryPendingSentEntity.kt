package com.scgexpress.backoffice.android.db.entity.delivery

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "delivery_pending_sent")
data class DeliveryPendingSentEntity(

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0,

    @SerializedName("ofd_code")
    @ColumnInfo(name = "ofd_code") var ofdCode: String = "",

    @SerializedName("payment_method")
    @ColumnInfo(name = "payment_method") var paymentMethod: Int = 0,

    @SerializedName("recipient_signed_name")
    @ColumnInfo(name = "recipient_signed_name") var recipientSignedName: String = "",

    @SerializedName("recipient_signature_img_url")
    @ColumnInfo(name = "recipient_signature_img_url") var recipientSignatureImageUrl: String? = null,

    @SerializedName("recipient_signature_img_path")
    @ColumnInfo(name = "recipient_signature_img_path") var recipientSignatureImagePath: String? = null,

    @SerializedName("submit_at")
    @ColumnInfo(name = "submit_at") var submitAt: Long = 0,

    @SerializedName("sync")
    @ColumnInfo(name = "sync") var sync: Boolean = false
)