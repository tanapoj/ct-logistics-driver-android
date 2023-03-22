package com.scgexpress.backoffice.android.db.dao

import io.reactivex.Flowable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import io.reactivex.Single


@Dao
interface MasterDataDao {

    @get:Query("SELECT * FROM tbl_master_parcel_sizing ORDER BY id ASC")
    val parcelSizingItems: Flowable<List<TblMasterParcelSizing>>

    @Query("SELECT * FROM tbl_auth_users WHERE personal_id = :personalId")
    fun getAuthUsers(personalId: String): Single<TblAuthUsers>

    @Query("SELECT * FROM tbl_auth_users WHERE branch_id = :branchId")
    fun getAuthUsersRetention(branchId: String): Single<List<TblAuthUsers>>

    @Query("SELECT * FROM tbl_master_manifest_type")
    fun getMasterManifastType(): Flowable<List<TblMasterManifestType>>

    @Query("SELECT * FROM tbl_scg_branch WHERE id = :id")
    fun getScgBranch(id: Int): Single<TblScgBranch>

    @Query("SELECT * FROM tbl_scg_branch")
    fun getRetentionScgBranch(): Single<List<TblScgBranch>>

    @Query("SELECT * FROM tbl_scg_assort_code WHERE zipcode = :zipcode")
    fun getScgAssortCode(zipcode: String): Single<TblScgAssortCode>

    @Query("SELECT * FROM tbl_scg_region_diff WHERE id_origin_region = :idOriginRegion AND Id_destination_region = :idDestinationRegion")
    fun getScgRegionDiff(idOriginRegion: Int, idDestinationRegion: Int): Single<TblScgRegionDiff>

    @Query("SELECT * FROM tbl_master_parcel_sizing WHERE name = :name")
    fun getMasterParcelSizing(name: String): Single<TblMasterParcelSizing>

    @Query("SELECT * FROM tbl_master_parcel_properties WHERE id_service_type_level_3 = :idServiceTypeLevel3 AND id_parcel_sizing = :idParcelSizing AND status_active='Y'")
    fun getMasterParcelProperties(idServiceTypeLevel3: String, idParcelSizing: String): Single<TblMasterParcelProperties>

    @Query("SELECT * FROM tbl_organization WHERE organization_id = :organizationId")
    fun getOrganization(organizationId: Int): Single<TblOrganization>

    @Query("SELECT * FROM tbl_organization WHERE customer_code = :customerCode")
    fun getOrganizationFormCustomerCode(customerCode: String): Single<TblOrganization>

    //id_payment_type = 2 is cash (เงินสด)
    @Query("SELECT * FROM tbl_scg_cod_model WHERE  id_cod_tier = :codTierId AND id_payment_type = 2  AND status_active = 'Y' AND status_deleted = 'N'")
    fun getScgCodModel(codTierId: Int): Single<TblScgCodModel>

    @Query("SELECT * FROM tbl_scg_pricing_model WHERE id_price_tier = :idPriceTier AND id_region_diff = :idRegionDiff AND id_parcel_properties = :idParcelProperties AND status_active = 'Y' AND status_deleted = 'N'")
    fun getScgPricingModel(idPriceTier: Int, idRegionDiff: Int, idParcelProperties: Int): Single<TblScgPricingModel>

    @Query("SELECT * FROM tbl_scg_postalCode WHERE id = :id")
    fun getScgPostalCode(id: Int): Single<TblScgPostalcode>

    @Query("SELECT * FROM tbl_scg_postalCode WHERE postal_code = :postalCode")
    fun getScgPostalCode(postalCode: String): Single<TblScgPostalcode>

    @Query("SELECT * FROM tbl_scg_postalCode WHERE postal_code = :postalCode AND remote_area = 'Y'")
    fun getScgPostalCodesInRemoteArea(postalCode: String): Single<List<TblScgPostalcode>>

    @Query("SELECT * FROM tbl_scg_postalCode WHERE postal_code = :postalCode")
    fun getScgPostalCodes(postalCode: String): Single<List<TblScgPostalcode>>

