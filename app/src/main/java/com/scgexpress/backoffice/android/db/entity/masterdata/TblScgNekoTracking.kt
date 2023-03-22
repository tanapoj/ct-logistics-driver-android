package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_neko_tracking")
data class TblScgNekoTracking(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "c2_tracking_number") @SerializedName("c2_tracking_number") @ColumnInfo(name = "c2_tracking_number") var c2TrackingNumber: String? = "",
    @Json(name = "c2_parcel_id") @SerializedName("c2_parcel_id") @ColumnInfo(name = "c2_parcel_id") var c2ParcelId: Int = 0,
    @Json(name = "c2_schedule_datetime") @SerializedName("c2_schedule_datetime") @ColumnInfo(name = "c2_schedule_datetime") var c2ScheduleDatetime: String? = "",
    @Json(name = "c2_header_date") @SerializedName("c2_header_date") @ColumnInfo(name = "c2_header_date") var c2HeaderDate: String? = "",
    @Json(name = "c2_input_datetime") @SerializedName("c2_input_datetime") @ColumnInfo(name = "c2_input_datetime") var c2InputDatetime: String? = "",
    @Json(name = "c2_server_receive_datetime") @SerializedName("c2_server_receive_datetime") @ColumnInfo(name = "c2_server_receive_datetime") var c2ServerReceiveDatetime: String? = "",
    @Json(name = "c2_collect_amount") @SerializedName("c2_collect_amount") @ColumnInfo(name = "c2_collect_amount") var c2CollectAmount: Double = .0,
    @Json(name = "c2_date_created") @SerializedName("c2_date_created") @ColumnInfo(name = "c2_date_created") var c2DateCreated: String? = "",
    @Json(name = "c2_status_mapping") @SerializedName("c2_status_mapping") @ColumnInfo(name = "c2_status_mapping") var c2StatusMapping: String? = "",
    @Json(name = "customer_code") @SerializedName("customer_code") @ColumnInfo(name = "customer_code") var customerCode: String? = "",
    @Json(name = "tracking_number") @SerializedName("tracking_number") @ColumnInfo(name = "tracking_number") var trackingNumber: String? = "",
    @Json(name = "schedule_date") @SerializedName("schedule_date") @ColumnInfo(name = "schedule_date") var scheduleDate: String? = "",
    @Json(name = "schedule_time") @SerializedName("schedule_time") @ColumnInfo(name = "schedule_time") var scheduleTime: String? = "",
    @Json(name = "size_item_code") @SerializedName("size_item_code") @ColumnInfo(name = "size_item_code") var sizeItemCode: String? = "",
    @Json(name = "item_name") @SerializedName("item_name") @ColumnInfo(name = "item_name") var itemName: String? = "",
    @Json(name = "dest_center_code") @SerializedName("dest_center_code") @ColumnInfo(name = "dest_center_code") var destCenterCode: String? = "",
    @Json(name = "dest_center_name") @SerializedName("dest_center_name") @ColumnInfo(name = "dest_center_name") var destCenterName: String? = "",
    @Json(name = "status_code") @SerializedName("status_code") @ColumnInfo(name = "status_code") var statusCode: String? = "",
    @Json(name = "status_short_name") @SerializedName("status_short_name") @ColumnInfo(name = "status_short_name") var statusShortName: String? = "",
    @Json(name = "header_date") @SerializedName("header_date") @ColumnInfo(name = "header_date") var headerDate: String? = "",
    @Json(name = "collect_return_tracking_number") @SerializedName("collect_return_tracking_number") @ColumnInfo(name = "collect_return_tracking_number") var collectReturnTrackingNumber: String? = "",
    @Json(name = "center_code") @SerializedName("center_code") @ColumnInfo(name = "center_code") var centerCode: String? = "",
    @Json(name = "center_name") @SerializedName("center_name") @ColumnInfo(name = "center_name") var centerName: String? = "",
    @Json(name = "employee_id") @SerializedName("employee_id") @ColumnInfo(name = "employee_id") var employeeId: String? = "",
    @Json(name = "waybill_type") @SerializedName("waybill_type") @ColumnInfo(name = "waybill_type") var waybillType: String? = "",
    @Json(name = "input_datetime") @SerializedName("input_datetime") @ColumnInfo(name = "input_datetime") var inputDatetime: String? = "",
    @Json(name = "server_receive_datetime") @SerializedName("server_receive_datetime") @ColumnInfo(name = "server_receive_datetime") var serverReceiveDatetime: String? = "",
    @Json(name = "terminal_id") @SerializedName("terminal_id") @ColumnInfo(name = "terminal_id") var terminalId: String? = "",
    @Json(name = "route_classification") @SerializedName("route_classification") @ColumnInfo(name = "route_classification") var routeClassification: String? = "",
    @Json(name = "collection_amount") @SerializedName("collection_amount") @ColumnInfo(name = "collection_amount") var collectionAmount: String? = "",
    @Json(name = "sorting_code") @SerializedName("sorting_code") @ColumnInfo(name = "sorting_code") var sortingCode: String? = "",
    @Json(name = "postal_code") @SerializedName("postal_code") @ColumnInfo(name = "postal_code") var postalCode: String? = "",
    @Json(name = "collect_return_reason_code") @SerializedName("collect_return_reason_code") @ColumnInfo(name = "collect_return_reason_code") var collectReturnReasonCode: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}