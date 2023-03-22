package com.scgexpress.backoffice.android.repository.masterdata

import android.util.Log
import com.scgexpress.backoffice.android.api.MasterDataService
import com.scgexpress.backoffice.android.common.isAllowCod
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.scgexpress.backoffice.android.model.MasterData
import com.scgexpress.backoffice.android.model.MasterDataVersion
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterDataRepository @Inject
constructor(private val dao: MasterDataDao, private val service: MasterDataService) {

    private val TAG = "-MasterData"


    fun getVersion(): Flowable<MasterDataVersion> {
        return service.getVersion()
    }

    fun getAssortCode(zipcode: String): Single<TblScgAssortCode> {
        return dao.getScgAssortCode(zipcode)
    }

    fun getScgPostalCode(id: Int): Single<TblScgPostalcode> {
        return dao.getScgPostalCode(id)
    }

    fun getScgPostalCode(zipcode: String): Single<TblScgPostalcode> {
        return dao.getScgPostalCode(zipcode)
    }

    fun getScgPostalCodes(zipcode: String, remoteAreaOnly: Boolean = false): Single<List<TblScgPostalcode>> {
        return if (remoteAreaOnly) {
            dao.getScgPostalCodesInRemoteArea(zipcode)
        } else {
            dao.getScgPostalCodes(zipcode)
        }
    }

    fun getMasterParcelProperties(
        idServiceTypeLevel3: String,
        idParcelSizing: String
    ): Single<TblMasterParcelProperties> {
        return dao.getMasterParcelProperties(idServiceTypeLevel3, idParcelSizing)
    }

    fun deliveryFee(
        customerCode: String,
        senderBranchId: Int,
        zipcode: String,
        trackingId: String,
        parcelSizingId: String,
        serviceTypeLevel3Id: String
    ): Single<TblScgPricingModel> {

        //verify input
        //TODO:
//        if (!trackingId.isTrackingCod()) {
//            throw IllegalArgumentException("tracking id $trackingId is not COD (must start with 11)")
//        }
//        if (trackingId.isTrackingCod() && !arrayOf("5", "6", "7").contains(serviceTypeLevel3Id)) {
//            throw IllegalArgumentException("tracking id $trackingId is COD serviceTypeLevel3Id must be 5, 6, 7 ($serviceTypeLevel3Id is given)")
//        }
//        if (!trackingId.isTrackingCod() && !arrayOf("2", "3", "4").contains(serviceTypeLevel3Id)) {
//            throw IllegalArgumentException("tracking id $trackingId is non-COD serviceTypeLevel3Id must be 2, 3, 4 ($serviceTypeLevel3Id is given)")
//        }

        //step 1: find region diff
        val senderBranch = dao.getScgBranch(senderBranchId)
        val destination = dao.getScgAssortCode(zipcode)

        val regionDiff =
            Single.zip(senderBranch, destination, BiFunction { sender: TblScgBranch, destination: TblScgAssortCode ->
                Pair(sender, destination)
            }).flatMap {
                Log.i(TAG, "1 idRegion = ${it.first.regionId}, ${it.second.idRegion}")
                dao.getScgRegionDiff(it.first.regionId, it.second.idRegion)
            }

        //step 2: find parcel properties
        val parcelProperties = dao.getMasterParcelProperties(serviceTypeLevel3Id, parcelSizingId)
//        parcelProperties
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.i(TAG, "2 parcel properties id " + it.id)
//                }, {
//                    Log.i(TAG, "NO DATA!")
//                })

        //step 3: find price tier
        val priceTier = dao.getOrganizationFormCustomerCode(customerCode)
//        priceTier
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.i(TAG, "2 price tier " + it.priceTierId)
//                }, {
//                    Log.i(TAG, "NO DATA!")
//                })

        //step 4: combine
        return Single.zip(
            regionDiff,
            parcelProperties,
            priceTier,
            Function3 { regionDiff: TblScgRegionDiff, parcelProperties: TblMasterParcelProperties, priceTier: TblOrganization ->
                Triple(regionDiff, parcelProperties, priceTier)
            }).flatMap {
            val regionDiffId = it.first.id
            val parcelPropertiesId = it.second.id
            val priceTierId = it.third.priceTierId
            Log.i(TAG, "3 regionDiffId $regionDiffId")
            Log.i(TAG, "3 parcelPropertiesId $parcelPropertiesId")
            Log.i(TAG, "3 priceTierId $priceTierId")
            dao.getScgPricingModel(priceTierId, regionDiffId, parcelPropertiesId)
        }
    }

    fun codFee(codAmount: Double, customerCode: String, trackingId: String? = null): Single<Double> {

//        trackingId?.let {
//            if (!trackingId.isTrackingCod()) {
//                throw IllegalArgumentException("tracking id $trackingId is not COD (must start with 11)")
//            }
//        }

        return dao.getOrganizationFormCustomerCode(customerCode)
            .flatMap {
                if (it.isAllowCod()) {
                    Log.i(TAG, "4 regionDiffId ${it.codTierId}")
                    dao.getScgCodModel(it.codTierId)
                } else {
                    throw IllegalArgumentException("organization ${it.organizationId} not allow COD")
                }
            }
            .map {
                val codAmountMin = it.codAmountMin?.toDoubleOrNull() ?: 0.0
                val codPricePercent = it.codPricePercent?.toDoubleOrNull() ?: 0.0
                val codPriceMin = it.codPriceMin?.toDoubleOrNull() ?: 0.0
                val codFeeMin = it.codFeeMin?.toDoubleOrNull() ?: 0.0

                Log.i(TAG, "5 regionDiffId $codAmountMin")
                Log.i(TAG, "5 codPricePercent $codPricePercent")
                Log.i(TAG, "5 codPriceMin $codPriceMin")
                Log.i(TAG, "5 codFeeMin $codFeeMin")

                val codFee = if (codAmountMin > 0) {
                    if (codAmount < codAmountMin) {
                        codPriceMin
                    } else {
                        codAmount * codPricePercent / 100
                    }
                } else {
                    codAmount * codPricePercent / 100
                }

                val codFeeAmount = if (codFeeMin > 0) {
                    if (codFee < codFeeMin) {
                        codPriceMin
                    } else {
                        codFee
                    }
                } else {
                    codFee
                }

                //Pair(codFee, codFeeAmount)
                codFeeAmount
            }
    }

    fun clearAll(): Completable {
        return Completable.fromCallable {
            dao.clearTblAuthUsers()
            dao.clearTblManifestItems()
            dao.clearTblManifestOfdItems()
            dao.clearTblManifestOfdSheets()
            dao.clearTblManifestSheets()
            dao.clearTblManifestSheetsGeneral()
            dao.clearTblMasterCustomerType()
            dao.clearTblMasterDataSource()
            dao.clearTblMasterIrregularType()
            dao.clearTblMasterManifestType()
            dao.clearTblMasterOperationCode()
            dao.clearTblMasterParcelProperties()
            dao.clearTblMasterParcelSizing()
            dao.clearTblMasterParcelStatus()
            dao.clearTblMasterPaymentType()
            dao.clearTblMasterPromocode()
            dao.clearTblMasterRetentionReason()
            dao.clearTblMasterReturnReason()
            dao.clearTblMasterServiceTypeLevel1()
            dao.clearTblMasterServiceTypeLevel2()
            dao.clearTblMasterServiceTypeLevel3()
            dao.clearTblParcelStatus()
            dao.clearTblPrice()
            dao.clearTblReturnreruteSheets()
            dao.clearTblScgAssortCode()
            dao.clearTblScgBranch()
            dao.clearTblScgCodModel()
            dao.clearTblScgCodTier()
            dao.clearTblScgConsignment()
            dao.clearTblScgConsignmentDel()
            dao.clearTblScgNekoBilling()
            dao.clearTblScgNekoTracking()
            dao.clearTblScgParcelReturnrerute()
            dao.clearTblScgParcelTracking()
            dao.clearTblScgParcels()
            dao.clearTblScgPostalcode()
            dao.clearTblScgPriceTier()
            dao.clearTblScgPricingModel()
            dao.clearTblScgReceipt()
            dao.clearTblScgRegion()
            dao.clearTblScgRegionDiff()
            dao.clearTblMasterBillingCompany()
            dao.clearTblOrganization()
            dao.clearTblScgApiToken()
            dao.clearTblScgUnregisteredTracking()
            dao.clearTblTempCal()
            Completable.complete()
        }
    }

    fun insertAll(masterData: MasterData): Observable<Int> {
//        return Observable.create(ObservableOnSubscribe<Int> { emitter ->
        return Observable.create { emitter ->
            //masterData.masterManifestType?.let { dao.insertAllTblMasterManifestType(it) }
            val tasks = listOf(
                { masterData.authUsers?.let { dao.insertAllTblAuthUsers(it) } },
                { masterData.manifestItems?.let { dao.insertAllTblManifestItems(it) } },
                { masterData.manifestOfdItems?.let { dao.insertAllTblManifestOfdItems(it) } },
                { masterData.manifestOfdSheets?.let { dao.insertAllTblManifestOfdSheets(it) } },
                { masterData.manifestSheets?.let { dao.insertAllTblManifestSheets(it) } },
                { masterData.manifestSheetsGeneral?.let { dao.insertAllTblManifestSheetsGeneral(it) } },
                { masterData.masterCustomerType?.let { dao.insertAllTblMasterCustomerType(it) } },
                { masterData.masterDataSource?.let { dao.insertAllTblMasterDataSource(it) } },
                { masterData.masterIrregularType?.let { dao.insertAllTblMasterIrregularType(it) } },
                { masterData.masterManifestType?.let { dao.insertAllTblMasterManifestType(it) } },
                { masterData.masterOperationCode?.let { dao.insertAllTblMasterOperationCode(it) } },
                { masterData.masterParcelProperties?.let { dao.insertAllTblMasterParcelProperties(it) } },
                { masterData.masterParcelSizing?.let { dao.insertAllTblMasterParcelSizing(it) } },
                { masterData.masterParcelStatus?.let { dao.insertAllTblMasterParcelStatus(it) } },
                { masterData.masterPaymentType?.let { dao.insertAllTblMasterPaymentType(it) } },
                { masterData.masterPromocode?.let { dao.insertAllTblMasterPromocode(it) } },
                { masterData.masterRetentionReason?.let { dao.insertAllTblMasterRetentionReason(it) } },
                { masterData.masterReturnReason?.let { dao.insertAllTblMasterReturnReason(it) } },
                { masterData.masterServiceTypeLevel1?.let { dao.insertAllTblMasterServiceTypeLevel1(it) } },
                { masterData.masterServiceTypeLevel2?.let { dao.insertAllTblMasterServiceTypeLevel2(it) } },
                { masterData.masterServiceTypeLevel3?.let { dao.insertAllTblMasterServiceTypeLevel3(it) } },
                { masterData.parcelStatus?.let { dao.insertAllTblParcelStatus(it) } },
                { masterData.price?.let { dao.insertAllTblPrice(it) } },
                { masterData.returnreruteSheets?.let { dao.insertAllTblReturnreruteSheets(it) } },
                { masterData.scgAssortCode?.let { dao.insertAllTblScgAssortCode(it) } },
                { masterData.scgBranch?.let { dao.insertAllTblScgBranch(it) } },
                { masterData.scgCodModel?.let { dao.insertAllTblScgCodModel(it) } },
                { masterData.scgCodTier?.let { dao.insertAllTblScgCodTier(it) } },
                { masterData.scgConsignment?.let { dao.insertAllTblScgConsignment(it) } },
                { masterData.scgConsignmentDel?.let { dao.insertAllTblScgConsignmentDel(it) } },
                { masterData.scgNekoBilling?.let { dao.insertAllTblScgNekoBilling(it) } },
                { masterData.scgNekoTracking?.let { dao.insertAllTblScgNekoTracking(it) } },
                { masterData.scgParcelReturnrerute?.let { dao.insertAllTblScgParcelReturnrerute(it) } },
                { masterData.scgParcelTracking?.let { dao.insertAllTblScgParcelTracking(it) } },
                { masterData.scgParcels?.let { dao.insertAllTblScgParcels(it) } },
                { masterData.scgPostalcode?.let { dao.insertAllTblScgPostalcode(it) } },
                { masterData.scgPriceTier?.let { dao.insertAllTblScgPriceTier(it) } },
                { masterData.scgPricingModel?.let { dao.insertAllTblScgPricingModel(it) } },
                { masterData.scgReceipt?.let { dao.insertAllTblScgReceipt(it) } },
                { masterData.scgRegion?.let { dao.insertAllTblScgRegion(it) } },
                { masterData.scgRegionDiff?.let { dao.insertAllTblScgRegionDiff(it) } },
                { masterData.masterBillingCompany?.let { dao.insertAllTblMasterBillingCompany(it) } },
                { masterData.organization?.let { dao.insertAllTblOrganization(it) } },
                { masterData.scgApiToken?.let { dao.insertAllTblScgApiToken(it) } },
                { masterData.scgUnregisteredTracking?.let { dao.insertAllTblScgUnregisteredTracking(it) } },
                { masterData.tempCal?.let { dao.insertAllTblTempCal(it) } }
            )
            val total = tasks.size
            var done = 0

            tasks.forEach { task ->
                task()
                done++
                emitter.onNext(100 * done / total)
            }

            emitter.onComplete()

        }
    }

    val masterManifestType: Flowable<List<TblMasterManifestType>>
        get() {
            return dao.getMasterManifastType()
                .subscribeOn(Schedulers.io())
        }

}