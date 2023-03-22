package com.scgexpress.backoffice.android.model

import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.google.gson.annotations.SerializedName

data class MasterData(
        @SerializedName("tbl_auth_users") var authUsers: List<TblAuthUsers>? = listOf(),
        @SerializedName("tbl_manifest_items") var manifestItems: List<TblManifestItems>? = listOf(),
        @SerializedName("tbl_manifest_ofd_items") var manifestOfdItems: List<TblManifestOfdItems>? = listOf(),
        @SerializedName("tbl_manifest_ofd_sheets") var manifestOfdSheets: List<TblManifestOfdSheets>? = listOf(),
        @SerializedName("tbl_manifest_sheets") var manifestSheets: List<TblManifestSheets>? = listOf(),
        @SerializedName("tbl_manifest_sheets_general") var manifestSheetsGeneral: List<TblManifestSheetsGeneral>? = listOf(),
        @SerializedName("tbl_master_billing_company") var masterBillingCompany: List<TblMasterBillingCompany>? = listOf(),
        @SerializedName("tbl_master_customer_type") var masterCustomerType: List<TblMasterCustomerType>? = listOf(),
        @SerializedName("tbl_master_data_source") var masterDataSource: List<TblMasterDataSource>? = listOf(),
        @SerializedName("tbl_master_irregular_type") var masterIrregularType: List<TblMasterIrregularType>? = listOf(),

        @SerializedName("tbl_master_manifest_type") var masterManifestType: List<TblMasterManifestType>? = listOf(),
        @SerializedName("tbl_master_operation_code") var masterOperationCode: List<TblMasterOperationCode>? = listOf(),
        @SerializedName("tbl_master_parcel_properties") var masterParcelProperties: List<TblMasterParcelProperties>? = listOf(),
        @SerializedName("tbl_master_parcel_sizing") var masterParcelSizing: List<TblMasterParcelSizing>? = listOf(),
        @SerializedName("tbl_master_parcel_status") var masterParcelStatus: List<TblMasterParcelStatus>? = listOf(),
        @SerializedName("tbl_master_payment_type") var masterPaymentType: List<TblMasterPaymentType>? = listOf(),
        @SerializedName("tbl_master_promocode") var masterPromocode: List<TblMasterPromocode>? = listOf(),
        @SerializedName("tbl_master_retention_reason") var masterRetentionReason: List<TblMasterRetentionReason>? = listOf(),
        @SerializedName("tbl_master_return_reason") var masterReturnReason: List<TblMasterReturnReason>? = listOf(),
        @SerializedName("tbl_master_service_type_level_1") var masterServiceTypeLevel1: List<TblMasterServiceTypeLevel1>? = listOf(),

        @SerializedName("tbl_master_service_type_level_2") var masterServiceTypeLevel2: List<TblMasterServiceTypeLevel2>? = listOf(),
        @SerializedName("tbl_master_service_type_level_3") var masterServiceTypeLevel3: List<TblMasterServiceTypeLevel3>? = listOf(),
        @SerializedName("tbl_organization") var organization: List<TblOrganization>? = listOf(),
        @SerializedName("tbl_parcel_status") var parcelStatus: List<TblParcelStatus>? = listOf(),
        @SerializedName("tbl_price") var price: List<TblPrice>? = listOf(),
        @SerializedName("tbl_returnrerute_sheets") var returnreruteSheets: List<TblReturnreruteSheets>? = listOf(),
        @SerializedName("tbl_scg_api_token") var scgApiToken: List<TblScgApiToken>? = listOf(),
        @SerializedName("tbl_scg_assort_code") var scgAssortCode: List<TblScgAssortCode>? = listOf(),
        @SerializedName("tbl_scg_branch") var scgBranch: List<TblScgBranch>? = listOf(),
        @SerializedName("tbl_scg_cod_model") var scgCodModel: List<TblScgCodModel>? = listOf(),

        @SerializedName("tbl_scg_cod_tier") var scgCodTier: List<TblScgCodTier>? = listOf(),
        @SerializedName("tbl_scg_consignment") var scgConsignment: List<TblScgConsignment>? = listOf(),
        @SerializedName("tbl_scg_consignment_del") var scgConsignmentDel: List<TblScgConsignmentDel>? = listOf(),
        @SerializedName("tbl_scg_neko_billing") var scgNekoBilling: List<TblScgNekoBilling>? = listOf(),
        @SerializedName("tbl_scg_neko_tracking") var scgNekoTracking: List<TblScgNekoTracking>? = listOf(),
        @SerializedName("tbl_scg_parcel_returnrerute") var scgParcelReturnrerute: List<TblScgParcelReturnrerute>? = listOf(),
        @SerializedName("tbl_scg_parcels") var scgParcels: List<TblScgParcels>? = listOf(),
        @SerializedName("tbl_scg_parcel_tracking") var scgParcelTracking: List<TblScgParcelTracking>? = listOf(),
        @SerializedName("tbl_scg_postalCode") var scgPostalcode: List<TblScgPostalcode>? = listOf(),
        @SerializedName("tbl_scg_price_tier") var scgPriceTier: List<TblScgPriceTier>? = listOf(),
        @SerializedName("tbl_scg_pricing_model") var scgPricingModel: List<TblScgPricingModel>? = listOf(),

        @SerializedName("tbl_scg_receipt") var scgReceipt: List<TblScgReceipt>? = listOf(),
        @SerializedName("tbl_scg_region") var scgRegion: List<TblScgRegion>? = listOf(),
        @SerializedName("tbl_scg_region_diff") var scgRegionDiff: List<TblScgRegionDiff>? = listOf(),
        @SerializedName("tbl_scg_unregistered_tracking") var scgUnregisteredTracking: List<TblScgUnregisteredTracking>? = listOf(),
        @SerializedName("tbl_temp_cal") var tempCal: List<TblTempCal>?
)
