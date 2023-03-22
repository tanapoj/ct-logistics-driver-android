package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_neko_billing")
data class TblScgNekoBilling(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "c2_tracking_number") @SerializedName("c2_tracking_number") @ColumnInfo(name = "c2_tracking_number") var c2TrackingNumber: String? = "",
    @Json(name = "c2_parcel_id") @SerializedName("c2_parcel_id") @ColumnInfo(name = "c2_parcel_id") var c2ParcelId: Int = 0,
    @Json(name = "c2_date_created") @SerializedName("c2_date_created") @ColumnInfo(name = "c2_date_created") var c2DateCreated: String? = "",
    @Json(name = "c2_operation_west_4_yr_mth_date") @SerializedName("c2_operation_west_4_yr_mth_date") @ColumnInfo(name = "c2_operation_west_4_yr_mth_date") var c2OperationWest4YrMthDate: String? = "",
    @Json(name = "c2_operation_tally_west_4_yr_mth_date") @SerializedName("c2_operation_tally_west_4_yr_mth_date") @ColumnInfo(
        name = "c2_operation_tally_west_4_yr_mth_date"
    ) var c2OperationTallyWest4YrMthDate: String? = "",
    @Json(name = "c2_delivery_operation_west_4_yr_mth_date") @SerializedName("c2_delivery_operation_west_4_yr_mth_date") @ColumnInfo(
        name = "c2_delivery_operation_west_4_yr_mth_date"
    ) var c2DeliveryOperationWest4YrMthDate: String? = "",
    @Json(name = "c2_delivery_tally_west_4_yr_mth_date") @SerializedName("c2_delivery_tally_west_4_yr_mth_date") @ColumnInfo(
        name = "c2_delivery_tally_west_4_yr_mth_date"
    ) var c2DeliveryTallyWest4YrMthDate: String? = "",
    @Json(name = "company_code") @SerializedName("company_code") @ColumnInfo(name = "company_code") var companyCode: String? = "",
    @Json(name = "data_classification") @SerializedName("data_classification") @ColumnInfo(name = "data_classification") var dataClassification: String? = "",
    @Json(name = "delivery_registration_center_code") @SerializedName("delivery_registration_center_code") @ColumnInfo(
        name = "delivery_registration_center_code"
    ) var deliveryRegistrationCenterCode: String? = "",
    @Json(name = "delivery_operation_center_code") @SerializedName("delivery_operation_center_code") @ColumnInfo(name = "delivery_operation_center_code") var deliveryOperationCenterCode: String? = "",
    @Json(name = "delivery_account_type") @SerializedName("delivery_account_type") @ColumnInfo(name = "delivery_account_type") var deliveryAccountType: String? = "",
    @Json(name = "delivery_customer_code") @SerializedName("delivery_customer_code") @ColumnInfo(name = "delivery_customer_code") var deliveryCustomerCode: String? = "",
    @Json(name = "delivery_classification_code") @SerializedName("delivery_classification_code") @ColumnInfo(name = "delivery_classification_code") var deliveryClassificationCode: String? = "",
    @Json(name = "delivery_freight_fare_no") @SerializedName("delivery_freight_fare_no") @ColumnInfo(name = "delivery_freight_fare_no") var deliveryFreightFareNo: String? = "",
    @Json(name = "delivery_personal_no") @SerializedName("delivery_personal_no") @ColumnInfo(name = "delivery_personal_no") var deliveryPersonalNo: String? = "",
    @Json(name = "delivery_company_identification_code") @SerializedName("delivery_company_identification_code") @ColumnInfo(
        name = "delivery_company_identification_code"
    ) var deliveryCompanyIdentificationCode: String? = "",
    @Json(name = "delivery_batch_billing_code") @SerializedName("delivery_batch_billing_code") @ColumnInfo(name = "delivery_batch_billing_code") var deliveryBatchBillingCode: String? = "",
    @Json(name = "payment_collect_registration_center_code") @SerializedName("payment_collect_registration_center_code") @ColumnInfo(
        name = "payment_collect_registration_center_code"
    ) var paymentCollectRegistrationCenterCode: String? = "",
    @Json(name = "payment_collect_operation_center_code") @SerializedName("payment_collect_operation_center_code") @ColumnInfo(
        name = "payment_collect_operation_center_code"
    ) var paymentCollectOperationCenterCode: String? = "",
    @Json(name = "payment_collect_classification") @SerializedName("payment_collect_classification") @ColumnInfo(name = "payment_collect_classification") var paymentCollectClassification: String? = "",
    @Json(name = "payment_collect_customer_code") @SerializedName("payment_collect_customer_code") @ColumnInfo(name = "payment_collect_customer_code") var paymentCollectCustomerCode: String? = "",
    @Json(name = "payment_collect_classification_code") @SerializedName("payment_collect_classification_code") @ColumnInfo(
        name = "payment_collect_classification_code"
    ) var paymentCollectClassificationCode: String? = "",
    @Json(name = "payment_collect_freight_fare_no") @SerializedName("payment_collect_freight_fare_no") @ColumnInfo(name = "payment_collect_freight_fare_no") var paymentCollectFreightFareNo: String? = "",
    @Json(name = "payment_collect_personal_no") @SerializedName("payment_collect_personal_no") @ColumnInfo(name = "payment_collect_personal_no") var paymentCollectPersonalNo: String? = "",
    @Json(name = "payment_collect_company_identification_code") @SerializedName("payment_collect_company_identification_code") @ColumnInfo(
        name = "payment_collect_company_identification_code"
    ) var paymentCollectCompanyIdentificationCode: String? = "",
    @Json(name = "payment_collect_batch_billing_code") @SerializedName("payment_collect_batch_billing_code") @ColumnInfo(
        name = "payment_collect_batch_billing_code"
    ) var paymentCollectBatchBillingCode: String? = "",
    @Json(name = "tracking_no") @SerializedName("tracking_no") @ColumnInfo(name = "tracking_no") var trackingNo: String? = "",
    @Json(name = "handling_center_code") @SerializedName("handling_center_code") @ColumnInfo(name = "handling_center_code") var handlingCenterCode: String? = "",
    @Json(name = "business_associate_code") @SerializedName("business_associate_code") @ColumnInfo(name = "business_associate_code") var businessAssociateCode: String? = "",
    @Json(name = "estimated_dest_center_code") @SerializedName("estimated_dest_center_code") @ColumnInfo(name = "estimated_dest_center_code") var estimatedDestCenterCode: String? = "",
    @Json(name = "consignment_note_classification") @SerializedName("consignment_note_classification") @ColumnInfo(name = "consignment_note_classification") var consignmentNoteClassification: String? = "",
    @Json(name = "size_item") @SerializedName("size_item") @ColumnInfo(name = "size_item") var sizeItem: String? = "",
    @Json(name = "item_code") @SerializedName("item_code") @ColumnInfo(name = "item_code") var itemCode: String? = "",
    @Json(name = "no") @SerializedName("no") @ColumnInfo(name = "no") var no: String? = "",
    @Json(name = "freight_fare") @SerializedName("freight_fare") @ColumnInfo(name = "freight_fare") var freightFare: String? = "",
    @Json(name = "items_price") @SerializedName("items_price") @ColumnInfo(name = "items_price") var itemsPrice: String? = "",
    @Json(name = "payment_collect_amount") @SerializedName("payment_collect_amount") @ColumnInfo(name = "payment_collect_amount") var paymentCollectAmount: String? = "",
    @Json(name = "service_charge_total") @SerializedName("service_charge_total") @ColumnInfo(name = "service_charge_total") var serviceChargeTotal: String? = "",
    @Json(name = "time_zone_service_charge") @SerializedName("time_zone_service_charge") @ColumnInfo(name = "time_zone_service_charge") var timeZoneServiceCharge: String? = "",
    @Json(name = "on_the_day_delivery_service_charge") @SerializedName("on_the_day_delivery_service_charge") @ColumnInfo(
        name = "on_the_day_delivery_service_charge"
    ) var onTheDayDeliveryServiceCharge: String? = "",
    @Json(name = "reserve_2") @SerializedName("reserve_2") @ColumnInfo(name = "reserve_2") var reserve2: String? = "",
    @Json(name = "reserve_3") @SerializedName("reserve_3") @ColumnInfo(name = "reserve_3") var reserve3: String? = "",
    @Json(name = "reserve_4") @SerializedName("reserve_4") @ColumnInfo(name = "reserve_4") var reserve4: String? = "",
    @Json(name = "reserve_5") @SerializedName("reserve_5") @ColumnInfo(name = "reserve_5") var reserve5: String? = "",
    @Json(name = "reserve_6") @SerializedName("reserve_6") @ColumnInfo(name = "reserve_6") var reserve6: String? = "",
    @Json(name = "reserve_7") @SerializedName("reserve_7") @ColumnInfo(name = "reserve_7") var reserve7: String? = "",
    @Json(name = "cool_service_charge") @SerializedName("cool_service_charge") @ColumnInfo(name = "cool_service_charge") var coolServiceCharge: String? = "",
    @Json(name = "handling_center_service_charge") @SerializedName("handling_center_service_charge") @ColumnInfo(name = "handling_center_service_charge") var handlingCenterServiceCharge: String? = "",
    @Json(name = "collect_service_charge") @SerializedName("collect_service_charge") @ColumnInfo(name = "collect_service_charge") var collectServiceCharge: String? = "",
    @Json(name = "ctr_contracted_customer_flag") @SerializedName("ctr_contracted_customer_flag") @ColumnInfo(name = "ctr_contracted_customer_flag") var ctrContractedCustomerFlag: String? = "",
    @Json(name = "ori_operation_base_center_code") @SerializedName("ori_operation_base_center_code") @ColumnInfo(name = "ori_operation_base_center_code") var oriOperationBaseCenterCode: String? = "",
    @Json(name = "dest_operation_base_center_code") @SerializedName("dest_operation_base_center_code") @ColumnInfo(name = "dest_operation_base_center_code") var destOperationBaseCenterCode: String? = "",
    @Json(name = "ori_region") @SerializedName("ori_region") @ColumnInfo(name = "ori_region") var oriRegion: String? = "",
    @Json(name = "dest_region") @SerializedName("dest_region") @ColumnInfo(name = "dest_region") var destRegion: String? = "",
    @Json(name = "operation_west_4_yr_mth_date") @SerializedName("operation_west_4_yr_mth_date") @ColumnInfo(name = "operation_west_4_yr_mth_date") var operationWest4YrMthDate: String? = "",
    @Json(name = "operationn_tally_west_4_yr_mth_date") @SerializedName("operationn_tally_west_4_yr_mth_date") @ColumnInfo(
        name = "operationn_tally_west_4_yr_mth_date"
    ) var operationnTallyWest4YrMthDate: String? = "",
    @Json(name = "delivery_operation_west_4_yr_mth_date") @SerializedName("delivery_operation_west_4_yr_mth_date") @ColumnInfo(
        name = "delivery_operation_west_4_yr_mth_date"
    ) var deliveryOperationWest4YrMthDate: String? = "",
    @Json(name = "delivery_tally_west_4_yr_mth_date") @SerializedName("delivery_tally_west_4_yr_mth_date") @ColumnInfo(
        name = "delivery_tally_west_4_yr_mth_date"
    ) var deliveryTallyWest4YrMthDate: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}