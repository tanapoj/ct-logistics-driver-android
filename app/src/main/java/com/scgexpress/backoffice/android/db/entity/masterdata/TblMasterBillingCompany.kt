package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_master_billing_company")
data class TblMasterBillingCompany(
    @PrimaryKey @Json(name = "company_id") @SerializedName("company_id") @ColumnInfo(name = "company_id") var companyId: Int = 0,
    @Json(name = "company_name") @SerializedName("company_name") @ColumnInfo(name = "company_name") var companyName: String? = "",
    @Json(name = "company_branch") @SerializedName("company_branch") @ColumnInfo(name = "company_branch") var companyBranch: String? = "",
    @Json(name = "company_tax_number") @SerializedName("company_tax_number") @ColumnInfo(name = "company_tax_number") var companyTaxNumber: String? = "",
    @Json(name = "company_address") @SerializedName("company_address") @ColumnInfo(name = "company_address") var companyAddress: String? = "",
    @Json(name = "company_district") @SerializedName("company_district") @ColumnInfo(name = "company_district") var companyDistrict: String? = "",
    @Json(name = "company_province") @SerializedName("company_province") @ColumnInfo(name = "company_province") var companyProvince: String? = "",
    @Json(name = "company_zipcode") @SerializedName("company_zipcode") @ColumnInfo(name = "company_zipcode") var companyZipcode: String? = "",
    @Json(name = "company_tel") @SerializedName("company_tel") @ColumnInfo(name = "company_tel") var companyTel: String? = "",
    @Json(name = "remark") @SerializedName("remark") @ColumnInfo(name = "remark") var remark: String? = ""
) {
    @Ignore
    constructor() : this(0)
}