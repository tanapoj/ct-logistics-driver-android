package com.scgexpress.backoffice.android.ui.delivery.task

import android.app.Application
import android.content.Context
import android.os.SystemClock
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Const.BUTTON_CLICKED_DELAY
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_REVERSE
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_REVERSE_NONE
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_RETENTION
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.delivery.DeliveryReverseFilter
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.model.delivery.DeliveryTaskList
import com.scgexpress.backoffice.android.model.delivery.TitleDeliveryCompleted
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.ContextWrapper
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DeliveryTaskViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: DeliveryRepository,
    private val notRepo: NotificationLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _pickupTask = MutableLiveData<List<DeliveryTask>>()
    private val pickupTask: LiveData<List<DeliveryTask>>
        get() = _pickupTask

    private val _newDeliveryTask = MutableLiveData<List<DeliveryTask>>()
    val newDeliveryTask: LiveData<List<DeliveryTask>>
        get() = _newDeliveryTask

    private val _inProgressDeliveryTask = MutableLiveData<List<DeliveryTask>>()
    val inProgressDeliveryTask: LiveData<List<DeliveryTask>>
        get() = _inProgressDeliveryTask

    private val _inProgressReverse = MutableLiveData<List<DeliveryTask>>()
    val inProgressReverse: LiveData<List<DeliveryTask>>
        get() = _inProgressReverse

    private val _inProgressReverseNon = MutableLiveData<List<DeliveryTask>>()
    val inProgressReverseNon: LiveData<List<DeliveryTask>>
        get() = _inProgressReverseNon

    private val _inProgressFilter = MutableLiveData<List<DeliveryTask>>()
    val inProgressFilter: LiveData<List<DeliveryTask>>
        get() = _inProgressFilter

    private val _completedDeliveryTask = MutableLiveData<List<TitleDeliveryCompleted>>()
    val completedDeliveryTask: LiveData<List<TitleDeliveryCompleted>>
        get() = _completedDeliveryTask

    private val _cDeliveryTaskCompleted = MutableLiveData<List<DeliveryTask>>()
    val cDeliveryTaskCompleted: LiveData<List<DeliveryTask>>
        get() = _cDeliveryTaskCompleted

    private val _cDeliveryTaskReverse = MutableLiveData<List<DeliveryTask>>()
    val cDeliveryTaskReverse: LiveData<List<DeliveryTask>>
        get() = _cDeliveryTaskReverse

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _dataSearch: MutableLiveData<DeliveryTaskList> = MutableLiveData()
    private val dataSearch: LiveData<DeliveryTaskList>
        get() = _dataSearch

    private var _dataSearchResult: MutableLiveData<List<DeliveryTask>> = MutableLiveData()
    val dataSearchResult: LiveData<List<DeliveryTask>>
        get() = _dataSearchResult

    private val _itemClick: MutableLiveData<DeliveryTask> = MutableLiveData()
    val itemClick: LiveData<DeliveryTask>
        get() = _itemClick

    private val _actionPickup: MutableLiveData<DeliveryTask> = MutableLiveData()
    val actionPickup: LiveData<DeliveryTask>
        get() = _actionPickup

    private val _phoneCall: MutableLiveData<String> = MutableLiveData()
    val phoneCall: LiveData<String>
        get() = _phoneCall

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location>
        get() = _location

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

    private val _reverseCounter: MutableLiveData<List<Int>> = MutableLiveData()
    val reverseCounter: LiveData<List<Int>>
        get() = _reverseCounter

    private var _serviceType: MutableLiveData<Map<Int, String>> = MutableLiveData()
    val serviceType: LiveData<Map<Int, String>>
        get() = _serviceType

    private var _sizing: MutableLiveData<Map<Int, String>> = MutableLiveData()
    val sizing: LiveData<Map<Int, String>>
        get() = _sizing

    var mLastClickTime: Long = 0

    fun requestItem() {
        loadServiceType().subscribe {
            loadParcelSize().subscribe {
                getDeliveryTasks()
            }.also {
                addDisposable(it)
            }
        }.also {
            addDisposable(it)
        }

        _tabTaskCounter.value = listOf(0, 0)
        _reverseCounter.value = listOf(0, 0, 0)
    }

    private fun getDeliveryTasks() {
        repo.getAllTask()
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it.isEmpty()) {
                    showAlertMessage("There is no data loaded from API or offline-data")
                }
                val data = setServiceAndSizeName(it)
                _pickupTask.value = data
                mapData(data)
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
                else
                    showAlertMessage("cannot load data, with error: ${it.message}")
            }.also {
                addDisposable(it)
            }
    }

    fun refresh(filterPosition: Int) {
        _refreshing.value = true

        repo.getAllTask()
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                if (it.isEmpty()) {
                    showAlertMessage("There is no data")
                }
                val data = setServiceAndSizeName(it)
                _pickupTask.value = data
                mapData(data)
                checkFilter(filterPosition)
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

    private fun mapData(data: List<DeliveryTask>) {
        try {
            val d: ArrayList<TitleDeliveryCompleted> = arrayListOf()
            var inProgressCount = 0
            var completedCount = 0
            for ((status, list) in data.groupBy { it.deliveryStatus }) {
                when (status) {
                    PARAMS_DELIVERY_TASK_IN_PROGRESS -> {
                        _inProgressDeliveryTask.value = list
                        _inProgressFilter.value = list
                        inProgressCount = list.size
                        mapDataReverse(list)
                    }
                    PARAMS_DELIVERY_TASK_COMPLETED, PARAMS_DELIVERY_TASK_RETENTION -> {
                        d.add(TitleDeliveryCompleted(status, list))
                        completedCount += list.size
                    }
                }
            }
            _completedDeliveryTask.value = d
            _tabTaskCounter.value = listOf(inProgressCount, completedCount)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun mapDataReverse(data: List<DeliveryTask>) {
        try {
            var reverseCount = 0
            var reverseNonCount = 0
            for ((status, list) in data.groupBy { it.productReverseStatus }) {
                when (status) {
                    PARAMS_DELIVERY_REVERSE -> {
                        _inProgressReverse.value = list
                        reverseCount = list.size
                    }
                    PARAMS_DELIVERY_REVERSE_NONE -> {
                        _inProgressReverseNon.value = list
                        reverseNonCount = list.size
                    }
                }
            }
            _reverseCounter.value =
                listOf(reverseCount + reverseNonCount, reverseCount, reverseNonCount)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun mapDataReverseCompleted(data: List<DeliveryTask>): List<DeliveryReverseFilter> {
        val btnReserveTextList =
            listOf(R.string.all, R.string.reverse, R.string.non_reverse)

        val dataList: List<DeliveryReverseFilter> = listOf(
            DeliveryReverseFilter(
                "${context.getString(btnReserveTextList[0])} (${data.size})",
                data.size,
                data
            ),
            DeliveryReverseFilter(context.getString(btnReserveTextList[1]), 0, listOf()),
            DeliveryReverseFilter(context.getString(btnReserveTextList[2]), 0, listOf())
        )
        try {
            for ((status, list) in data.groupBy { it.productReverseStatus }) {
                when (status) {
                    PARAMS_DELIVERY_REVERSE -> {
                        dataList[1].itemList = list
                        dataList[1].count = list.size
                        dataList[1].title =
                            "${context.getString(btnReserveTextList[1])} (${dataList[1].count})"
                    }
                    PARAMS_DELIVERY_REVERSE_NONE -> {
                        dataList[2].itemList = list
                        dataList[2].count = list.size
                        dataList[2].title =
                            "${context.getString(btnReserveTextList[2])} (${dataList[2].count})"
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return dataList
    }

    fun search(id: String) {
        val resultList = dataSearch.value!!.items!!
            .filter { p -> p.trackingCode != null }
            .filter { p ->
                p.trackingCode.toLowerCase(Locale.ENGLISH).trim()
                    .contains(id.toLowerCase(Locale.ENGLISH).trim())
            }

        val arrResult: ArrayList<DeliveryTask> = dataSearch.value!!.items!!
            .filter { p ->
                p.senderCode.toLowerCase(Locale.ENGLISH).trim().contains(id.toLowerCase(Locale.ENGLISH).trim()) ||
                        p.senderName.toLowerCase(Locale.ENGLISH).trim().contains(
                            id.toLowerCase(
                                Locale.ENGLISH
                            ).trim()
                        ) ||
                        p.recipientName.toLowerCase(Locale.ENGLISH).trim().contains(
                            id.toLowerCase(
                                Locale.ENGLISH
                            ).trim()
                        ) ||
                        p.recipientTel.toLowerCase(Locale.ENGLISH).trim().contains(id.toLowerCase(Locale.ENGLISH).trim())
            } as ArrayList<DeliveryTask>
        arrResult.addAll(resultList)
        _dataSearchResult.value = arrResult.distinct()
    }

    fun itemClick(item: DeliveryTask) {
        if (checkLastClickTime())
            _itemClick.value = item
    }

    fun onActionPickup(item: DeliveryTask) {
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

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }

    fun getData(): DeliveryTaskList {
        val t = pickupTask.value!! as ArrayList<DeliveryTask>
        return DeliveryTaskList(t)
    }

    fun setDataSearch(data: DeliveryTaskList) {
        _dataSearch.value = data
    }

    fun filterList(position: Int, btnReserveList: List<Button>) {
        btnReserveList[position].setBackgroundResource(R.drawable.button_green_background)
        btnReserveList[position].setTextColor(ContextCompat.getColor(context, R.color.white))
        for (i in btnReserveList.indices) {
            if (i != position) {
                btnReserveList[i].setBackgroundResource(R.drawable.button_border_green_background)
                btnReserveList[i].setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }
        }
    }

    fun checkFilter(position: Int) {
        when (position) {
            0 -> _inProgressFilter.value = _inProgressDeliveryTask.value
            1 -> _inProgressFilter.value = _inProgressReverse.value
            2 -> _inProgressFilter.value = _inProgressReverseNon.value
        }
    }

    private fun setServiceAndSizeName(data: List<DeliveryTask>): List<DeliveryTask> {
        try {
            val services = serviceType.value
            val sizings = sizing.value
            for (item in data) {
                item.serviceName = services!![item.service] ?: error("")
                item.sizeName = sizings!![item.size] ?: error("")
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return data
    }

    private fun loadServiceType(): Completable {
        return Completable.create { emitter ->
            try {
                repo.getAllServiceType(ContextWrapper(context))
                    .scheduleBy(rxThreadScheduler)
                    .subscribe({ list ->
                        _serviceType.value = list.map { it.first to it.second }.toMap()
                        emitter.onComplete()
                    }, {
                        /**/
                    }).also {
                        addDisposable(it)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(e)
            }
        }.scheduleBy(rxThreadScheduler)
    }

    private fun loadParcelSize(): Completable {
        return Completable.create { emitter ->
            try {
                repo.getAllParcelSize(ContextWrapper(context))
                    .scheduleBy(rxThreadScheduler)
                    .subscribe({ list ->
                        _sizing.value = list.map { it.first to it.second }.toMap()
                        emitter.onComplete()
                    }, {
                    }).also {
                        addDisposable(it)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(e)
            }
        }.scheduleBy(rxThreadScheduler)
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

    fun getCodCount(itemList: List<DeliveryTask>): String {
        var codCounted = 0
        for (item in itemList) {
            if (item.codAmount != null) {
                codCounted += 1
            }
        }
        return codCounted.toString()
    }

    fun getCodSum(itemList: List<DeliveryTask>): String {
        var cod = .0
        for (item in itemList) {
            if (item.codAmount != null)
                cod += item.codAmount!!
        }
        return cod.toCurrencyFormat()
    }
}
