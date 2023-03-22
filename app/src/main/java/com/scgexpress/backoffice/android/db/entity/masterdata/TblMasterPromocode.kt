package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_master_promocode")
data class TblMasterPromocode(
    @PrimaryKey @Json(name = "promocode_id") @SerializedName("promocode_id") @ColumnInfo(name = "promocode_id") var promocodeId: Int = 0,
    @Json(name = "promo_code") @SerializedName("promo_code") @ColumnInfo(name = "promo_code") var promoCode: String? = "",
    @Json(name = "start_date") @SerializedName("start_date") @ColumnInfo(name = "start_date") var startDate: String? = "",
    @Json(name = "expire_date") @SerializedName("expire_date") @ColumnInfo(name = "expire_date") var expireDate: String? = "",
    @Json(name = "flagance") @SerializedName("flagance") @ColumnInfo(name = "flagance") var flagance: String? = "N",
    @Json(name = "max_discount") @SerializedName("max_discount") @ColumnInfo(name = "max_discount") var maxDiscount: Int = 0,
    @Json(name = "discount") @SerializedName("discount") @ColumnInfo(name = "discount") var discount: Int = 0,
    @Json(name = "max_use") @SerializedName("max_use") @ColumnInfo(name = "max_use") var maxUse: Int = 0,
    @Json(name = "curr_use") @SerializedName("curr_use") @ColumnInfo(name = "curr_use") var currUse: Int = 0
) {
    @Ignore
    constructor() : this(0)
}