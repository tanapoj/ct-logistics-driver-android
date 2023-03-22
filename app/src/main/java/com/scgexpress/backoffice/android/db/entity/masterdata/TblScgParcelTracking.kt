package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_parcel_tracking", primaryKeys = arrayOf("tracking_number", "seq"))
data class TblScgParcelTracking(
    @Json(name = "tracking_number") @SerializedName("tracking_number") @ColumnInfo(name = "tracking_number") var trackingNumber: String = "",
    @Json(name = "seq") @SerializedName("seq") @ColumnInfo(name = "seq") var seq: String = "",
    @Json(name = "delivery_date") @SerializedName("delivery_date") @ColumnInfo(name = "delivery_date") var deliveryDate: String? = "",
    @Json(name = "delivery_time_zone") @SerializedName("delivery_time_zone") @ColumnInfo(name = "delivery_time_zone") var deliveryTimeZone: String? = "",
    @Json(name = "size_item_code") @SerializedName("size_item_code") @ColumnInfo(name = "size_item_code") var sizeItemCode: String? = "",
    @Json(name = "item_name") @SerializedName("item_name") @ColumnInfo(name = "item_name") var itemName: String? = "",
    @Json(name = "arrive_branch_code") @SerializedName("arrive_branch_code") @ColumnInfo(name = "arrive_branch_code") var arriveBranchCode: String? = "",
    @Json(name = "arrive_branch_name") @SerializedName("arrive_branch_name") @ColumnInfo(name = "arrive_branch_name") var arriveBranchName: String? = "",
    @Json(name = "status_code") @SerializedName("status_code") @ColumnInfo(name = "status_code") var statusCode: String? = "",
    @Json(name = "status") @SerializedName("status") @ColumnInfo(name = "status") var status: String? = "",
    @Json(name = "input_date") @SerializedName("input_date") @ColumnInfo(name = "input_date") var inputDate: String? = "",
    @Json(name = "input_time") @SerializedName("input_time") @ColumnInfo(name = "input_time") var inputTime: String? = "",
    @Json(name = "branch_code") @SerializedName("branch_code") @ColumnInfo(name = "branch_code") var branchCode: String? = "",
    @Json(name = "branch_name") @SerializedName("branch_name") @ColumnInfo(name = "branch_name") var branchName: String? = "",
    @Json(name = "pic_id") @SerializedName("pic_id") @ColumnInfo(name = "pic_id") var picId: String? = "",
    @Json(name = "c2_delivery_date") @SerializedName("c2_delivery_date") @ColumnInfo(name = "c2_delivery_date") var c2DeliveryDate: String? = "",
    @Json(name = "c2_input_date") @SerializedName("c2_input_date") @ColumnInfo(name = "c2_input_date") var c2InputDate: String? = "",
    @Json(name = "c2_date_created") @SerializedName("c2_date_created") @ColumnInfo(name = "c2_date_created") var c2DateCreated: String? = ""
) {
    @Ignore
    constructor() : this("0")
}