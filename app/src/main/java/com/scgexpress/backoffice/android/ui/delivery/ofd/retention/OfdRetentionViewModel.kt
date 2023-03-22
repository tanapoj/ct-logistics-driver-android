package com.scgexpress.backoffice.android.ui.delivery.ofd.retention

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.Delivery
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelList
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelResponse
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject


class OfdRetentionViewModel @Inject constructor(
    application: Application,
    private val repo: DeliveryRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val OFD_SENT_STATUS_CODE: String = "34"
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

    private val _items = MutableLiveData<List<DeliveryTask>>()
    val items: LiveData<List<DeliveryTask>>
        get() = _items

    private var _dataNetwork: MutableLiveData<List<DeliveryOfdParcelResponse>> = MutableLiveData()
    val dataNetwork: LiveData<List<DeliveryOfdParcelResponse>>
        get() = _dataNetwork

    private var _scanData: MutableLiveData<List<Delivery>> = MutableLiveData()
    val scanData: LiveData<List<Delivery>>
        get() = _scanData

    private var _scanDataList: MutableLiveData<DeliveryOfdParcelList> = MutableLiveData()
    val scanDataList: LiveData<DeliveryOfdParcelList>
        get() = _scanDataList

    private var _manifestID: MutableLiveData<String> = MutableLiveData()
    val manifestID: LiveData<String>
        get() = _manifestID

    private var _codAmount: MutableLiveData<Double> = MutableLiveData()
    val codAmount: LiveData<Double>
        get() = _codAmount

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _codDialog = MutableLiveData<Event<Double>>()
    val codDialog: LiveData<Event<Double>>
        get() = _codDialog

    private var newItem: Delivery = Delivery()

    private val scannedTaskList = mutableListOf<DeliveryTask>()

    private val _countStatus = MutableLiveData<Int>()
    val countStatus: LiveData<Int>
        get() = _countStatus

    private val _trackingCode = MutableLiveData<Event<String>>()
    val trackingCode: LiveData<Event<String>>
        get() = _trackingCode

    private val _confirmRemoveTracking = MutableLiveData<Event<String>>()
    val confirmRemoveTracking: LiveData<Event<String>>
        get() = _confirmRemoveTracking

    private var groupID: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var mLastClickTime: Long = 0

    /*private fun prepareData() {
        getDataNetwork()
    }*/

    private fun addScannedTask(task: DeliveryTask) {
        scannedTaskList.add(task)
        _items.value = scannedTaskList.toList()
        _countStatus.value = scannedTaskList.size
    }

    fun setTrackingCode(trackingCode: String) {
        if (trackingCode in scannedTaskList.map { it.trackingCode }) {
            showWarning("this tracking already scanned")
            return
        }
        //TODO:Fix it - get real task
        repo.getTaskByTrackingCode(trackingCode).subscribe({
            addScannedTask(it)
            _trackingCode.value = Event(trackingCode)
        }, {
            showWarning("tracking $trackingCode not found")
        }).also {
            addDisposable(it)
        }
    }

    fun removeTrackingCode(trackingCode: String, forceDelete: Boolean = false) {
        if (!forceDelete) {
            _confirmRemoveTracking.value = Event(trackingCode)
            return
        }

        scannedTaskList.removeAll { it.trackingCode == trackingCode }
        _items.value = scannedTaskList.toList()
        _countStatus.value = scannedTaskList.size
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    private fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}