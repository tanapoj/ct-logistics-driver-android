package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_parcel_returnrerute")
data class TblScgParcelReturnrerute(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "sheet_id") @SerializedName("sheet_id") @ColumnInfo(name = "sheet_id") var sheetId: Int = 0,
    @Json(name = "tracking_number") @SerializedName("tracking_number") @ColumnInfo(name = "tracking_number") var trackingNumber: String? = "",
    @Json(name = "relate_tracking_number") @SerializedName("relate_tracking_number") @ColumnInfo(name = "relate_tracking_number") var relateTrackingNumber: String? = "",
    @Json(name = "parcels_id") @SerializedName("parcels_id") @ColumnInfo(name = "parcels_id") var parcelsId: Int = 0,
    @Json(name = "type") @SerializedName("type") @ColumnInfo(name = "type") var type: String? = "",
    @Json(name = "new_route") @SerializedName("new_route") @ColumnInfo(name = "new_route") var newRoute: String? = "",
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_received") @SerializedName("status_received") @ColumnInfo(name = "status_received") var statusReceived: String? = "N",
    @Json(name = "status_conflict_branch") @SerializedName("status_conflict_branch") @ColumnInfo(name = "status_conflict_branch") var statusConflictBranch: String? = "N",
    @Json(name = "status_destination_scan_out") @SerializedName("status_destination_scan_out") @ColumnInfo(name = "status_destination_scan_out") var statusDestinationScanOut: String? = "N",
    @Json(name = "user_created") @SerializedName("user_created") @ColumnInfo(name = "user_created") var userCreated: Int = 0,
    @Json(name = "user_modified") @SerializedName("user_modified") @ColumnInfo(name = "user_modified") var userModified: Int = 0,
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = "",
    @Json(name = "status_neko_push_scan_in") @SerializedName("status_neko_push_scan_in") @ColumnInfo(name = "status_neko_push_scan_in") var statusNekoPushScanIn: String? = "N",
    @Json(name = "code_neko_push_scan_in") @SerializedName("code_neko_push_scan_in") @ColumnInfo(name = "code_neko_push_scan_in") var codeNekoPushScanIn: String? = "",
    @Json(name = "date_neko_push_scan_in") @SerializedName("date_neko_push_scan_in") @ColumnInfo(name = "date_neko_push_scan_in") var dateNekoPushScanIn: String? = "",
    @Json(name = "neko_last_push_datetime") @SerializedName("neko_last_push_datetime") @ColumnInfo(name = "neko_last_push_datetime") var nekoLastPushDatetime: String? = ""
) {
    @Ignore
    constructor() : this(0)
}