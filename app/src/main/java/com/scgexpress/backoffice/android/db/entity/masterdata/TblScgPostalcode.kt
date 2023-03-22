package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_postalCode", primaryKeys = arrayOf("id", "postal_code"))
data class TblScgPostalcode(
    @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "postal_code") @SerializedName("postal_code") @ColumnInfo(name = "postal_code") var postalCode: String = "",
    @Json(name = "district") @SerializedName("district") @ColumnInfo(name = "district") var district: String? = "",
    @Json(name = "province") @SerializedName("province") @ColumnInfo(name = "province") var province: String? = "",
    @Json(name = "remark") @SerializedName("remark") @ColumnInfo(name = "remark") var remark: String? = "",
    @Json(name = "extra_charge") @SerializedName("extra_charge") @ColumnInfo(name = "extra_charge") var extraCharge: String? = "0.00",
    @Json(name = "remote_area") @SerializedName("remote_area") @ColumnInfo(name = "remote_area") var remoteArea: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}