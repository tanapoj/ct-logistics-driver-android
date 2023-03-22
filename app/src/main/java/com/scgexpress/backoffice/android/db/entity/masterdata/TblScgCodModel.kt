package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_cod_model")
data class TblScgCodModel(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "id_cod_tier") @SerializedName("id_cod_tier") @ColumnInfo(name = "id_cod_tier") var idCodTier: Int = 0,
    @Json(name = "id_payment_type") @SerializedName("id_payment_type") @ColumnInfo(name = "id_payment_type") var idPaymentType: Int = 0,
    @Json(name = "cod_amount_min") @SerializedName("cod_amount_min") @ColumnInfo(name = "cod_amount_min") var codAmountMin: String? = "",
    @Json(name = "cod_fee_min") @SerializedName("cod_fee_min") @ColumnInfo(name = "cod_fee_min") var codFeeMin: String? = "",
    @Json(name = "cod_amount_max") @SerializedName("cod_amount_max") @ColumnInfo(name = "cod_amount_max") var codAmountMax: String? = "",
    @Json(name = "cod_fee_max") @SerializedName("cod_fee_max") @ColumnInfo(name = "cod_fee_max") var codFeeMax: String? = "",
    @Json(name = "cod_price_percent") @SerializedName("cod_price_percent") @ColumnInfo(name = "cod_price_percent") var codPricePercent: String? = "",
    @Json(name = "cod_price_min") @SerializedName("cod_price_min") @ColumnInfo(name = "cod_price_min") var codPriceMin: String? = "",
    @Json(name = "cod_price_max") @SerializedName("cod_price_max") @ColumnInfo(name = "cod_price_max") var codPriceMax: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}