package com.scgexpress.backoffice.android.repository.notification

import com.scgexpress.backoffice.android.api.NotificationService
import com.scgexpress.backoffice.android.ui.notification.NotificationModel
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationNetworkRepository @Inject constructor(private val service: NotificationService) {

    fun acceptBooking(bookingId: String, assignmentId: String, manifestID: String): Flowable<NotificationModel> {
        val map: HashMap<String, Any> = hashMapOf()
        map["bookingID"] = bookingId ?: ""
        map["assignmentID"] = assignmentId ?: ""
        map["manifestID"] = manifestID

        return service.acceptBooking(map)
    }

    fun rejectBooking(bookingId: String, assignmentId: String): Flowable<NotificationModel> {
        val map: HashMap<String, Any> = hashMapOf()
        map["bookingID"] = bookingId ?: ""
        map["assignmentID"] = assignmentId ?: ""

        return service.rejectBooking(map)
    }
}

