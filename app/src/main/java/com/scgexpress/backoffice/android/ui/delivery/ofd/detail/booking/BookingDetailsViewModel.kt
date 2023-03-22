package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class BookingDetailsViewModel @Inject constructor(
        application: Application,
        private val repoNetwork: DeliveryNetworkRepository,
        private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val IMAGE_DIRECTORY = "/scgexpress"
        const val BUTTON_CLICKED_DELAY: Long = 1000
    }

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<BookingInfo> = MutableLiveData()
    val data: LiveData<BookingInfo>
        get() = _data

    private val _customer: MutableLiveData<TblOrganization> = MutableLiveData()
    val customer: LiveData<TblOrganization>
        get() = _customer

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _finish = MutableLiveData<Event<Boolean>>()
    val finish: LiveData<Event<Boolean>>
        get() = _finish

    var mLastClickTime: Long = 0

    fun rejectBooking(rejectStatus: BookingRejectStatusModel, note: String) {
        addDisposable(repoNetwork.rejectBooking(_data.value!!.bookingID
                ?: "", _data.value!!.assignID
                ?: "", rejectStatus.subCatOrderID!!, rejectStatus.catOrderID!!, note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.message == "Success") {
                        Timber.d("BookingInfo has been rejected!")
                        finish(true)
                    }
                }) {
                    if (it is NoConnectivityException)
                        showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                })
    }

    fun getCustomer(customerCode: String) {
//        addDisposable(repoLocal.getOrganization(customerCode)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    if (it != null) {
//                        _customer.value = it
//                    } else {
//                        showWarning(context.getString(R.string.sentence_pickup_scan_please_insert_customer_code_correctly))
//                    }
//                }) {
//                    if (it is NoConnectivityException)
//                        showSnackbar(context.getString(R.string.there_is_on_internet_connection))
//                })
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun finish(isFinish: Boolean) {
        _finish.value = Event(isFinish)
    }

    fun setBookingInfo(booking: BookingInfo) {
        _data.value = booking
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}