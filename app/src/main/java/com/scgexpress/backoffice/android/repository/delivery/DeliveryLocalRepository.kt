package com.scgexpress.backoffice.android.repository.delivery

import com.google.common.collect.Lists
import com.scgexpress.backoffice.android.db.dao.DeliveryDao
import com.scgexpress.backoffice.android.db.entity.DeliveryEntity
import com.scgexpress.backoffice.android.model.Delivery
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("legacy delivery")
@Singleton
class DeliveryLocalRepository @Inject
constructor(private val dao: DeliveryDao) {

    val lastGroupID: Single<String>
        get() {
            return dao.lastGroupID
        }

    fun getDelivery(userId: String, manifestID: String, groupID: String, statusID: String): Flowable<List<Delivery>> {
        return dao.getDelivery(userId, manifestID, groupID, false)
            .subscribeOn(Schedulers.io())
            .map(this::pareDeliveryList)
    }

    fun getDeliveryScan(userId: String, manifestID: String, statusID: String): Flowable<List<Delivery>> {
        return dao.getDelivery(userId, manifestID, false)
            .subscribeOn(Schedulers.io())
            .map(this::pareDeliveryList)
    }

    fun getUnSyncedDelivery(userId: String): Flowable<List<Delivery>> {
        //TODO: mock data
//        return Flowable.fromArray(listOf(
//                Delivery(senderCode = "100212", trackingNumber = "A111931730525A", manifestID = "1"),
//                Delivery(senderCode = "100212", manifestID = "1"),
//                Delivery(senderCode = "2222", trackingNumber = "A111611368772A", manifestID = "1"),
//                Delivery(senderCode = "2222", manifestID = "1"),
//                Delivery(senderCode = "2222", manifestID = "1")
//        ))
        return dao.getUnSyncedDelivery(userId)
            .subscribeOn(Schedulers.io())
            .map(this::pareDeliveryList)
    }

    fun updateDeliveryStatus(modelList: List<Delivery>, statusID: String) {
        Completable.fromCallable {
            val modelList = Lists.transform(
                modelList
            ) { input ->
                DeliveryEntity(
                    groupID = input!!.groupID!!,
                    userId = input.userId!!,
                    manifestID = input.manifestID!!,
                    trackingNumber = input.trackingNumber!!,
                    senderCode = input.senderCode,
                    senderName = input.senderName,
                    statusID = statusID,
                    codAmount = input.codAmount,
                    codCollected = input.codCollected,
                    orderDate = input.orderDate,
                    deleted = input.deleted,
                    sync = input.sync,
                    timestamp = input.timestamp,
                    note = input.note
                )
            }
            dao.insertOrReplaceDelivery(modelList)
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updateDelivery(model: Delivery) {
        Completable.fromCallable {
            dao.insertOrReplaceDelivery(
                DeliveryEntity(
                    model.groupID!!,
                    model.userId!!,
                    model.manifestID!!,
                    model.trackingNumber!!,
                    model.senderCode,
                    model.senderName,
                    model.statusID,
                    model.codAmount,
                    model.codCollected,
                    model.orderDate,
                    model.deleted,
                    model.sync,
                    model.timestamp,
                    model.note
                )
            )
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteDelivery(userId: String, manifestID: String, groupID: String, statusID: String) {
        Completable.fromCallable {
            dao.deleteDelivery(userId, manifestID, groupID, statusID)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun pareDeliveryList(entityList: List<DeliveryEntity>?): List<Delivery>? {
        if (entityList != null) {
            val modelList: ArrayList<Delivery> = arrayListOf()
            for (entity in entityList) {
                modelList.add(Delivery(entity))
            }
            return modelList
        }
        return null
    }
}