package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_manifest_sheets_general")
data class TblManifestSheetsGeneral(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "code") @SerializedName("code") @ColumnInfo(name = "code") var code: String? = "",
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "status_completed") @SerializedName("status_completed") @ColumnInfo(name = "status_completed") var statusCompleted: String? = "N",
    @Json(name = "id_manifest_type") @SerializedName("id_manifest_type") @ColumnInfo(name = "id_manifest_type") var idManifestType: Int = 0,
    @Json(name = "id_issued_branch") @SerializedName("id_issued_branch") @ColumnInfo(name = "id_issued_branch") var idIssuedBranch: Int = 0,
    @Json(name = "id_departure_branch") @SerializedName("id_departure_branch") @ColumnInfo(name = "id_departure_branch") var idDepartureBranch: Int = 0,
    @Json(name = "id_arrival_branch") @SerializedName("id_arrival_branch") @ColumnInfo(name = "id_arrival_branch") var idArrivalBranch: Int = 0,
    @Json(name = "name_vehicle") @SerializedName("name_vehicle") @ColumnInfo(name = "name_vehicle") var nameVehicle: String? = "",
    @Json(name = "name_driver") @SerializedName("name_driver") @ColumnInfo(name = "name_driver") var nameDriver: String? = "",
    @Json(name = "no_of_items_total") @SerializedName("no_of_items_total") @ColumnInfo(name = "no_of_items_total") var noOfItemsTotal: Int = 0,
    @Json(name = "no_of_items_depart") @SerializedName("no_of_items_depart") @ColumnInfo(name = "no_of_items_depart") var noOfItemsDepart: Int = 0,
    @Json(name = "no_of_items_arrival") @SerializedName("no_of_items_arrival") @ColumnInfo(name = "no_of_items_arrival") var noOfItemsArrival: Int = 0,
    @Json(name = "no_of_items_delivered") @SerializedName("no_of_items_delivered") @ColumnInfo(name = "no_of_items_delivered") var noOfItemsDelivered: Int = 0,
    @Json(name = "no_of_items_retention") @SerializedName("no_of_items_retention") @ColumnInfo(name = "no_of_items_retention") var noOfItemsRetention: Int = 0,
    @Json(name = "no_of_items_irregular") @SerializedName("no_of_items_irregular") @ColumnInfo(name = "no_of_items_irregular") var noOfItemsIrregular: Int = 0,
    @Json(name = "user_created") @SerializedName("user_created") @ColumnInfo(name = "user_created") var userCreated: Int = 0,
    @Json(name = "user_deleted") @SerializedName("user_deleted") @ColumnInfo(name = "user_deleted") var userDeleted: Int = 0,
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_completed") @SerializedName("date_completed") @ColumnInfo(name = "date_completed") var dateCompleted: String? = "",
    @Json(name = "last_modified") @SerializedName("last_modified") @ColumnInfo(name = "last_modified") var lastModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}