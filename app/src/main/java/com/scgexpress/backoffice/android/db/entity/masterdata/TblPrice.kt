package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_price")
data class TblPrice(
    @PrimaryKey @Json(name = "price_id") @SerializedName("price_id") @ColumnInfo(name = "price_id") var priceId: Int = 0,
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "product_id") @SerializedName("product_id") @ColumnInfo(name = "product_id") var productId: Int = 0,
    @Json(name = "price_tier_id") @SerializedName("price_tier_id") @ColumnInfo(name = "price_tier_id") var priceTierId: Int = 0,
    @Json(name = "organization_id") @SerializedName("organization_id") @ColumnInfo(name = "organization_id") var organizationId: Int = 0,
    @Json(name = "currency") @SerializedName("currency") @ColumnInfo(name = "currency") var currency: String? = "THB",
    @Json(name = "amount") @SerializedName("amount") @ColumnInfo(name = "amount") var amount: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}