package com.scgexpress.backoffice.android.ui.pickup.detail

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Const.BUTTON_CLICKED_DELAY
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ACTION_RESEND
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_REJECTED
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.common.thenPass
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.pickup.PickupTaskAcception
import com.scgexpress.backoffice.android.model.pickup.ResendReceipt
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.pickup.ContextWrapper
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class PickupDetailsViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: PickupRepository,
    private val dataRepository: DataRepository,
    private val repoData: DataRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _taskID: MutableLiveData<String> = MutableLiveData()
    private val taskID: LiveData<String>
        get() = _taskID

    private var _data: MutableLiveData<PickupTask> = MutableLiveData()
    val data: LiveData<PickupTask>
        get() = _data

    private var _trackings: MutableLiveData<List<Any>> = MutableLiveData()
    val trackings: LiveData<List<Any>>
        get() = _trackings

    private var _receipts: MutableLiveData<List<Any>> = MutableLiveData()
    val receipts: LiveData<List<Any>>
        get() = _receipts

    private val _customer: MutableLiveData<TblOrganization> = MutableLiveData()
    val customer: LiveData<TblOrganization>
        get() = _customer

    private val _viewPhoto: MutableLiveData<SubmitTracking> = MutableLiveData()
    val viewPhoto: LiveData<SubmitTracking>
        get() = _viewPhoto

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _finish = MutableLiveData<Event<Boolean>>()
    val finish: LiveData<Event<Boolean>>
        get() = _finish

    private var _serviceType: MutableLiveData<Map<Int, String>> = MutableLiveData()
    val serviceType: LiveData<Map<Int, String>>
        get() = _serviceType

    private var _sizing: MutableLiveData<Map<Int, String>> = MutableLiveData()
    val sizing: LiveData<Map<Int, String>>
        get() = _sizing

    var mLastClickTime: Long = 0

    fun getData(taskId: String) {
        loadServiceType()
        loadParcelSize()

        repo.getTaskById(taskId)
            .flatMap {
                when {
                    it.isOfflineData -> repo.attachOfflineDataToTask(it)
                    else -> Single.just(it)
                }
            }
            .flatMap {
                loadDefaultCustomerPreFieldInfo(it.customerCode) thenPass it
            }
            .scheduleBy(rxThreadScheduler)
            .subscribe({
                _data.value = it

                val d: ArrayList<Any> =
                    arrayListOf(Title(context.getString(R.string.dont_pick_yet)))
                d.addAll(it.trackingInfo!!.trackings)
                _trackings.value = d

                _receipts.value = it.trackingInfo!!.receipts
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    private fun loadDefaultCustomerPreFieldInfo(customerCode: String): Completable {
        return Completable.create { emitter ->
            dataRepository.getOrganization(customerCode)
                .scheduleBy(rxThreadScheduler)
                .scheduleBy(rxThreadScheduler)
                .subscribe({
                    _customer.value = it
                    emitter.onComplete()
                }, emitter::onError)
                .also { addDisposable(it) }
        }.scheduleBy(rxThreadScheduler)
    }

    //TODO Pickup task : Fix this mockup
    /*fun getDataCompleted(taskId: String) {
        repo.getTaskCompleted(taskId)
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                _data.value = it

                val d: ArrayList<Any> = arrayListOf(Title(context.getString(R.string.dont_pick_yet)))
                d.addAll(it.trackingInfo!!.trackings)
                _trackings.value = d

                _receipts.value = it.trackingInfo!!.receipts

                getOrganization(it.customerCode.toInt())
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }*/

    fun rejectBooking(rejectStatus: BookingRejectStatusModel, note: String) {
        val body = PickupTaskAcception(
            PARAMS_PICKUP_TASK_REJECTED,
            rejectStatus.subCatOrderID!!.toInt(),
            note
        )

        repo.acceptTask(
            _data.value!!.id, body
        )
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                if (it.status == PARAMS_PICKUP_TASK_REJECTED) {
                    showWarning(context.getString(R.string.sentence_booking_has_been_rejected))
                    finish(true)
                }
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    fun resendReceipt(receiptId: String, tel: String, email: String) {
        val resendReceipt = ResendReceipt(PARAMS_PICKUP_TASK_ACTION_RESEND, receiptId, tel, email)
        repo.resend(resendReceipt)
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                if (it.isSuccess())
                    showWarning(context.getString(R.string.resend_receipt))
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    private fun showAlertMessage(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun finish(isFinish: Boolean) {
        _finish.value = Event(isFinish)
    }

    fun viewPhoto(submitTracking: SubmitTracking) {
        _viewPhoto.value = submitTracking
    }

    fun setTaskID(taskID: String) {
        if (taskID.isNotEmpty()) {
            _taskID.value = taskID
            getData(taskID)
        }
    }

    fun getCustomerTel(): String {
        return if (customer.value == null) ""
        else customer.value!!.phone!!
    }

    fun getCustomerEmail(): String {
        return if (customer.value == null) ""
        else customer.value!!.email!!
    }

   fun setServiceAndSizeName(data: List<SubmitTracking>): List<SubmitTracking> {
        try {
            val services = serviceType.value
            val sizings = sizing.value
            for (item in data) {
                item.serviceName = services!![item.serviceId] ?: error("")
                item.sizeName = sizings!![item.sizeId] ?: error("")
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return data
    }

    private fun loadServiceType() {
        repo.getAllServiceType(ContextWrapper(context))
            .scheduleBy(rxThreadScheduler)
            .subscribe({ list ->
                _serviceType.value = list.map { it.first to it.second }.toMap()
            }, {
            }).also {
                addDisposable(it)
            }
    }

    private fun loadParcelSize() {
        repo.getAllParcelSize(ContextWrapper(context))
            .scheduleBy(rxThreadScheduler)
            .subscribe({ list ->
                _sizing.value = list.map { it.first to it.second }.toMap()
            }, {
            }).also {
                addDisposable(it)
            }
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}