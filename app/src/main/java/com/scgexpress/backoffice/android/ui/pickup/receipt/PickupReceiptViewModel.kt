package com.scgexpress.backoffice.android.ui.pickup.receipt

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

class PickupReceiptViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: PickupRepository,
    private val dataRepository: DataRepository,
    private val loginPreference: LoginPreference,
    private val pickupPreference: PickupPreference
) : RxAndroidViewModel(application) {

    val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<List<PickupScanningTrackingEntity>> = MutableLiveData()
    val data: LiveData<List<PickupScanningTrackingEntity>>
        get() = _data

    private var _task: MutableLiveData<PickupTask> = MutableLiveData()
    val task: LiveData<PickupTask>
        get() = _task

    private val _alertMessage = MutableLiveData<Event<String>>()
    val alertMessage: LiveData<Event<String>>
        get() = _alertMessage

    private val _nextBooking = MutableLiveData<Event<PickupTask>>()
    val nextBooking: LiveData<Event<PickupTask>>
        get() = _nextBooking

    private var _finish: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val finish: LiveData<Event<Boolean>>
        get() = _finish

    private var _finishWithMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val finishWithMessage: LiveData<Event<String>>
        get() = _finishWithMessage

    private val _isEnableButtonSend = MutableLiveData<Event<Boolean>>()
    val isEnableButtonSend: LiveData<Event<Boolean>>
        get() = _isEnableButtonSend

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String>
        get() = _phone

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    var taskIdList: List<String>?
        get() = pickupPreference.currentScanningTaskIdList
        set(value) {
            pickupPreference.currentScanningTaskIdList = value
        }

    var payment: String = ""
    var taskId: String = ""
    var customerCode = "0"
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

    fun requestItem() {
        getTask(taskId, false)
        getData()
    }

    fun getData() {
        getCurrentLocation(context).subscribe({ (lat, lng) ->
            latitude = lat.let { if(it == .0) null else it }
            longitude = lng.let { if(it == .0) null else it }
        }, {
            Timber.e(it)
        }).also { addDisposable(it) }

        repo.getScanningTracking()
            .scheduleBy(rxThreadScheduler)
            .subscribe({
                Timber.d("getData $it")
                if (it == null) return@subscribe
                _data.value = it
            }) {
            }.also {
                addDisposable(it)
            }
    }

    fun sendReceipt(tel: String, email: String) {
        if (_task.value != null) {
            customerCode = _task.value!!.customerCode
        }
        Timber.d("sendReceipt CALL repo.submitScanningTracking()")
        Timber.d("taskId = $taskId")
        Timber.d("customerCode = $customerCode")
        Timber.d("user.personalId = ${user.personalId}")
        Timber.d("payment.toInt() = ${payment.toInt()}")
        Timber.d("tel = $tel email=$email")
        Timber.d("data.value = ${data.value}")
        repo.submitScanningTracking(
            taskId,
            user.id,
            customerCode,
            payment.toInt(),
            tel,
            email,
            latitude to longitude,
            data.value!!
        )
            .scheduleBy(rxThreadScheduler)
            .subscribe({list ->
                Timber.d("sendReceipt --> submitScanningTracking 1 --> $list")
                if(list.all { it.isSuccess() }){
                    getNextTask("Success, All Task Submitted")
                }
                else{
                    val fails = list.filter { !it.isSuccess() }
                    getNextTask("has ${fails.size} fail! (from ${list.size}) with: ${fails}")
                    Timber.e(IllegalStateException("$list"))
                }
            }) {
                Timber.d("sendReceipt --> submitScanningTracking 2 --> $it")
                Timber.e(it)
                //if (it is NoConnectivityException) {
                //showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
                getNextTask("Task Saved")
                //}
            }.also {
                addDisposable(it)
            }
    }

    private fun getNextTask(msg: String? = null) {

        Timber.d("getNextTask $taskIdList")

//        repo.getAllTask().toSingle().subscribe({ sc ->
//            Timber.d("TEST: All Task ---------")
//            for (x in sc) {
//                Timber.d("$x")
//            }
//        }, {}).also { addDisposable(it) }

        if (taskIdList != null) {
            pickupPreference.leftShiftScanningTaskIdList()?.let {
                Timber.d("getNextTask leftShiftScanningTaskIdList $it")
                getTask(it, true)
                return@getNextTask
            }
        }

        if (msg == null) {
            _finish.value = Event(true)
        } else {
            _finishWithMessage.value = Event(msg)
        }

//        val ids = taskIdsRemembered?.toMutableList()
//        if (ids == null) {
//            finish(true)
//            return
//        }
//        if (ids.isEmpty()) {
//            finish(true)
//            return
//        }
//        ids.removeAt(0)
//        taskIdsRemembered = ids
//        getTask(ids[0], true)
    }

    private fun getTask(s: String, isNext: Boolean) {
        repo.getTaskById(s)
            .scheduleBy(rxThreadScheduler)
            .subscribe({
                if (it == null) return@subscribe
                _task.value = it
                loadDefaultCustomerPreFieldInfo(it.customerCode)
                if (isNext)
                    showNextBookingDialog(it)
            }) {
                Timber.e(it)
            }.also {
                addDisposable(it)
            }
    }

    private fun loadDefaultCustomerPreFieldInfo(customerCode: String) {
        Timber.d("load pre-field data email + phone")
        dataRepository.getOrganization(customerCode)
            .scheduleBy(rxThreadScheduler)
            .subscribe({
                Timber.d("load pre-field data email + phone: $it")
                if (!it.phone.isNullOrBlank()) {
                    _phone.value = it.phone
                }
                if (!it.email.isNullOrBlank()) {
                    _email.value = it.email
                }
            }, {
                Timber.e(it)
            })
            .also {
                addDisposable(it)
            }
    }

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }

    private fun showNextBookingDialog(task: PickupTask) {
        _nextBooking.value = Event(task)
    }

    fun finish(finish: Boolean) {
        _finish.value = Event(finish)
    }

    private fun isEnableButtonSend(isEnable: Boolean) {
        _isEnableButtonSend.value = Event(isEnable)
    }

    fun getLocationHelper(mContext: Context): LocationHelper {
        return LocationHelper.getInstance(mContext)
    }
}