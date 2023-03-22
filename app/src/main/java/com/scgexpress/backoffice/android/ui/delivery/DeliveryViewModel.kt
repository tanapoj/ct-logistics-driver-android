package com.scgexpress.backoffice.android.ui.delivery

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.SystemClock
import android.text.InputType
import android.util.TypedValue
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.toDateFormat
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class DeliveryViewModel @Inject constructor(
    application: Application,
    private val repoNetworkDelivery: DeliveryNetworkRepository,
    private val repoLocalNotification: NotificationLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val OFD_MANIFEST_CODE: String = "OFD"
        const val BUTTON_CLICKED_DELAY: Long = 1000
    }

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _ofdManifest: MutableLiveData<ArrayList<Manifest>> = MutableLiveData()
    val ofdManifest: LiveData<ArrayList<Manifest>>
        get() = _ofdManifest

    private val _newBookings = MutableLiveData<ArrayList<BookingInfo>>()
    val newBookings: LiveData<ArrayList<BookingInfo>>
        get() = _newBookings

    private val _ofdClick: MutableLiveData<Manifest> = MutableLiveData()
    val ofdClick: LiveData<Manifest>
        get() = _ofdClick

    private val _ofdScan: MutableLiveData<Manifest> = MutableLiveData()
    val ofdScan: LiveData<Manifest>
        get() = _ofdScan

    private val _ofdSent: MutableLiveData<Manifest> = MutableLiveData()
    val ofdSent: LiveData<Manifest>
        get() = _ofdSent

    private val _ofdCantSent: MutableLiveData<Manifest> = MutableLiveData()
    val ofdCantSent: LiveData<Manifest>
        get() = _ofdCantSent

    private val _bookingAccept: MutableLiveData<BookingInfo> = MutableLiveData()
    val bookingAccept: LiveData<BookingInfo>
        get() = _bookingAccept

    private val _bookingReject: MutableLiveData<BookingInfo> = MutableLiveData()
    val bookingReject: LiveData<BookingInfo>
        get() = _bookingReject

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean>
        get() = _refreshing

    private val current: Calendar by lazy {
        Calendar.getInstance()
    }

    var startDate: String
    var endDate: String
    var mLastClickTime: Long = 0

    init {
        //TODO:Fix this
        //startDate = current.time.toDateFormat()
        startDate = "2019-01-01"
        endDate = current.time.toDateFormat()
    }

    fun loadData(from: String, to: String) {
        startDate = from
        endDate = to
        getOfdManifests()
    }

    private fun getOfdManifests() {
        addDisposable(repoNetworkDelivery.getOfdManifests(startDate, endDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    _ofdManifest.postValue(it.manifestList)
                    _newBookings.postValue(it.newBookingList)
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
                //throw OnErrorNotImplementedException(it)
            })
    }


    private fun getManifests() {
        addDisposable(repoNetworkDelivery.getManifests(startDate, endDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    _ofdManifest.postValue(it)
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
                //throw OnErrorNotImplementedException(it)
            })
    }

    private fun getBookings() {
        addDisposable(repoNetworkDelivery.getBookings(startDate, endDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    _newBookings.postValue(it)
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
                //throw OnErrorNotImplementedException(it)
            })
    }

    fun refresh() {
        _refreshing.value = true
        addDisposable(repoNetworkDelivery.getOfdManifests(startDate, endDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    _ofdManifest.postValue(it.manifestList)
                    _newBookings.postValue(it.newBookingList)
                    _refreshing.value = false
                }
            }) {
                _refreshing.value = false
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
                //throw OnErrorNotImplementedException(it)
            })
    }

    fun acceptBooking(manifestID: String, item: BookingInfo) {
        addDisposable(repoNetworkDelivery.acceptBooking(
            item.bookingID
                ?: "", item.assignID ?: "", manifestID
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.message == "Success") {
                    _bookingAccept.value = BookingInfo()
                    showWarning(context.getString(R.string.sentence_booking_has_been_accepted))
                    loadData(startDate, endDate)
                    deleteBooking(item.bookingID)
                }
            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
            })
    }

    fun rejectBooking(rejectStatus: BookingRejectStatusModel, note: String) {
        addDisposable(repoNetworkDelivery.rejectBooking(
            _bookingReject.value!!.bookingID
                ?: "", _bookingReject.value!!.assignID
                ?: "", rejectStatus.subCatOrderID!!, rejectStatus.catOrderID!!, note
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.message == "Success") {
                    showWarning(context.getString(R.string.sentence_booking_has_been_rejected))
                    loadData(startDate, endDate)
                    deleteBooking(_bookingReject.value!!.bookingID)
                }
            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
            })
    }

    private fun createOfdManifest(vehicle: String) {
        addDisposable(repoNetworkDelivery.createManifest(
            arrayListOf(
                DeliveryOfdCreate(
                    OFD_MANIFEST_CODE,
                    user.branchId, user.branchCode,
                    Utils.getServerDateTimeFormat(Utils.getCurrentTimestamp()), vehicle
                )
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    if (it == "Created success") {
                        getManifests()
                    }
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    private fun deleteBooking(bookingID: String) {
        repoLocalNotification.deleteNotification(bookingID)
    }

    fun ofdClick(item: Manifest) {
        if (checkLastClickTime())
            _ofdClick.value = item
    }

    fun ofdScan(item: Manifest) {
        if (checkLastClickTime())
            _ofdScan.value = item
    }

    fun ofdSent(item: Manifest) {
        if (checkLastClickTime())
            _ofdSent.value = item
    }

    fun ofdCantSent(item: Manifest) {
        if (checkLastClickTime())
            _ofdCantSent.value = item
    }

    fun bookingAccept(item: BookingInfo) {
        if (checkLastClickTime())
            _bookingAccept.value = item
    }

    fun bookingReject(item: BookingInfo) {
        if (checkLastClickTime())
            _bookingReject.value = item
    }

    fun showSnackbar(msg: String) {
        if (checkLastClickTime())
            _snackbar.value = Event(msg)
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    @SuppressLint("RestrictedApi")
    fun showDialogOfdCreated(context: Context) {
        val mBuilder = AlertDialog.Builder(context)

        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        input.layoutParams = lp
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Please insert vehicle"
        input.setSingleLine(true)
        mBuilder.setView(input, getPixelValue(16), 0, getPixelValue(16), 0)

        mBuilder.setTitle("Create OFD Manifest")
        mBuilder.setPositiveButton("OK", null)
        mBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                if (input.text.toString().isNotEmpty()) {
                    createOfdManifest(input.text.toString())
                    mDialog.dismiss()
                }
            }
        }
        mDialog.show()
    }

    private fun getPixelValue(dp: Int): Int {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}