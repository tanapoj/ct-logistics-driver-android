package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_parcel_status_2")
data class TblParcelStatus2(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "status_test") @SerializedName("status_test") @ColumnInfo(name = "status_test") var statusTest: String? = "N",
    @Json(name = "status_deleted") @SerializedName("status_deleted") @ColumnInfo(name = "status_deleted") var statusDeleted: String? = "N",
    @Json(name = "status_active") @SerializedName("status_active") @ColumnInfo(name = "status_active") var statusActive: String? = "Y",
    @Json(name = "status_arrived") @SerializedName("status_arrived") @ColumnInfo(name = "status_arrived") var statusArrived: String? = "",
    @Json(name = "status_scan_out_destination") @SerializedName("status_scan_out_destination") @ColumnInfo(name = "status_scan_out_destination") var statusScanOutDestination: String? = "",
    @Json(name = "status_ofd_delivered") @SerializedName("status_ofd_delivered") @ColumnInfo(name = "status_ofd_delivered") var statusOfdDelivered: String? = "",
    @Json(name = "status_ofd_retention") @SerializedName("status_ofd_retention") @ColumnInfo(name = "status_ofd_retention") var statusOfdRetention: String? = "",
    @Json(name = "status_successful_push_to_neko") @SerializedName("status_successful_push_to_neko") @ColumnInfo(name = "status_successful_push_to_neko") var statusSuccessfulPushToNeko: String? = "N",
    @Json(name = "code_response_from_neko") @SerializedName("code_response_from_neko") @ColumnInfo(name = "code_response_from_neko") var codeResponseFromNeko: String? = "",
    @Json(name = "datetime_push_to_neko") @SerializedName("datetime_push_to_neko") @ColumnInfo(name = "datetime_push_to_neko") var datetimePushToNeko: String? = "",
    @Json(name = "id_manifest_sheet") @SerializedName("id_manifest_sheet") @ColumnInfo(name = "id_manifest_sheet") var idManifestSheet: Int = 0,
    @Json(name = "tracking_number") @SerializedName("tracking_number") @ColumnInfo(name = "tracking_number") var trackingNumber: String? = "",
    @Json(name = "id_parcel") @SerializedName("id_parcel") @ColumnInfo(name = "id_parcel") var idParcel: Int = 0,
    @Json(name = "id_data_source") @SerializedName("id_data_source") @ColumnInfo(name = "id_data_source") var idDataSource: Int = 0,
    @Json(name = "id_status") @SerializedName("id_status") @ColumnInfo(name = "id_status") var idStatus: Int = 0,
    @Json(name = "id_service_type_level_1") @SerializedName("id_service_type_level_1") @ColumnInfo(name = "id_service_type_level_1") var idServiceTypeLevel1: Int = 0,
    @Json(name = "id_service_type_level_2") @SerializedName("id_service_type_level_2") @ColumnInfo(name = "id_service_type_level_2") var idServiceTypeLevel2: Int = 0,
    @Json(name = "id_service_type_level_3") @SerializedName("id_service_type_level_3") @ColumnInfo(name = "id_service_type_level_3") var idServiceTypeLevel3: Int = 0,
    @Json(name = "id_parcel_sizing") @SerializedName("id_parcel_sizing") @ColumnInfo(name = "id_parcel_sizing") var idParcelSizing: Int = 0,
    @Json(name = "id_irregular_type") @SerializedName("id_irregular_type") @ColumnInfo(name = "id_irregular_type") var idIrregularType: Int = 0,
    @Json(name = "id_retention_reason") @SerializedName("id_retention_reason") @ColumnInfo(name = "id_retention_reason") var idRetentionReason: Int = 0,
    @Json(name = "id_return_reason") @SerializedName("id_return_reason") @ColumnInfo(name = "id_return_reason") var idReturnReason: Int = 0,
    @Json(name = "id_neko_billing") @SerializedName("id_neko_billing") @ColumnInfo(name = "id_neko_billing") var idNekoBilling: Int = 0,
    @Json(name = "id_neko_tracking") @SerializedName("id_neko_tracking") @ColumnInfo(name = "id_neko_tracking") var idNekoTracking: Int = 0,
    @Json(name = "id_reference_1") @SerializedName("id_reference_1") @ColumnInfo(name = "id_reference_1") var idReference1: Int = 0,
    @Json(name = "id_reference_2") @SerializedName("id_reference_2") @ColumnInfo(name = "id_reference_2") var idReference2: Int = 0,
    @Json(name = "id_reference_3") @SerializedName("id_reference_3") @ColumnInfo(name = "id_reference_3") var idReference3: Int = 0,
    @Json(name = "amount_1") @SerializedName("amount_1") @ColumnInfo(name = "amount_1") var amount1: String? = "",
    @Json(name = "amount_2") @SerializedName("amount_2") @ColumnInfo(name = "amount_2") var amount2: String? = "",
    @Json(name = "note_1") @SerializedName("note_1") @ColumnInfo(name = "note_1") var note1: String? = "",
    @Json(name = "note_2") @SerializedName("note_2") @ColumnInfo(name = "note_2") var note2: String? = "",
    @Json(name = "id_created_user") @SerializedName("id_created_user") @ColumnInfo(name = "id_created_user") var idCreatedUser: Int = 0,
    @Json(name = "id_last_modified_user") @SerializedName("id_last_modified_user") @ColumnInfo(name = "id_last_modified_user") var idLastModifiedUser: Int = 0,
    @Json(name = "datetime_created") @SerializedName("datetime_created") @ColumnInfo(name = "datetime_created") var datetimeCreated: String? = "",
    @Json(name = "datetime_last_modified") @SerializedName("datetime_last_modified") @ColumnInfo(name = "datetime_last_modified") var datetimeLastModified: String? = "",
    @Json(name = "datetime_input") @SerializedName("datetime_input") @ColumnInfo(name = "datetime_input") var datetimeInput: String? = ""
) {
    @Ignore
    constructor() : this(0)
}