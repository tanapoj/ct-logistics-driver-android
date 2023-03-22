package com.scgexpress.backoffice.android.repository.notification

import android.annotation.SuppressLint
import com.google.common.collect.Lists
import com.scgexpress.backoffice.android.db.dao.NotificationDao
import com.scgexpress.backoffice.android.db.entity.NotificationEntity
import com.scgexpress.backoffice.android.ui.notification.NotificationModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationLocalRepository @Inject
constructor(private val notificationDao: NotificationDao) {

    val notifications: Flowable<List<NotificationModel>>
        get() {
            return notificationDao.notifications
                .subscribeOn(Schedulers.io())
                .map(this::prepareNotificationList)
        }

    fun getNotificationByUser(userID: String): Flowable<List<NotificationModel>> {
        return notificationDao.getNotificationByUser(userID)
            .subscribeOn(Schedulers.io())
            .map(this::prepareNotificationList)
    }

    @SuppressLint("CheckResult")
    fun updateNotification(modelList: List<NotificationModel>) {
        Completable.fromCallable {
            notificationDao.deleteNotification()
            val notificationModelList = Lists.transform(
                modelList
            ) { input ->
                NotificationEntity(
                    userID = input!!.userID,
                    metaID = input.metaID,
                    type = input.type,
                    category = input.category,
                    message = input.message,
                    timestamp = input.timestamp,
                    action = input.action,
                    meta = input.meta,
                    seen = input.seen
                )
            }
            notificationDao.insertOrReplaceNotification(notificationModelList)
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun insertNotification(model: NotificationModel) {
        Completable.fromCallable {
            notificationDao.insertOrReplaceNotification(
                NotificationEntity(
                    userID = model.userID,
                    metaID = model.metaID,
                    type = model.type,
                    category = model.category,
                    message = model.message,
                    timestamp = model.timestamp,
                    action = model.action,
                    meta = model.meta
                )
            )
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updateNotification(model: NotificationModel) {
        Completable.fromCallable {
            notificationDao.insertOrReplaceNotification(
                NotificationEntity(
                    model.id,
                    model.userID,
                    model.metaID,
                    model.type,
                    model.category,
                    model.message,
                    model.timestamp,
                    model.action,
                    model.meta,
                    model.seen
                )
            )
            Completable.complete()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteNotification(id: String) {
        Completable.fromCallable {
            notificationDao.deleteNotification(id)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun prepareNotificationList(categoryEntityList: List<NotificationEntity>?): List<NotificationModel>? {
        if (categoryEntityList != null) {
            val categoriesModelList: ArrayList<NotificationModel> = arrayListOf()
            for (categoryEntity in categoryEntityList) {
                categoriesModelList.add(NotificationModel(categoryEntity))
            }
            return categoriesModelList
        }
        return null
    }
}