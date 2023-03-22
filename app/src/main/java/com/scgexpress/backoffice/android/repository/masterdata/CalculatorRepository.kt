package com.scgexpress.backoffice.android.repository.masterdata

import com.scgexpress.backoffice.android.common.isAllowCod
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculatorRepository @Inject
constructor(
        private val schedule: RxThreadScheduler,
        private val dao: MasterDataDao
) {

    private val TAG = "-MasterData"

    fun deliveryFee(
        customerCode: String,
        senderBranchId: Int,
        zipCode: String,
        parcelSizingId: String,
        serviceTypeLevel3Id: String
    ): Single<TblScgPricingModel> {

        Timber.d("CalculateRepo.deliveryFee(customerCode:$customerCode senderBranchId:$senderBranchId, zipCode:$zipCode parcelSizingId:$parcelSizingId serviceTypeLevel3Id:$serviceTypeLevel3Id)")

        //step 1: find region diff
        val senderBranch = dao.getScgBranch(senderBranchId)
        val destinationBranch = dao.getScgAssortCode(zipCode)

        val regionDiff =
            Single.zip(senderBranch, destinationBranch, BiFunction { sender: TblScgBranch, destination: TblScgAssortCode ->
                Pair(sender, destination)
            }).flatMap {(src, dest) ->
                Timber.d("1 idRegion = ${src.regionId}, ${dest.idRegion}")
                dao.getScgRegionDiff(src.regionId, dest.idRegion)
            }

        //step 2: find parcel properties
        val parcelProperties = dao.getMasterParcelProperties(serviceTypeLevel3Id, parcelSizingId)

        //step 3: find price tier
        val priceTier = dao.getOrganizationFormCustomerCode(customerCode)

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
            Timber.d("3 regionDiffId $regionDiffId")
            Timber.d("3 parcelPropertiesId $parcelPropertiesId")
            Timber.d("3 priceTierId $priceTierId")
            dao.getScgPricingModel(priceTierId, regionDiffId, parcelPropertiesId)
        }
    }

    fun codFee(codAmount: Double, customerCode: String): Single<Double> {

        return dao.getOrganizationFormCustomerCode(customerCode)
            .flatMap {
                if (it.isAllowCod()) {
                    Timber.d("4 regionDiffId ${it.codTierId}")
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

                Timber.d("5 regionDiffId $codAmountMin")
                Timber.d("5 codPricePercent $codPricePercent")
                Timber.d("5 codPriceMin $codPriceMin")
                Timber.d("5 codFeeMin $codFeeMin")

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

    fun calculateSummaryFee(
        customerCode: String,
        trackingList: List<PickupScanningTrackingEntity>
    ): FeeSummary {
        Timber.d("summary fee: customer:$customerCode and $trackingList")
        return FeeSummary().apply {
            for (data in trackingList) {
                deliveryFee += data.deliveryFee
                codFee += data.codFee ?: .0
                cartonFee += data.cartonFee ?: .0
            }
            deliveryFee = calculateFreightFareExtraForC2Customer(deliveryFee, trackingList.size, customerCode)
            totalFee = deliveryFee + codFee + cartonFee
        }
    }

    private val C2_CUSTOMER = "999999"
    private val PARCEL_COUNT_LOWER_BOUND = 10

    private fun calculateFreightFareExtraForC2Customer(
        baseDeliveryFee: Double,
        parcelCount: Int,
        customerCode: String
    ): Double {
        var fee = baseDeliveryFee
//        if (customerCode == C2_CUSTOMER && parcelCount < 3) {
//            fee += 20.0
//        }
        if (customerCode == C2_CUSTOMER && parcelCount < PARCEL_COUNT_LOWER_BOUND) {
            fee += 60.0
        }
        return fee
    }

    companion object {
        data class FeeSummary(
            var deliveryFee: Double = .0,
            var codFee: Double = .0,
            var cartonFee: Double = .0,
            var totalFee: Double = .0
        )
    }

}