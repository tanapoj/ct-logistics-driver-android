package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_temp_cal")
data class TblTempCal(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "tracking_number") @SerializedName("tracking_number") @ColumnInfo(name = "tracking_number") var trackingNumber: String? = "",
    @Json(name = "c2_input_datetime") @SerializedName("c2_input_datetime") @ColumnInfo(name = "c2_input_datetime") var c2InputDatetime: String? = "",
    @Json(name = "c2_date_created") @SerializedName("c2_date_created") @ColumnInfo(name = "c2_date_created") var c2DateCreated: String? = "",
    @Json(name = "status_code") @SerializedName("status_code") @ColumnInfo(name = "status_code") var statusCode: String? = "",
    @Json(name = "status_short_name") @SerializedName("status_short_name") @ColumnInfo(name = "status_short_name") var statusShortName: String? = "",
    @Json(name = "center_code") @SerializedName("center_code") @ColumnInfo(name = "center_code") var centerCode: String? = "",
    @Json(name = "center_name") @SerializedName("center_name") @ColumnInfo(name = "center_name") var centerName: String? = "",
    @Json(name = "employee_id") @SerializedName("employee_id") @ColumnInfo(name = "employee_id") var employeeId: String? = "",
    @Json(name = "issued") @SerializedName("issued") @ColumnInfo(name = "issued") var issued: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}