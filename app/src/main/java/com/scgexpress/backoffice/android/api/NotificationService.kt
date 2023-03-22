package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.ui.notification.NotificationModel
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationService {
    @POST("acceptBooking")
    fun acceptBooking(@Body body: HashMap<String, Any>): Flowable<NotificationModel>

    @POST("cancelBooking")
    fun rejectBooking(@Body body: HashMap<String, Any>): Flowable<NotificationModel>
}