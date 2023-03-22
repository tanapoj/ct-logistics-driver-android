package com.scgexpress.backoffice.android.repository.delivery

import com.google.common.collect.Lists
import com.scgexpress.backoffice.android.db.dao.TrackingPositionDao
import com.scgexpress.backoffice.android.db.entity.TrackingPositionEntity
import com.scgexpress.backoffice.android.model.OfdItemPosition
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingPositionLocalRepository @Inject
constructor(private val dao: TrackingPositionDao) {

    val trackingPositionItems: Flowable<List<OfdItemPosition>>
        get() {
            return dao.trackingPositionItems
                .subscribeOn(Schedulers.io())
                .map(this::pareTrackingPositionList)
        }

    fun getTrackingPosition(userId: String, manifestID: String): Flowable<List<OfdItemPosition>> {
        return dao.getTrackingPositions(userId, manifestID)
            .subscribeOn(Schedulers.io())
            .map(this::pareTrackingPositionList)
    }

    fun updateTrackingPosition(modelList: List<OfdItemPosition>) {
        Completable.fromCallable {
            val modelList = Lists.transform(
                modelList
            ) { input ->
                TrackingPositionEntity(
                    userId = input!!.userId!!,
                    manifestID = input.manifestID!!,
                    trackingNumber = input.itemID!!,
                    position = input.position
                )
            }
            dao.insertOrReplaceTrackingPosition(modelList)
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updateTrackingPosition(model: OfdItemPosition) {
        Completable.fromCallable {
            dao.insertOrReplaceTrackingPosition(
                TrackingPositionEntity(
                    model.userId!!,
                    model.manifestID!!,
                    model.itemID!!,
                    model.position
                )
            )
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteTrackingPosition(userId: String, manifestID: String, trackingNumber: String) {
        Completable.fromCallable {
            dao.deleteTrackingPosition(userId, manifestID, trackingNumber)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun pareTrackingPositionList(entityList: List<TrackingPositionEntity>?): ArrayList<OfdItemPosition>? {
        if (entityList != null) {
            val modelList: ArrayList<OfdItemPosition> = arrayListOf()
            for (entity in entityList) {
                modelList.add(OfdItemPosition(entity))
            }
            return modelList
        }
        return null
    }
}