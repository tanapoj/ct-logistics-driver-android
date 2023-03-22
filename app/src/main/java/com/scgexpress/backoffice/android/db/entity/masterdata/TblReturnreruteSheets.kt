package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_returnrerute_sheets")
data class TblReturnreruteSheets(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "barcode") @SerializedName("barcode") @ColumnInfo(name = "barcode") var barcode: String? = "",
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "branch_origin") @SerializedName("branch_origin") @ColumnInfo(name = "branch_origin") var branchOrigin: String? = "",
    @Json(name = "branch_destination") @SerializedName("branch_destination") @ColumnInfo(name = "branch_destination") var branchDestination: String? = "",
    @Json(name = "no_of_total_return_items") @SerializedName("no_of_total_return_items") @ColumnInfo(name = "no_of_total_return_items") var noOfTotalReturnItems: Int = 0,
    @Json(name = "no_of_total_reroute_items") @SerializedName("no_of_total_reroute_items") @ColumnInfo(name = "no_of_total_reroute_items") var noOfTotalRerouteItems: Int = 0,
    @Json(name = "driver") @SerializedName("driver") @ColumnInfo(name = "driver") var driver: String? = "",
    @Json(name = "vehicle") @SerializedName("vehicle") @ColumnInfo(name = "vehicle") var vehicle: String? = "",
    @Json(name = "type") @SerializedName("type") @ColumnInfo(name = "type") var type: String? = "",
    @Json(name = "user_created") @SerializedName("user_created") @ColumnInfo(name = "user_created") var userCreated: Int = 0,
    @Json(name = "user_modified") @SerializedName("user_modified") @ColumnInfo(name = "user_modified") var userModified: Int = 0,
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}