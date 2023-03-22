package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_manifest_items")
data class TblManifestItems(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "sheet_id") @SerializedName("sheet_id") @ColumnInfo(name = "sheet_id") var sheetId: Int = 0,
    @Json(name = "stack_id") @SerializedName("stack_id") @ColumnInfo(name = "stack_id") var stackId: String? = "",
    @Json(name = "barcode") @SerializedName("barcode") @ColumnInfo(name = "barcode") var barcode: String? = "",
    @Json(name = "parcels_id") @SerializedName("parcels_id") @ColumnInfo(name = "parcels_id") var parcelsId: Int = 0,
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_received") @SerializedName("status_received") @ColumnInfo(name = "status_received") var statusReceived: String? = "N",
    @Json(name = "status_conflict_branch") @SerializedName("status_conflict_branch") @ColumnInfo(name = "status_conflict_branch") var statusConflictBranch: String? = "N",
    @Json(name = "status_destination_scan_out") @SerializedName("status_destination_scan_out") @ColumnInfo(name = "status_destination_scan_out") var statusDestinationScanOut: String? = "N",
    @Json(name = "user_created") @SerializedName("user_created") @ColumnInfo(name = "user_created") var userCreated: Int = 0,
    @Json(name = "user_received") @SerializedName("user_received") @ColumnInfo(name = "user_received") var userReceived: Int = 0,
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_received") @SerializedName("date_received") @ColumnInfo(name = "date_received") var dateReceived: String? = "",
    @Json(name = "status_neko_push_scan_out") @SerializedName("status_neko_push_scan_out") @ColumnInfo(name = "status_neko_push_scan_out") var statusNekoPushScanOut: String? = "N",
    @Json(name = "code_neko_push_scan_out") @SerializedName("code_neko_push_scan_out") @ColumnInfo(name = "code_neko_push_scan_out") var codeNekoPushScanOut: String? = "",
    @Json(name = "date_neko_push_scan_out") @SerializedName("date_neko_push_scan_out") @ColumnInfo(name = "date_neko_push_scan_out") var dateNekoPushScanOut: String? = "",
    @Json(name = "status_neko_push_scan_in") @SerializedName("status_neko_push_scan_in") @ColumnInfo(name = "status_neko_push_scan_in") var statusNekoPushScanIn: String? = "N",
    @Json(name = "code_neko_push_scan_in") @SerializedName("code_neko_push_scan_in") @ColumnInfo(name = "code_neko_push_scan_in") var codeNekoPushScanIn: String? = "",
    @Json(name = "date_neko_push_scan_in") @SerializedName("date_neko_push_scan_in") @ColumnInfo(name = "date_neko_push_scan_in") var dateNekoPushScanIn: String? = "",
    @Json(name = "sending_status_scanout") @SerializedName("sending_status_scanout") @ColumnInfo(name = "sending_status_scanout") var sendingStatusScanout: Int = 0,
    @Json(name = "sending_status_scanin") @SerializedName("sending_status_scanin") @ColumnInfo(name = "sending_status_scanin") var sendingStatusScanin: Int = 0,
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}