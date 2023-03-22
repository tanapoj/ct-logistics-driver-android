package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_manifest_ofd_sheets")
data class TblManifestOfdSheets(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "barcode") @SerializedName("barcode") @ColumnInfo(name = "barcode") var barcode: String? = "",
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_completed") @SerializedName("status_completed") @ColumnInfo(name = "status_completed") var statusCompleted: String? = "N",
    @Json(name = "branch_origin") @SerializedName("branch_origin") @ColumnInfo(name = "branch_origin") var branchOrigin: String? = "",
    @Json(name = "branch_destination") @SerializedName("branch_destination") @ColumnInfo(name = "branch_destination") var branchDestination: String? = "",
    @Json(name = "no_of_received_items") @SerializedName("no_of_received_items") @ColumnInfo(name = "no_of_received_items") var noOfReceivedItems: Int = 0,
    @Json(name = "no_of_total_items") @SerializedName("no_of_total_items") @ColumnInfo(name = "no_of_total_items") var noOfTotalItems: Int = 0,
    @Json(name = "driver") @SerializedName("driver") @ColumnInfo(name = "driver") var driver: String? = "",
    @Json(name = "vehicle") @SerializedName("vehicle") @ColumnInfo(name = "vehicle") var vehicle: String? = "",
    @Json(name = "user_created") @SerializedName("user_created") @ColumnInfo(name = "user_created") var userCreated: Int = 0,
    @Json(name = "user_deleted") @SerializedName("user_deleted") @ColumnInfo(name = "user_deleted") var userDeleted: Int = 0,
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_completed") @SerializedName("date_completed") @ColumnInfo(name = "date_completed") var dateCompleted: String? = "",
    @Json(name = "last_modified") @SerializedName("last_modified") @ColumnInfo(name = "last_modified") var lastModified: String? = "",
    @Json(name = "status_neko_push_scan_out") @SerializedName("status_neko_push_scan_out") @ColumnInfo(name = "status_neko_push_scan_out") var statusNekoPushScanOut: String? = "N",
    @Json(name = "code_neko_push_scan_out") @SerializedName("code_neko_push_scan_out") @ColumnInfo(name = "code_neko_push_scan_out") var codeNekoPushScanOut: String? = "",
    @Json(name = "date_neko_push_scan_out") @SerializedName("date_neko_push_scan_out") @ColumnInfo(name = "date_neko_push_scan_out") var dateNekoPushScanOut: String? = "",
    @Json(name = "status_neko_push_scan_in") @SerializedName("status_neko_push_scan_in") @ColumnInfo(name = "status_neko_push_scan_in") var statusNekoPushScanIn: String? = "N",
    @Json(name = "code_neko_push_scan_in") @SerializedName("code_neko_push_scan_in") @ColumnInfo(name = "code_neko_push_scan_in") var codeNekoPushScanIn: String? = "",
    @Json(name = "date_neko_push_scan_in") @SerializedName("date_neko_push_scan_in") @ColumnInfo(name = "date_neko_push_scan_in") var dateNekoPushScanIn: String? = "",
    @Json(name = "no_of_delivered_items") @SerializedName("no_of_delivered_items") @ColumnInfo(name = "no_of_delivered_items") var noOfDeliveredItems: Int = 0,
    @Json(name = "no_of_retention_items") @SerializedName("no_of_retention_items") @ColumnInfo(name = "no_of_retention_items") var noOfRetentionItems: Int = 0,
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}