package com.scgexpress.backoffice.android.ui.delivery.ofd.scan

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.LocationHelper
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.isTrackingId
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryLocalRepository
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class OfdScanViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository,
    private val repoLocal: DeliveryLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val OFD_SCAN_STATUS_CODE: String = "7"
        const val OFD_STATUS_ID_SUCCESS: String = "success"
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

    private var _dataNetwork: MutableLiveData<List<DeliveryOfdParcelResponse>> = MutableLiveData()
    val dataNetwork: LiveData<List<DeliveryOfdParcelResponse>>
        get() = _dataNetwork

    private var _header: MutableLiveData<Manifest> = MutableLiveData()
    val header: LiveData<Manifest>
        get() = _header

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private var groupID: String = ""
    private var manifestID: String = ""
    private var trackingIds: List<String> = listOf()
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    fun prepareData() {
        getDataNetwork()
    }

    fun confirmScanOfd(trackingID: String) {
        if (trackingID.isEmpty()) {
            showWarning(context.getString(R.string.sentence_please_scan_your_tracking))
            return
        }
        if (!trackingID.isTrackingId()) {
            showWarning("'$trackingID' ${context.getString(R.string.sentence_is_not_tracking_ID_format)}")
            _trackingId.value = ""
            return
        }

        addDisposable(repoNetwork.scanOfd(
            manifestID, DeliveryOfdParcelList(
                arrayListOf(
                    DeliveryOfdParcel(
                        trackingId = trackingID, statusCode = OFD_SCAN_STATUS_CODE,
                        datetimeInput = Utils.getServerDateTimeFormat(Utils.getCurrentTimestamp()),
                        latitude = latitude, longitude = longitude
                    )
                )
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it[0].status == OFD_STATUS_ID_SUCCESS) {
                    updateData(it[0], false)
                    showSnackbar(context.getString(R.string.sentence_put_ofd_parcels_successful))
                } else {
                    showWarning(context.getString(R.string.sentence_put_ofd_parcels_failed))
                }
                _trackingId.value = ""
                loadHeader()
            }) {
                if (it is NoConnectivityException) {
                    updateDataNoInternet(trackingID, false)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    private fun getDataNetwork() {
        addDisposable(repoNetwork.getManifestItemScanned(manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    _dataNetwork.value = it
                    val d: ArrayList<Any> = arrayListOf(DeliveryOfdScanTitle("0"))
                    d.addAll(_dataNetwork.value!!)
                    setTrackingIds(d)
                    _data.value = d
                }
                genGroupID()
            }) {
                genGroupID()
            })
    }

    private fun updateDataNetwork(dataNetwork: List<DeliveryOfdParcelResponse>): List<Delivery> {
        val data = arrayListOf<Delivery>()
        for (item in dataNetwork) {
            data.add(Delivery(item))
        }
        return data
    }

    private fun loadHeader() {
        if (manifestID != "") {
            repoNetwork.getManifestHeader(manifestID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it == null) return@subscribe
                    _header.value = it
                }) {
                    //throw OnErrorNotImplementedException(it)
                }.also {
                    addDisposable(it)
                }
        }
    }

    private fun getDataLocal() {
        addDisposable(repoLocal.getDelivery(user.id, manifestID, groupID, OFD_SCAN_STATUS_CODE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    val d: ArrayList<Any> = arrayListOf(DeliveryOfdScanTitle("0"))
                    d.addAll(it)
                    if (_dataNetwork.value != null)
                        d.addAll(_dataNetwork.value!!)
                    setTrackingIds(d)
                    _data.value = d
                }
            }) {
            })
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
                    getDataLocal()
                }) {
                    groupID = "1"
                    getDataLocal()
                })
        }
        return groupID
    }


    private fun updateData(data: DeliveryOfdParcelResponse, deleted: Boolean) {
        val codCollected = 0.0
        repoLocal.updateDelivery(
            Delivery(
                groupID, user.id, manifestID, data.trackingId,
                data.parcelsInfo.senderCode, data.parcelsInfo.senderName, OFD_SCAN_STATUS_CODE,
                data.parcelsInfo.codAmount, codCollected, data.parcelsInfo.userOrderDate, deleted,
                true, Utils.getCurrentTimestamp()
            )
        )
    }

    private fun updateDataNoInternet(trackingID: String, deleted: Boolean) {
        repoLocal.updateDelivery(
            Delivery(
                groupID = groupID, userId = user.id, manifestID = manifestID,
                trackingNumber = trackingID, statusID = OFD_SCAN_STATUS_CODE, deleted = deleted,
                sync = false, timestamp = Utils.getCurrentTimestamp()
            )
        )
    }

    private fun deleteScanOfd(manifestID: String, item: Any) {
        var trackingNumber = ""
        var statusID = ""
        if (item is Delivery) {
            trackingNumber = item.trackingNumber!!
            statusID = OFD_SCAN_STATUS_CODE
        } else if (item is DeliveryOfdParcelResponse) {
            trackingNumber = item.trackingId
            statusID = item.status
        }

        addDisposable(repoNetwork.deleteOfdScan(
            manifestID, DeliveryOfdParcelList(
                arrayListOf(
                    DeliveryOfdParcel(
                        trackingId = trackingNumber, statusCode = statusID,
                        datetimeInput = Utils.getServerDateTimeFormat(Utils.getCurrentTimestamp())
                    )
                )
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it[0].status == OFD_STATUS_ID_SUCCESS) {
                    updateData(it[0], true)
                    showSnackbar(context.getString(R.string.sentence_scan_delete_ofd_parcels_successful))
                    if (item is DeliveryOfdParcelResponse) {
                        prepareData()
                    }
                } else {
                    showWarning(context.getString(R.string.sentence_scan_delete_ofd_parcels_failed))
                }
                loadHeader()
            }) {
                if (it is NoConnectivityException) {
                    if (item is Delivery)
                        updateDataNoInternet(item.trackingNumber!!, true)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    private fun setTrackingIds(data: List<Any>) {
        val result = arrayListOf<String>()
        for (item in data) {
            if (item is Delivery) {
                result.add(item.trackingNumber!!)
            } else if (item is DeliveryOfdParcelResponse) {
                result.add(item.trackingId)
            }
        }
        this.trackingIds = result
    }

    fun checkExistTracking(trackingCode: String): Boolean {
        return trackingIds.any { l -> l == trackingCode }
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    fun setManifestID(manifestID: String) {
        this.manifestID = manifestID
        loadHeader()
    }

    fun showDialogConfirmDeleteTracking(context: Context, manifestID: String, item: Any) {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(context.getString(R.string.delete_tracking))
        mBuilder.setMessage(context.getString(R.string.sentence_are_you_sure_you_want_to_delete_this_tracking))
        mBuilder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
            deleteScanOfd(manifestID, item)
        }
        mBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun getLocationHelper(mContext: Context): LocationHelper {
        return LocationHelper.getInstance(mContext)
    }
}