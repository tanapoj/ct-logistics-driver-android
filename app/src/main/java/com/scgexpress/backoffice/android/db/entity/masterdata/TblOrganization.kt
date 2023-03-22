package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "tbl_organization")
data class TblOrganization(
    @PrimaryKey @Json(name = "organization_id") @SerializedName("organization_id") @ColumnInfo(name = "organization_id") var organizationId: Int = 0,
    @Json(name = "name") @SerializedName("name") @ColumnInfo(name = "name") var name: String? = "",
    @Json(name = "customer_code") @SerializedName("customer_code") @ColumnInfo(name = "customer_code") var customerCode: String? = "",
    @Json(name = "express_member_id") @SerializedName("express_member_id") @ColumnInfo(name = "express_member_id") var expressMemberId: Int = 0,
    @Json(name = "tracking_number_group") @SerializedName("tracking_number_group") @ColumnInfo(name = "tracking_number_group") var trackingNumberGroup: Int = 1,
    @Json(name = "organization_type_id") @SerializedName("organization_type_id") @ColumnInfo(name = "organization_type_id") var organizationTypeId: Int = 196,
    @Json(name = "customer_type_id") @SerializedName("customer_type_id") @ColumnInfo(name = "customer_type_id") var customerTypeId: Int = 1,
    @Json(name = "affiliation_id") @SerializedName("affiliation_id") @ColumnInfo(name = "affiliation_id") var affiliationId: Int = 2,
    @Json(name = "price_tier_id") @SerializedName("price_tier_id") @ColumnInfo(name = "price_tier_id") var priceTierId: Int = 0,
    @Json(name = "cod_tier_id") @SerializedName("cod_tier_id") @ColumnInfo(name = "cod_tier_id") var codTierId: Int = 0,
    @Json(name = "cod_allowed") @SerializedName("cod_allowed") @ColumnInfo(name = "cod_allowed") var codAllowed: String = "Y",
    @Json(name = "province_id") @SerializedName("province_id") @ColumnInfo(name = "province_id") var provinceId: Int = 1,
    @Json(name = "country_id") @SerializedName("country_id") @ColumnInfo(name = "country_id") var countryId: Int = 173,
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "title") @SerializedName("title") @ColumnInfo(name = "title") var title: String? = "",
    @Json(name = "description_short") @SerializedName("description_short") @ColumnInfo(name = "description_short") var descriptionShort: String? = "",
    @Json(name = "description_long") @SerializedName("description_long") @ColumnInfo(name = "description_long") var descriptionLong: String? = "",
    @Json(name = "json_media") @SerializedName("json_media") @ColumnInfo(name = "json_media") var jsonMedia: String? = "",
    @Json(name = "json_info") @SerializedName("json_info") @ColumnInfo(name = "json_info") var jsonInfo: String? = "",
    @Json(name = "address_1") @SerializedName("address_1") @ColumnInfo(name = "address_1") var address1: String? = "",
    @Json(name = "address_2") @SerializedName("address_2") @ColumnInfo(name = "address_2") var address2: String? = "",
    @Json(name = "postalcode") @SerializedName("postalcode") @ColumnInfo(name = "postalcode") var postalcode: String? = "",
    @Json(name = "email") @SerializedName("email") @ColumnInfo(name = "email") var email: String? = "",
    @Json(name = "phone") @SerializedName("phone") @ColumnInfo(name = "phone") var phone: String? = "",
    @Json(name = "fax") @SerializedName("fax") @ColumnInfo(name = "fax") var fax: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) : Serializable {
    @Ignore
    constructor() : this(0)
}