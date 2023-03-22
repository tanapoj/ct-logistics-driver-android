package com.scgexpress.backoffice.android.ui.notification

import android.app.Application
import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.MetaBooking
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preferrence.BookingPreference
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.repository.notification.NotificationNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class NotificationViewModel @Inject constructor(
    application: Application,
    private val repoLocal: NotificationLocalRepository,
    private val repoNetwork: NotificationNetworkRepository,
    private val bookingPreference: BookingPreference,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _notificationItems: MutableLiveData<List<NotificationModel>> = MutableLiveData()
    val notificationItems: LiveData<List<NotificationModel>>
        get() = _notificationItems

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _goBooking = MutableLiveData<Event<String>>()
    val goBooking: LiveData<Event<String>>
        get() = _goBooking

    fun initNotification() {
        addDisposable(repoLocal.getNotificationByUser(user.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _notificationItems.value = it
            }) {
                throw OnErrorNotImplementedException(it)
            })
    }

    fun updateNotificationSeen(model: NotificationModel) {
        model.seen = true
        repoLocal.updateNotification(model)
    }

    //STOPSHIP fix this
    fun acceptBooking(meta: MetaBooking) {
        addDisposable(repoNetwork.acceptBooking(
            meta.bookingID
                ?: "", meta.assignmentID ?: "", ""
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.message == "Success") {
                    showSnackbar(context.getString(R.string.sentence_booking_has_been_accepted))
                }
            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
            })
        saveBooking(meta)
    }

    fun rejectBooking(meta: MetaBooking) {
        addDisposable(repoNetwork.rejectBooking(
            meta.bookingID
                ?: "", meta.assignmentID ?: ""
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.message == "Success") {
                    showSnackbar(context.getString(R.string.sentence_booking_has_been_rejected))
                }

            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
            })
    }

    //STOPSHIP fix this
    private fun saveBooking(metaBooking: MetaBooking) {
        bookingPreference.booking = Utils.convertObjectToString(metaBooking)
    }

    private fun deleteBooking(id: String) {
        repoLocal.deleteNotification(id)
    }

    private fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun goBooking(bookingID: String) {
        _goBooking.value = Event(bookingID)
        //deleteBooking(bookingID)
    }

    fun getDateTime(timestamp: Long): String? {
        return try {
            DateUtils.getRelativeTimeSpanString(
                Date(timestamp).time * 1000, Calendar.getInstance().timeInMillis
                , DateUtils.MINUTE_IN_MILLIS
            ) as String?
        } catch (e: Exception) {
            e.toString()
        }
    }
}