package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_manifest_ofd_items")
data class TblManifestOfdItems(
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
    @Json(name = "ofd_status") @SerializedName("ofd_status") @ColumnInfo(name = "ofd_status") var ofdStatus: Int = 0,
    @Json(name = "ofd_note") @SerializedName("ofd_note") @ColumnInfo(name = "ofd_note") var ofdNote: String? = "",
    @Json(name = "ofd_collected_date") @SerializedName("ofd_collected_date") @ColumnInfo(name = "ofd_collected_date") var ofdCollectedDate: String? = "",
    @Json(name = "neko_push_status") @SerializedName("neko_push_status") @ColumnInfo(name = "neko_push_status") var nekoPushStatus: Int = 0,
    @Json(name = "neko_last_push_datetime") @SerializedName("neko_last_push_datetime") @ColumnInfo(name = "neko_last_push_datetime") var nekoLastPushDatetime: String? = "",
    @Json(name = "status_neko_push_ofd") @SerializedName("status_neko_push_ofd") @ColumnInfo(name = "status_neko_push_ofd") var statusNekoPushOfd: String? = "N",
    @Json(name = "code_neko_push_ofd") @SerializedName("code_neko_push_ofd") @ColumnInfo(name = "code_neko_push_ofd") var codeNekoPushOfd: String? = "",
    @Json(name = "date_neko_push_ofd") @SerializedName("date_neko_push_ofd") @ColumnInfo(name = "date_neko_push_ofd") var dateNekoPushOfd: String? = "",
    @Json(name = "status_delivered") @SerializedName("status_delivered") @ColumnInfo(name = "status_delivered") var statusDelivered: String? = "N",
    @Json(name = "user_delivered") @SerializedName("user_delivered") @ColumnInfo(name = "user_delivered") var userDelivered: Int = 0,
    @Json(name = "date_delivered") @SerializedName("date_delivered") @ColumnInfo(name = "date_delivered") var dateDelivered: String? = "",
    @Json(name = "status_neko_push_delivered") @SerializedName("status_neko_push_delivered") @ColumnInfo(name = "status_neko_push_delivered") var statusNekoPushDelivered: String? = "N",
    @Json(name = "code_neko_push_delivered") @SerializedName("code_neko_push_delivered") @ColumnInfo(name = "code_neko_push_delivered") var codeNekoPushDelivered: String? = "",
    @Json(name = "date_neko_push_delivered") @SerializedName("date_neko_push_delivered") @ColumnInfo(name = "date_neko_push_delivered") var dateNekoPushDelivered: String? = "",
    @Json(name = "status_retention") @SerializedName("status_retention") @ColumnInfo(name = "status_retention") var statusRetention: String? = "N",
    @Json(name = "user_retention") @SerializedName("user_retention") @ColumnInfo(name = "user_retention") var userRetention: Int = 0,
    @Json(name = "date_retention") @SerializedName("date_retention") @ColumnInfo(name = "date_retention") var dateRetention: String? = "",
    @Json(name = "status_neko_push_retention") @SerializedName("status_neko_push_retention") @ColumnInfo(name = "status_neko_push_retention") var statusNekoPushRetention: String? = "N",
    @Json(name = "code_neko_push_retention") @SerializedName("code_neko_push_retention") @ColumnInfo(name = "code_neko_push_retention") var codeNekoPushRetention: String? = "",
    @Json(name = "date_neko_push_retention") @SerializedName("date_neko_push_retention") @ColumnInfo(name = "date_neko_push_retention") var dateNekoPushRetention: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}