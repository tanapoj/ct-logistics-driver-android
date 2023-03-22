package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_branch")
data class TblScgBranch(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "branch_id") @SerializedName("branch_id") @ColumnInfo(name = "branch_id") var branchId: String? = "",
    @Json(name = "branch_code") @SerializedName("branch_code") @ColumnInfo(name = "branch_code") var branchCode: String? = "",
    @Json(name = "branch_name") @SerializedName("branch_name") @ColumnInfo(name = "branch_name") var branchName: String? = "",
    @Json(name = "customer_code") @SerializedName("customer_code") @ColumnInfo(name = "customer_code") var customerCode: String? = "",
    @Json(name = "personal_id") @SerializedName("personal_id") @ColumnInfo(name = "personal_id") var personalId: String? = "",
    @Json(name = "region_id") @SerializedName("region_id") @ColumnInfo(name = "region_id") var regionId: Int = 0,
    @Json(name = "status_hub") @SerializedName("status_hub") @ColumnInfo(name = "status_hub") var statusHub: String? = "N",
    @Json(name = "status_order_admin") @SerializedName("status_order_admin") @ColumnInfo(name = "status_order_admin") var statusOrderAdmin: String? = "Y",
    @Json(name = "status_manifest") @SerializedName("status_manifest") @ColumnInfo(name = "status_manifest") var statusManifest: String? = "Y",
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "neko_center_code") @SerializedName("neko_center_code") @ColumnInfo(name = "neko_center_code") var nekoCenterCode: String? = "",
    @Json(name = "customer_name") @SerializedName("customer_name") @ColumnInfo(name = "customer_name") var customerName: String? = "",
    @Json(name = "customer_address") @SerializedName("customer_address") @ColumnInfo(name = "customer_address") var customerAddress: String? = "",
    @Json(name = "customer_amphure") @SerializedName("customer_amphure") @ColumnInfo(name = "customer_amphure") var customerAmphure: String? = "",
    @Json(name = "customer_province") @SerializedName("customer_province") @ColumnInfo(name = "customer_province") var customerProvince: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}