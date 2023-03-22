package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_auth_users")
data class TblAuthUsers(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "profilesId") @SerializedName("profilesId") @ColumnInfo(name = "profilesId") var profilesid: Int = 4,
    @Json(name = "organization_id") @SerializedName("organization_id") @ColumnInfo(name = "organization_id") var organizationId: Int = 2,
    @Json(name = "personal_id") @SerializedName("personal_id") @ColumnInfo(name = "personal_id") var personalId: String? = "",
    @Json(name = "branch_id") @SerializedName("branch_id") @ColumnInfo(name = "branch_id") var branchId: Int = 0,
    @Json(name = "active") @SerializedName("active") @ColumnInfo(name = "active") var active: String? = "Y",
    @Json(name = "suspended") @SerializedName("suspended") @ColumnInfo(name = "suspended") var suspended: String? = "N",
    @Json(name = "banned") @SerializedName("banned") @ColumnInfo(name = "banned") var banned: String? = "N",
    @Json(name = "mustChangePassword") @SerializedName("mustChangePassword") @ColumnInfo(name = "mustChangePassword") var mustchangepassword: String? = "N",
    @Json(name = "email_order_alert") @SerializedName("email_order_alert") @ColumnInfo(name = "email_order_alert") var emailOrderAlert: String? = "N",
    @Json(name = "email_feedback_alert") @SerializedName("email_feedback_alert") @ColumnInfo(name = "email_feedback_alert") var emailFeedbackAlert: String? = "N",
    @Json(name = "email_payment_alert") @SerializedName("email_payment_alert") @ColumnInfo(name = "email_payment_alert") var emailPaymentAlert: String? = "N",
    @Json(name = "email_application_alert") @SerializedName("email_application_alert") @ColumnInfo(name = "email_application_alert") var emailApplicationAlert: String? = "N",
    @Json(name = "preferred_lang") @SerializedName("preferred_lang") @ColumnInfo(name = "preferred_lang") var preferredLang: String? = "",
    @Json(name = "preferred_currency") @SerializedName("preferred_currency") @ColumnInfo(name = "preferred_currency") var preferredCurrency: String? = "",
    @Json(name = "fname") @SerializedName("fname") @ColumnInfo(name = "fname") var fname: String? = "",
    @Json(name = "lname") @SerializedName("lname") @ColumnInfo(name = "lname") var lname: String? = "",
    @Json(name = "nickname") @SerializedName("nickname") @ColumnInfo(name = "nickname") var nickname: String? = "",
    @Json(name = "email") @SerializedName("email") @ColumnInfo(name = "email") var email: String? = "",
    @Json(name = "fb_address") @SerializedName("fb_address") @ColumnInfo(name = "fb_address") var fbAddress: String? = "",
    @Json(name = "fb_id") @SerializedName("fb_id") @ColumnInfo(name = "fb_id") var fbId: Int = 0,
    @Json(name = "fb_info") @SerializedName("fb_info") @ColumnInfo(name = "fb_info") var fbInfo: String? = "",
    @Json(name = "phone") @SerializedName("phone") @ColumnInfo(name = "phone") var phone: String? = "",
    @Json(name = "fax") @SerializedName("fax") @ColumnInfo(name = "fax") var fax: String? = "",
    @Json(name = "password") @SerializedName("password") @ColumnInfo(name = "password") var password: String? = "",
    @Json(name = "present_address_1") @SerializedName("present_address_1") @ColumnInfo(name = "present_address_1") var presentAddress1: String? = "",
    @Json(name = "present_address_2") @SerializedName("present_address_2") @ColumnInfo(name = "present_address_2") var presentAddress2: String? = "",
    @Json(name = "present_postalcode") @SerializedName("present_postalcode") @ColumnInfo(name = "present_postalcode") var presentPostalcode: String? = "",
    @Json(name = "present_province_id") @SerializedName("present_province_id") @ColumnInfo(name = "present_province_id") var presentProvinceId: Int = 1,
    @Json(name = "present_country_id") @SerializedName("present_country_id") @ColumnInfo(name = "present_country_id") var presentCountryId: Int = 173,
    @Json(name = "permanent_address_1") @SerializedName("permanent_address_1") @ColumnInfo(name = "permanent_address_1") var permanentAddress1: String? = "",
    @Json(name = "permanent_address_2") @SerializedName("permanent_address_2") @ColumnInfo(name = "permanent_address_2") var permanentAddress2: String? = "",
    @Json(name = "permanent_postalcode") @SerializedName("permanent_postalcode") @ColumnInfo(name = "permanent_postalcode") var permanentPostalcode: String? = "",
    @Json(name = "permanent_province_id") @SerializedName("permanent_province_id") @ColumnInfo(name = "permanent_province_id") var permanentProvinceId: Int = 1,
    @Json(name = "permanent_country_id") @SerializedName("permanent_country_id") @ColumnInfo(name = "permanent_country_id") var permanentCountryId: Int = 173,
    @Json(name = "address") @SerializedName("address") @ColumnInfo(name = "address") var address: String? = "",
    @Json(name = "json_media") @SerializedName("json_media") @ColumnInfo(name = "json_media") var jsonMedia: String? = "",
    @Json(name = "json_info") @SerializedName("json_info") @ColumnInfo(name = "json_info") var jsonInfo: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}