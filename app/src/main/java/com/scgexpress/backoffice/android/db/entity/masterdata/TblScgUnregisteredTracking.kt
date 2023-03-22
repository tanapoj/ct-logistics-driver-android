package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_unregistered_tracking")
data class TblScgUnregisteredTracking(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "tracking_number") @SerializedName("tracking_number") @ColumnInfo(name = "tracking_number") var trackingNumber: String? = "0",
    @Json(name = "customer_code") @SerializedName("customer_code") @ColumnInfo(name = "customer_code") var customerCode: String? = "",
    @Json(name = "latest_status_code") @SerializedName("latest_status_code") @ColumnInfo(name = "latest_status_code") var latestStatusCode: String? = "",
    @Json(name = "latest_status_name") @SerializedName("latest_status_name") @ColumnInfo(name = "latest_status_name") var latestStatusName: String? = "",
    @Json(name = "latest_center_code") @SerializedName("latest_center_code") @ColumnInfo(name = "latest_center_code") var latestCenterCode: String? = "",
    @Json(name = "latest_center_name") @SerializedName("latest_center_name") @ColumnInfo(name = "latest_center_name") var latestCenterName: String? = "",
    @Json(name = "latest_input_datetime") @SerializedName("latest_input_datetime") @ColumnInfo(name = "latest_input_datetime") var latestInputDatetime: String? = "",
    @Json(name = "pickup_employee_id") @SerializedName("pickup_employee_id") @ColumnInfo(name = "pickup_employee_id") var pickupEmployeeId: String? = "",
    @Json(name = "pickup_center_code") @SerializedName("pickup_center_code") @ColumnInfo(name = "pickup_center_code") var pickupCenterCode: String? = "",
    @Json(name = "pickup_center_name") @SerializedName("pickup_center_name") @ColumnInfo(name = "pickup_center_name") var pickupCenterName: String? = "",
    @Json(name = "pickup_datetime") @SerializedName("pickup_datetime") @ColumnInfo(name = "pickup_datetime") var pickupDatetime: String? = "",
    @Json(name = "freight_fare") @SerializedName("freight_fare") @ColumnInfo(name = "freight_fare") var freightFare: String? = "",
    @Json(name = "freight_fare_issued_datetime") @SerializedName("freight_fare_issued_datetime") @ColumnInfo(name = "freight_fare_issued_datetime") var freightFareIssuedDatetime: String? = "",
    @Json(name = "size_code") @SerializedName("size_code") @ColumnInfo(name = "size_code") var sizeCode: String? = "",
    @Json(name = "recipient_signed") @SerializedName("recipient_signed") @ColumnInfo(name = "recipient_signed") var recipientSigned: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}