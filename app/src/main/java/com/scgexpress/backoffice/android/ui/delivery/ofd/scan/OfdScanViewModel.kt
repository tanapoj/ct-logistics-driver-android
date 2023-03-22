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
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryLocalRepository
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class OfdScanViewModel @Inject constructor(
        application: Application,
        private val deliveryRepo: DeliveryRepository,
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

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _confirmRemoveTracking = MutableLiveData<Event<String>>()
    val confirmRemoveTracking: LiveData<Event<String>>
        get() = _confirmRemoveTracking

    private val _countStatus = MutableLiveData<Int>()
    val countStatus: LiveData<Int>
        get() = _countStatus

    private val _items = MutableLiveData<List<DeliveryTask>>()
    val items: LiveData<List<DeliveryTask>>
        get() = _items

    private val _trackingCode = MutableLiveData<Event<String>>()
    val trackingCode: LiveData<Event<String>>
        get() = _trackingCode

    private val scannedTaskList = mutableListOf<DeliveryTask>()

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

        deliveryRepo.getTaskByTrackingCode(trackingCode).subscribe({
            addScannedTask(it)
            _trackingCode.value = Event("")
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

    fun getLocationHelper(mContext: Context): LocationHelper {
        return LocationHelper.getInstance(mContext)
    }
}