    @Query("SELECT * FROM tbl_master_payment_type")
    fun getTblMasterPaymentType(): Single<List<TblMasterPaymentType>>


    /* code gen */

    @Query("DELETE FROM tbl_auth_users")
    fun clearTblAuthUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblAuthUsers(data: List<TblAuthUsers>)


    @Query("DELETE FROM tbl_manifest_items")
    fun clearTblManifestItems()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblManifestItems(data: List<TblManifestItems>)


    @Query("DELETE FROM tbl_manifest_ofd_items")
    fun clearTblManifestOfdItems()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblManifestOfdItems(data: List<TblManifestOfdItems>)


    @Query("DELETE FROM tbl_manifest_ofd_sheets")
    fun clearTblManifestOfdSheets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblManifestOfdSheets(data: List<TblManifestOfdSheets>)


    @Query("DELETE FROM tbl_manifest_sheets")
    fun clearTblManifestSheets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblManifestSheets(data: List<TblManifestSheets>)


    @Query("DELETE FROM tbl_manifest_sheets_general")
    fun clearTblManifestSheetsGeneral()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblManifestSheetsGeneral(data: List<TblManifestSheetsGeneral>)


    @Query("DELETE FROM tbl_master_customer_type")
    fun clearTblMasterCustomerType()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterCustomerType(data: List<TblMasterCustomerType>)


    @Query("DELETE FROM tbl_master_data_source")
    fun clearTblMasterDataSource()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterDataSource(data: List<TblMasterDataSource>)


    @Query("DELETE FROM tbl_master_irregular_type")
    fun clearTblMasterIrregularType()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterIrregularType(data: List<TblMasterIrregularType>)


    @Query("DELETE FROM tbl_master_manifest_type")
    fun clearTblMasterManifestType()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterManifestType(data: List<TblMasterManifestType>)


    @Query("DELETE FROM tbl_master_operation_code")
    fun clearTblMasterOperationCode()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterOperationCode(data: List<TblMasterOperationCode>)


    @Query("DELETE FROM tbl_master_parcel_properties")
    fun clearTblMasterParcelProperties()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterParcelProperties(data: List<TblMasterParcelProperties>)


    @Query("DELETE FROM tbl_master_parcel_sizing")
    fun clearTblMasterParcelSizing()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterParcelSizing(data: List<TblMasterParcelSizing>)


    @Query("DELETE FROM tbl_master_parcel_status")
    fun clearTblMasterParcelStatus()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterParcelStatus(data: List<TblMasterParcelStatus>)


    @Query("DELETE FROM tbl_master_payment_type")
    fun clearTblMasterPaymentType()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterPaymentType(data: List<TblMasterPaymentType>)


    @Query("DELETE FROM tbl_master_promocode")
    fun clearTblMasterPromocode()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterPromocode(data: List<TblMasterPromocode>)


    @Query("DELETE FROM tbl_master_retention_reason")
    fun clearTblMasterRetentionReason()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterRetentionReason(data: List<TblMasterRetentionReason>)


    @Query("DELETE FROM tbl_master_return_reason")
    fun clearTblMasterReturnReason()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterReturnReason(data: List<TblMasterReturnReason>)


    @Query("DELETE FROM tbl_master_service_type_level_1")
    fun clearTblMasterServiceTypeLevel1()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterServiceTypeLevel1(data: List<TblMasterServiceTypeLevel1>)


    @Query("DELETE FROM tbl_master_service_type_level_2")
    fun clearTblMasterServiceTypeLevel2()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterServiceTypeLevel2(data: List<TblMasterServiceTypeLevel2>)


    @Query("DELETE FROM tbl_master_service_type_level_3")
    fun clearTblMasterServiceTypeLevel3()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterServiceTypeLevel3(data: List<TblMasterServiceTypeLevel3>)


