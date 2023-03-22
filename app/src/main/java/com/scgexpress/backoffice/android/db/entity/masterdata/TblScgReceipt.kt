package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_receipt")
data class TblScgReceipt(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "status_sent") @SerializedName("status_sent") @ColumnInfo(name = "status_sent") var statusSent: String? = "N",
    @Json(name = "id_shipper") @SerializedName("id_shipper") @ColumnInfo(name = "id_shipper") var idShipper: Int = 0,
    @Json(name = "id_payment_type") @SerializedName("id_payment_type") @ColumnInfo(name = "id_payment_type") var idPaymentType: Int = 0,
    @Json(name = "receipt_tel") @SerializedName("receipt_tel") @ColumnInfo(name = "receipt_tel") var receiptTel: String? = "",
    @Json(name = "receipt_email") @SerializedName("receipt_email") @ColumnInfo(name = "receipt_email") var receiptEmail: String? = "",
    @Json(name = "receipt_path") @SerializedName("receipt_path") @ColumnInfo(name = "receipt_path") var receiptPath: String? = "",
    @Json(name = "id_created_user") @SerializedName("id_created_user") @ColumnInfo(name = "id_created_user") var idCreatedUser: Int = 0,
    @Json(name = "id_last_modified_user") @SerializedName("id_last_modified_user") @ColumnInfo(name = "id_last_modified_user") var idLastModifiedUser: Int = 0,
    @Json(name = "datetime_created") @SerializedName("datetime_created") @ColumnInfo(name = "datetime_created") var datetimeCreated: String? = "",
    @Json(name = "datetime_last_modified") @SerializedName("datetime_last_modified") @ColumnInfo(name = "datetime_last_modified") var datetimeLastModified: String? = "",
    @Json(name = "datetime_sent") @SerializedName("datetime_sent") @ColumnInfo(name = "datetime_sent") var datetimeSent: String? = ""
) {
    @Ignore
    constructor() : this(0)
}