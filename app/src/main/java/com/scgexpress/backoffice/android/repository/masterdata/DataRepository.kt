package com.scgexpress.backoffice.android.repository.masterdata

import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.common.thenEmit
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.ParcelSizing
import com.scgexpress.backoffice.android.model.ServiceTypeWithSizing
import com.scgexpress.backoffice.android.repository.delivery.ContextWrapper
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.functions.FunctionN

typealias IdAndValue = Pair<Int, String>

@Singleton
class DataRepository @Inject
constructor(
    private val scheduler: RxThreadScheduler,
    private val masterDataDao: MasterDataDao
) {

    private val SERVICE_TYPE_NORMAL = "Normal"
    private val SERVICE_TYPE_CHILED = "Chilled"
    private val SERVICE_TYPE_FROZEN = "Frozen"

    private val TAG = "-MasterData"

    fun getAssortCode(zipcode: String): Single<TblScgAssortCode> {
        return masterDataDao.getScgAssortCode(zipcode)
    }

    fun getScgBranch(): Single<List<TblScgBranch>> {
        return masterDataDao.getRetentionScgBranch().scheduleBy(scheduler)
    }

    fun getScgPostalCode(id: Int): Single<TblScgPostalcode> {
        return masterDataDao.getScgPostalCode(id)
    }

    fun getScgPostalCode(zipcode: String): Single<TblScgPostalcode> {
        return masterDataDao.getScgPostalCode(zipcode)
    }

    fun getScgPostalCodes(
        zipcode: String,
        remoteAreaOnly: Boolean = false
    ): Single<List<TblScgPostalcode>> {
        return if (remoteAreaOnly) {
            masterDataDao.getScgPostalCodesInRemoteArea(zipcode)
        } else {
            masterDataDao.getScgPostalCodes(zipcode)
        }
    }

    fun getScgSD(branchId: String): Single<List<TblAuthUsers>> {
        return masterDataDao.getAuthUsersRetention(branchId).scheduleBy(scheduler)
    }

    fun getMasterParcelProperties(
        idServiceTypeLevel3: String,
        idParcelSizing: String
    ): Single<TblMasterParcelProperties> {
        return masterDataDao.getMasterParcelProperties(idServiceTypeLevel3, idParcelSizing)
    }

    val masterManifestType: Flowable<List<TblMasterManifestType>>
        get() {
            return masterDataDao.getMasterManifastType()
                .subscribeOn(Schedulers.io())
        }

    fun getOrganization(organizationId: Int): Single<TblOrganization> {
        return masterDataDao.getOrganization(organizationId).scheduleBy(scheduler)
    }

    fun getOrganization(customerCode: String): Single<TblOrganization> {
        return masterDataDao.getOrganizationFormCustomerCode(customerCode).scheduleBy(scheduler)
    }

    fun getActivePaymentMethodList(): Single<List<TblMasterPaymentType>> {
        return masterDataDao.getTblMasterPaymentType().map { list ->
            list.filter { it.name != "undefined" }
        }.scheduleBy(scheduler)
    }

    //TODO: must Implemented
    fun getServiceTypeWithSizing(context: ContextWrapper): Single<List<ServiceTypeWithSizing>> {
        return Single.create{ emitter ->
            getAllServiceType(context).scheduleBy(scheduler).subscribe({ serviceTypeList ->
                serviceTypeList.map { (id, name) ->
                    getParcelSize(context, name).map { idValueList ->
                        val parcelSizeList = idValueList.map { (id, name) ->
                            ParcelSizing(id, name)
                        }
                        ServiceTypeWithSizing(id, name, parcelSizeList)
                    }
                }.let {
                    Single.zip(it) { list: Array<Any> ->
                        list.map { it as ServiceTypeWithSizing }.toList()
                    }
                }.scheduleBy(scheduler).subscribe({
                    emitter.onSuccess(it)
                }, emitter::onError)
            }, emitter::onError)
        }
    }

    private fun getAllServiceType(context: ContextWrapper): Single<List<IdAndValue>> {
        val cod = context.getStringArray(R.array.service_type_name_cod)
        val nonCod = context.getStringArray(R.array.service_type_name_non_cod)
        val data = (cod + nonCod).map {
            val (id, name) = it.split(",")
            id.toInt() to name
        }
        return Single.just(data)
    }

    private fun getParcelSize(context: ContextWrapper, service: String): Single<List<IdAndValue>> {
        return Single.create { emitter ->
            val filterOnlyId = when (service) {
                SERVICE_TYPE_NORMAL -> null
                SERVICE_TYPE_CHILED -> context.getIntArray(R.array.parcel_sizing_chilled_display_id)
                SERVICE_TYPE_FROZEN -> context.getIntArray(R.array.parcel_sizing_frozen_display_id)
                else -> throw IllegalStateException("Service must be [$SERVICE_TYPE_NORMAL, $SERVICE_TYPE_CHILED, $SERVICE_TYPE_FROZEN], but '$service' was passed")
            }

            masterDataDao.parcelSizingItems
                .scheduleBy(scheduler)
                .subscribe({ list ->
                    list.filter {
                        (filterOnlyId == null || it.id in filterOnlyId) && it.name != "Undefined"
                    }.map {
                        it.id to it.name!!
                    }.also {
                        emitter.onSuccess(it)
                    }
                }, emitter::onError, {})
        }
    }
}