    @Query("DELETE FROM tbl_parcel_status")
    fun clearTblParcelStatus()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblParcelStatus(data: List<TblParcelStatus>)


    @Query("DELETE FROM tbl_price")
    fun clearTblPrice()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblPrice(data: List<TblPrice>)


    @Query("DELETE FROM tbl_returnrerute_sheets")
    fun clearTblReturnreruteSheets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblReturnreruteSheets(data: List<TblReturnreruteSheets>)


    @Query("DELETE FROM tbl_scg_assort_code")
    fun clearTblScgAssortCode()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgAssortCode(data: List<TblScgAssortCode>)


    @Query("DELETE FROM tbl_scg_branch")
    fun clearTblScgBranch()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgBranch(data: List<TblScgBranch>)


    @Query("DELETE FROM tbl_scg_cod_model")
    fun clearTblScgCodModel()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgCodModel(data: List<TblScgCodModel>)


    @Query("DELETE FROM tbl_scg_cod_tier")
    fun clearTblScgCodTier()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgCodTier(data: List<TblScgCodTier>)


    @Query("DELETE FROM tbl_scg_consignment")
    fun clearTblScgConsignment()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgConsignment(data: List<TblScgConsignment>)


    @Query("DELETE FROM tbl_scg_consignment_del")
    fun clearTblScgConsignmentDel()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgConsignmentDel(data: List<TblScgConsignmentDel>)


    @Query("DELETE FROM tbl_scg_neko_billing")
    fun clearTblScgNekoBilling()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgNekoBilling(data: List<TblScgNekoBilling>)


    @Query("DELETE FROM tbl_scg_neko_tracking")
    fun clearTblScgNekoTracking()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgNekoTracking(data: List<TblScgNekoTracking>)


    @Query("DELETE FROM tbl_scg_parcel_returnrerute")
    fun clearTblScgParcelReturnrerute()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgParcelReturnrerute(data: List<TblScgParcelReturnrerute>)


    @Query("DELETE FROM tbl_scg_parcel_tracking")
    fun clearTblScgParcelTracking()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgParcelTracking(data: List<TblScgParcelTracking>)


    @Query("DELETE FROM tbl_scg_parcels")
    fun clearTblScgParcels()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgParcels(data: List<TblScgParcels>)


    @Query("DELETE FROM tbl_scg_postalCode")
    fun clearTblScgPostalcode()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgPostalcode(data: List<TblScgPostalcode>)


    @Query("DELETE FROM tbl_scg_price_tier")
    fun clearTblScgPriceTier()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgPriceTier(data: List<TblScgPriceTier>)


    @Query("DELETE FROM tbl_scg_pricing_model")
    fun clearTblScgPricingModel()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgPricingModel(data: List<TblScgPricingModel>)


    @Query("DELETE FROM tbl_scg_receipt")
    fun clearTblScgReceipt()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgReceipt(data: List<TblScgReceipt>)


    @Query("DELETE FROM tbl_scg_region")
    fun clearTblScgRegion()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgRegion(data: List<TblScgRegion>)


    @Query("DELETE FROM tbl_scg_region_diff")
    fun clearTblScgRegionDiff()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgRegionDiff(data: List<TblScgRegionDiff>)


    @Query("DELETE FROM tbl_master_billing_company")
    fun clearTblMasterBillingCompany()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblMasterBillingCompany(data: List<TblMasterBillingCompany>)


    @Query("DELETE FROM tbl_organization")
    fun clearTblOrganization()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblOrganization(data: List<TblOrganization>)


    @Query("DELETE FROM tbl_scg_api_token")
    fun clearTblScgApiToken()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgApiToken(data: List<TblScgApiToken>)


    @Query("DELETE FROM tbl_scg_unregistered_tracking")
    fun clearTblScgUnregisteredTracking()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblScgUnregisteredTracking(data: List<TblScgUnregisteredTracking>)


    @Query("DELETE FROM tbl_temp_cal")
    fun clearTblTempCal()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTblTempCal(data: List<TblTempCal>)

}