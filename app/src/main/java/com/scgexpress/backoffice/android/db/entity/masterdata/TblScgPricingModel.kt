package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_pricing_model")
data class TblScgPricingModel(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "id_price_tier") @SerializedName("id_price_tier") @ColumnInfo(name = "id_price_tier") var idPriceTier: Int = 0,
    @Json(name = "id_region_diff") @SerializedName("id_region_diff") @ColumnInfo(name = "id_region_diff") var idRegionDiff: Int = 0,
    @Json(name = "id_parcel_properties") @SerializedName("id_parcel_properties") @ColumnInfo(name = "id_parcel_properties") var idParcelProperties: Int = 0,
    @Json(name = "freight_fare") @SerializedName("freight_fare") @ColumnInfo(name = "freight_fare") var freightFare: String? = "0.000",
    @Json(name = "freight_fare_agent") @SerializedName("freight_fare_agent") @ColumnInfo(name = "freight_fare_agent") var freightFareAgent: String? = "0.000",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}