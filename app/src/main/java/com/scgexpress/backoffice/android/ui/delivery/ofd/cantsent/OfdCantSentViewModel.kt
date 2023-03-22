package com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.LocationHelper
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.isTrackingId
import com.scgexpress.backoffice.android.constant.RetentionStatus
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryLocalRepository
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class OfdCantSentViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository,
    private val repoLocal: DeliveryLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val OFD_RETENTION_STATUS_ID: String = ""
        const val OFD_STATUS_ID_SUCCESS: String = "success"
        const val BUTTON_CLICKED_DELAY: Long = 1000
    }

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _trackingId: MutableLiveData<String> = MutableLiveData()
    val trackingId: LiveData<String>
        get() = _trackingId

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _scanData: MutableLiveData<List<Delivery>> = MutableLiveData()
    val scanData: LiveData<List<Delivery>>
        get() = _scanData

    private var _retentionReason: MutableLiveData<MasterParcelModel> = MutableLiveData()
    val retentionReason: LiveData<MasterParcelModel>
        get() = _retentionReason

    private var _manifestID: MutableLiveData<String> = MutableLiveData()
    val manifestID: LiveData<String>
        get() = _manifestID

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _finish = MutableLiveData<Event<Boolean>>()
    val finish: LiveData<Event<Boolean>>
        get() = _finish

    private var groupID: String = ""
    var isOther = false
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var mLastClickTime: Long = 0

    fun scanOfdRetention(trackingID: String, note: String) {

        if (trackingID.isEmpty()) {
            showWarning(context.getString(R.string.sentence_please_scan_your_tracking))
            return
        }
        if (!trackingID.isTrackingId()) {
            showWarning("'$trackingID' ${context.getString(R.string.sentence_is_not_tracking_ID_format)}")
            _trackingId.value = ""
            return
        }

        addDisposable(repoNetwork.getTracking(trackingID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    updateData(it, false, note)
                } else {
                    showWarning(context.getString(R.string.sentence_scan_ofd_parcels_failed))
                }
                _trackingId.value = ""
            }) {
                if (it is NoConnectivityException) {
                    updateDataNoInternet(trackingID, false, note)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    private fun deleteOfdRetention(item: Delivery) {
        item.deleted = true
        repoLocal.updateDelivery(item)
    }

    fun confirmOfdRetention() {
        if (!checkLastClickTime()) return
        val deliveryParcel: ArrayList<DeliveryOfdParcel> = arrayListOf()
        for (data: Delivery in _scanData.value!!) {
            deliveryParcel.add(
                DeliveryOfdParcel(
                    trackingId = data.trackingNumber!!, statusCode = data.statusID!!,
                    datetimeInput = Utils.getServerDateTimeFormat(data.timestamp!!), ofdNote = data.note!!,
                    latitude = latitude, longitude = longitude
                )
            )
        }
        addDisposable(repoNetwork.scanOfd(manifestID.value!!, DeliveryOfdParcelList(deliveryParcel))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it[0].status == OFD_STATUS_ID_SUCCESS) {
                    showSnackbar(context.getString(R.string.sentence_put_ofd_parcels_successful))
                    finish(true)
                } else {
                    showWarning(context.getString(R.string.sentence_put_ofd_parcels_failed))
                    finish(false)
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                } else if (it is IndexOutOfBoundsException) {
                    showWarning(context.getString(R.string.sentence_put_ofd_parcels_failed))
                    finish(false)
                }
            })
    }

    private fun updateData(data: TrackingInfo, deleted: Boolean, note: String) {
        val codCollected = 0.0
        var codAmount = 0.0
        try {
            codAmount = data.codAmount.toDouble()
        } catch (ex: Exception) {
        }
        repoLocal.updateDelivery(
            Delivery(
                groupID, user.id, _manifestID.value!!,
                data.trackingNumber,
                data.senderCode, data.senderName, retentionReason.value!!.id,
                codAmount, codCollected, data.dateCreated, deleted,
                true, Utils.getCurrentTimestamp(), note
            )
        )


    }

    private fun updateDataNoInternet(trackingID: String, deleted: Boolean, note: String) {
        repoLocal.updateDelivery(
            Delivery(
                groupID = groupID, userId = user.id, manifestID = _manifestID.value!!,
                trackingNumber = trackingID, statusID = retentionReason.value!!.id, deleted = deleted,
                sync = false, timestamp = Utils.getCurrentTimestamp(), note = note
            )
        )
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    fun finish(finish: Boolean) {
        _finish.value = Event(finish)
    }

    fun setManifestID(manifestID: String) {
        _manifestID.value = manifestID
        _retentionReason.value = MasterParcelModel()
        genGroupID()
    }

    private fun getData() {
        repoLocal.getDelivery(user.id, _manifestID.value!!, groupID, OFD_RETENTION_STATUS_ID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null) return@subscribe
                _scanData.value = it
                val d: ArrayList<Any> = arrayListOf(DeliveryOfdScanTitle("0"))
                d.addAll(it)
                _data.value = d
            }) {
            }.also { addDisposable(it) }
    }

    private fun genGroupID(): String {
        if (groupID == "") {
            addDisposable(repoLocal.lastGroupID
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    groupID = if (it != null || it != "") {
                        (it.toInt() + 1).toString()
                    } else "1"
                    getData()
                }) {
                    groupID = "1"
                    getData()
                })
        }
        return groupID
    }

    fun checkExistTracking(trackingCode: String): Boolean {
        return scanData.value!!.any { l -> l.trackingNumber == trackingCode }
    }

    fun showDialogConfirmDeleteTracking(context: Context, item: Delivery) {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(context.getString(R.string.delete_tracking))
        mBuilder.setMessage(context.getString(R.string.sentence_are_you_sure_you_want_to_delete_this_tracking))
        mBuilder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
            deleteOfdRetention(item)
        }
        mBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun showDialogRetention(context: Context) {
        isOther = false
        var position = -1
        val retentionReason = arrayOfNulls<String>(RetentionStatus.list.size)
        for (i in 0 until RetentionStatus.list.size) {
            retentionReason[i] = RetentionStatus.list[i].name
        }

        val mBuilder = AlertDialog.Builder(context)

        mBuilder.setTitle(context.getString(R.string.retention))
        mBuilder.setSingleChoiceItems(retentionReason, -1) { _, i ->
            position = i
            isOther = i == retentionReason.size - 1
        }
        mBuilder.setPositiveButton(context.getString(R.string.ok), null)

        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                if (position > -1) {
                    _retentionReason.value = RetentionStatus.list[position]
                    mDialog.dismiss()
                }
            }
        }
        mDialog.show()
    }

    fun getRetentionName(id: String): String {
        return RetentionStatus.list.filter { l -> l.id == id }[0].name!!
    }

    fun getLocationHelper(mContext: Context): LocationHelper {
        return LocationHelper.getInstance(mContext)
    }

    private fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}