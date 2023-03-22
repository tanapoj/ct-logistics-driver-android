package com.scgexpress.backoffice.android.ui.pickup.task

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Const.BUTTON_CLICKED_DELAY
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ACCEPTED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_NEW_BOOKING
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_REJECTED
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.pickup.PickupTaskAcception
import com.scgexpress.backoffice.android.model.pickup.PickupTaskList
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class PickupTaskViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: PickupRepository,
    private val notRepo: NotificationLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _pickupTask = MutableLiveData<List<PickupTask>>()
    private val pickupTask: LiveData<List<PickupTask>>
        get() = _pickupTask

    private val _newPickupTask = MutableLiveData<List<PickupTask>>()
    val newPickupTask: LiveData<List<PickupTask>>
        get() = _newPickupTask

    private val _inProgressPickupTask = MutableLiveData<List<PickupTask>>()
    val inProgressPickupTask: LiveData<List<PickupTask>>
        get() = _inProgressPickupTask

    private val _completedPickupTask = MutableLiveData<List<PickupTask>>()
    val completedPickupTask: LiveData<List<PickupTask>>
        get() = _completedPickupTask

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _dataSearch: MutableLiveData<PickupTaskList> = MutableLiveData()
    private val dataSearch: LiveData<PickupTaskList>
        get() = _dataSearch

    private var _dataSearchResult: MutableLiveData<List<PickupTask>> = MutableLiveData()
    val dataSearchResult: LiveData<List<PickupTask>>
        get() = _dataSearchResult

    private val _itemClick: MutableLiveData<PickupTask> = MutableLiveData()
    val itemClick: LiveData<PickupTask>
        get() = _itemClick

    private val _actionPickup: MutableLiveData<PickupTask> = MutableLiveData()
    val actionPickup: LiveData<PickupTask>
        get() = _actionPickup

    private val _phoneCall: MutableLiveData<String> = MutableLiveData()
    val phoneCall: LiveData<String>
        get() = _phoneCall

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location>
        get() = _location

    private val _bookingAccept: MutableLiveData<Event<PickupTask>> = MutableLiveData()
    val bookingAccept: LiveData<Event<PickupTask>>
        get() = _bookingAccept

    private val _bookingReject: MutableLiveData<Event<PickupTask>> = MutableLiveData()
    val bookingReject: LiveData<Event<PickupTask>>
        get() = _bookingReject

    private val _alertMessage = MutableLiveData<Event<String>>()
    val alertMessage: LiveData<Event<String>>
        get() = _alertMessage

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean>
        get() = _refreshing

    private val _notificationIndicator: MutableLiveData<Boolean> = MutableLiveData()
    val notificationIndicator: LiveData<Boolean>
        get() = _notificationIndicator


    private val _tabTaskCounter: MutableLiveData<List<Int>> = MutableLiveData()
    val tabTaskCounter: LiveData<List<Int>>
        get() = _tabTaskCounter

    var mLastClickTime: Long = 0
    var bookingId = ""
    var taskId = ""

    fun requestItem() {
        getPickupTasks()
        //_tabTaskCounter.value = listOf(0, 0, 0)
    }

    private fun getPickupTasks() {
        repo.getAllTask()
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                //                if (it.isEmpty()) {
//                    showAlertMessage("There is no data loaded from API or offline-data")
//                }
                Timber.e("here is no data loaded from API or offline-data")
                _pickupTask.value = it
                mapData(it)
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
                else
                    showAlertMessage("cannot load data, with error: ${it.message}")
            }.also {
                addDisposable(it)
            }
    }

    fun refresh() {
        _refreshing.value = true

        repo.syncAll().subscribe({ (done, total) ->
            Timber.d("sync all pending: $done/$total")
        },{
            Timber.e(it)
        }).also { addDisposable(it) }

        repo.getAllTask()
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                if (it.isEmpty()) {
                    //showAlertMessage("There is no data")
                    Timber.e("There is no data")
                }
                _pickupTask.value = it
                mapData(it)
                _refreshing.value = false
            }) {
                _refreshing.value = false
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
                else
                    showAlertMessage("cannot load data, with error: ${it.message}")
            }.also {
                addDisposable(it)
            }
    }

    private fun acceptPickupTask(taskId: String) {
        val body = PickupTaskAcception(action = PARAMS_PICKUP_TASK_ACCEPTED)

        repo.acceptTask(taskId, body)
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                if (it.status == PARAMS_PICKUP_TASK_ACCEPTED) {
                    //showAlertMessage(context.getString(R.string.sentence_booking_has_been_accepted) + " no.$taskId")
                    deleteNotification()
                    requestItem()
                }else{
                    showAlertMessage(it.status!!)
                }
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    private fun deleteNotification() {
        Completable.fromCallable {
            notRepo.deleteNotification(bookingId)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun rejectBooking(rejectStatus: BookingRejectStatusModel, note: String) {
        val body = PickupTaskAcception(
            PARAMS_PICKUP_TASK_REJECTED,
            rejectStatus.subCatOrderID!!.toInt(),
            note
        )

        repo.acceptTask(taskId, body)
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                if (it.status == PARAMS_PICKUP_TASK_REJECTED) {
                    showAlertMessage(context.getString(R.string.sentence_booking_has_been_rejected))
                    deleteNotification()
                    requestItem()
                }else{
                    showAlertMessage(it.status!!)
                }
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    fun showConfirmAcceptationDialog(context: Context, taskId: String, bookingCode: String?) {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setMessage("Confirm accept booking ${bookingCode.let { "no. $it" }}")
        mBuilder.setPositiveButton(context.getString(R.string.ok), null)
        mBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                mDialog.dismiss()
                acceptPickupTask(taskId)
            }
        }
        mDialog.show()
    }

    private fun mapData(data: List<PickupTask>) {
        try {
            var newBookingCount = 0
            var inProgressCount = 0
            var completedCount = 0
            _newPickupTask.value = listOf()
            _inProgressPickupTask.value = listOf()
            _completedPickupTask.value = listOf()

            for ((status, list) in data.groupBy { it.status }) {
                Timber.d("mapData $status $list")
                when (status) {
                    PARAMS_PICKUP_TASK_NEW_BOOKING -> {
                        _newPickupTask.value = list
                        newBookingCount = list.size
                    }
                    PARAMS_PICKUP_TASK_IN_PROGRESS -> {
                        _inProgressPickupTask.value = list
                        inProgressCount = list.size
                    }
                    PARAMS_PICKUP_TASK_COMPLETED -> {
                        _completedPickupTask.value = list
                        completedCount = list.size
                    }
                }
            }
            _tabTaskCounter.value = listOf(
                newBookingCount, inProgressCount, completedCount
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun search(id: String) {
        val resultList = dataSearch.value!!.items!!
            .filter { p -> p.bookingCode != null }
            .filter { p ->
                p.bookingCode!!.toLowerCase(Locale.ENGLISH).trim()
                    .contains(id.toLowerCase(Locale.ENGLISH).trim())
            }

        val arrResult: ArrayList<PickupTask> = dataSearch.value!!.items!!
            .filter { p ->
                p.customerCode.toLowerCase(Locale.ENGLISH).trim().contains(id.toLowerCase(Locale.ENGLISH).trim()) ||
                        p.customerName.toLowerCase(Locale.ENGLISH).trim().contains(
                            id.toLowerCase(
                                Locale.ENGLISH
                            ).trim()
                        ) ||
                        p.senderName.toLowerCase(Locale.ENGLISH).trim().contains(
                            id.toLowerCase(
                                Locale.ENGLISH
                            ).trim()
                        ) ||
                        p.tel.toLowerCase(Locale.ENGLISH).trim().contains(id.toLowerCase(Locale.ENGLISH).trim())
            } as ArrayList<PickupTask>
        arrResult.addAll(resultList)
        _dataSearchResult.value = arrResult.distinct()
    }

    fun itemClick(item: PickupTask) {
        if (checkLastClickTime())
            _itemClick.value = item
    }

    fun clearItemClick() {
            _itemClick.value = PickupTask()
    }

    fun onActionPickup(item: PickupTask) {
        if (checkLastClickTime()) {
            _actionPickup.value = item
        }
    }

    fun phoneCall(tel: String) {
        if (checkLastClickTime())
            _phoneCall.value = tel
    }

    fun showAddress(location: Location) {
        if (!checkLastClickTime()) return
        if (location.latitude.isNullOrBlank() || location.longitude.isNullOrBlank()) {
            context.resources.getString(R.string.dialog_alert_location_out_of_service).also {
                showAlertMessage(it)
            }
        } else {
            _location.value = location
        }
    }

    fun bookingAccept(item: PickupTask) {
        if (checkLastClickTime()) {
            bookingId = item.bookingCode!!
            _bookingAccept.value = Event(item)
        }
    }

    fun bookingReject(item: PickupTask) {
        if (checkLastClickTime()) {
            bookingId = item.bookingCode!!
            taskId = item.id
            _bookingReject.value = Event(item)
        }

    }

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }

    fun getData(): PickupTaskList {
        val t = pickupTask.value!! as ArrayList<PickupTask>
        return PickupTaskList(t)
    }

    fun setDataSearch(data: PickupTaskList) {
        _dataSearch.value = data
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    fun initNotificationIndicator() {
        notRepo.getNotificationByUser(user.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _notificationIndicator.value = it.isNotEmpty()
            }
            .also {
                addDisposable(it)
            }
    }
}
