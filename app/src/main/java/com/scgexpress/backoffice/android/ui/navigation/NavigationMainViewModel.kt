package com.scgexpress.backoffice.android.ui.navigation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Leg
import com.google.android.gms.maps.model.*
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.common.Const.PARAMS_ALL_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_CUSTOM
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_DEFAULT
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_FAR
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_NEAR
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK
import com.scgexpress.backoffice.android.common.maps.GoogleMapsHelper
import com.scgexpress.backoffice.android.common.maps.LatLngLocation
import com.scgexpress.backoffice.android.common.maps.LocationDistance
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.model.MarkerTask
import com.scgexpress.backoffice.android.model.ServiceTypeWithSizing
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.model.distance.AllTaskDistance
import com.scgexpress.backoffice.android.model.distance.DeliveryTaskDistance
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.distance.PickupTaskDistance
import com.scgexpress.backoffice.android.repository.delivery.ContextWrapper
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.ui.navigation.map.NavigationMapActivity
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject
import kotlin.collections.ArrayList

class NavigationMainViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val deliveryRepo: DeliveryRepository,
    private val pickupRepo: PickupRepository,
    private val dataRepo: DataRepository
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    val filterTypeList: List<String> = listOf(
        PARAMS_ALL_TASK,
        PARAMS_PICKUP_TASK,
        PARAMS_DELIVERY_TASK
    )
    lateinit var activity: NavigationMapActivity
    var mLastClickTime: Long = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var type: String = ""
    var directionParcelDeliveryList: ArrayList<DeliveryTaskDistance> = arrayListOf()
    var directionParcelPickupList: ArrayList<PickupTaskDistance> = arrayListOf()
    var directionParcelAllTaskList: ArrayList<AllTaskDistance> = arrayListOf()
    private var isStartedDirection = false
    private var isMoveCamera = false
    private var taskList: MutableList<Any> = mutableListOf()
    private var pickupTaskList: MutableList<PickupTask> = mutableListOf()
    private var deliveryTaskList: MutableList<DeliveryTask> = mutableListOf()
    private var sortTaskList: MutableList<Any> = mutableListOf()
    private var serviceTypeAndSizing: List<ServiceTypeWithSizing>? = null

    private val iconBitmapDeliveryTask =
        context.getDrawable(R.drawable.ic_baseline_archive_24dp_blue)!!.toBitmap()
    private val iconBitmapPickupTask =
        context.getDrawable(R.drawable.ic_baseline_unarchive_24dp_orange)!!.toBitmap()

    private val _sortedModeEnable = MutableLiveData<Map<String, Boolean>>()
    val sortedModeEnable: LiveData<Map<String, Boolean>>
        get() = _sortedModeEnable

    private val _marker = MutableLiveData<MarkerTask>()
    val marker: LiveData<MarkerTask>
        get() = _marker

    private val _drawPolyLine = MutableLiveData<List<LatLng>>()
    val drawPolyLine: LiveData<List<LatLng>>
        get() = _drawPolyLine

    private val _moveCamera = MutableLiveData<LatLng>()
    val moveCamera: LiveData<LatLng>
        get() = _moveCamera

    private val _displayTaskList = MutableLiveData<List<Any>>()
    val displayTaskList: LiveData<List<Any>>
        get() = _displayTaskList

    private val _actionDelivery: MutableLiveData<DeliveryTask> = MutableLiveData()
    val actionDelivery: LiveData<DeliveryTask>
        get() = _actionDelivery

    private val _actionPickup: MutableLiveData<PickupTask> = MutableLiveData()
    val actionPickup: LiveData<PickupTask>
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

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    fun loadData() {
        getServiceTypeAndSizing()()
        loadPickupTasks()
        loadDeliveryTasks()
    }

    private fun loadPickupTasks() {
        pickupRepo.getAllTask()
            .scheduleBy(rxThreadScheduler)
            .subscribe({ pickupTaskList ->
                taskList.addAll(pickupTaskList.filterInProgressPickup())
                _displayTaskList.value = taskList

                taskList.map {
                    when (it) {
                        is PickupTask -> {
                            this.pickupTaskList.add(it)
                            directionParcelPickupList.add(PickupTaskDistance(it))
                            directionParcelAllTaskList.add(
                                AllTaskDistance(
                                    PickupTaskDistance(it),
                                    null
                                )
                            )
                        }
                        else -> showAlertMessage("Items must be PickupTask")
                    }
                }
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
                else
                    showAlertMessage("cannot load data, with error: ${it.message}")
            }.also {
                addDisposable(it)
            }
    }

    private fun inProgressPickupTaskPredicate(pickupTask: PickupTask) =
        pickupTask.status == Const.PARAMS_PICKUP_TASK_IN_PROGRESS

    private fun List<PickupTask>.filterInProgressPickup() = filter(::inProgressPickupTaskPredicate)

    private fun loadDeliveryTasks() {
        deliveryRepo.getAllTask()
            .toSingle()
            .flatMap {
                getServiceTypeAndSizing() thenPassWith it
            }
            .scheduleBy(rxThreadScheduler)
            .subscribe({ (serviceTypeAndSizing, deliveryTaskList) ->
                taskList.addAll(
                    attachServiceAndSizeName(
                        deliveryTaskList,
                        serviceTypeAndSizing
                    ).filterInProgressDelivery().toMutableList()
                )
                _displayTaskList.value = taskList
                taskList.map {
                    when (it) {
                        is DeliveryTask -> {
                            this.deliveryTaskList.add(it)
                            directionParcelDeliveryList.add(DeliveryTaskDistance(it))
                            directionParcelAllTaskList.add(
                                AllTaskDistance(
                                    null,
                                    DeliveryTaskDistance(it)
                                )
                            )
                        }
                        else -> showAlertMessage("Items must be DeliveryTask")
                    }
                }
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
                else
                    showAlertMessage("cannot load data, with error: ${it.message}")
            }.also {
                addDisposable(it)
            }
    }

    private fun inProgressDeliveryTaskPredicate(deliveryTask: DeliveryTask) =
        deliveryTask.deliveryStatus == PARAMS_DELIVERY_TASK_IN_PROGRESS

    private fun List<DeliveryTask>.filterInProgressDelivery() =
        filter(::inProgressDeliveryTaskPredicate)

    private fun attachServiceAndSizeName(
        data: List<DeliveryTask>,
        serviceTypeAndSizing: List<ServiceTypeWithSizing>
    ): List<DeliveryTask> {
        return data.map { deliveryTask ->
            val serviceId = deliveryTask.service
            val sizeId = deliveryTask.size
            val serviceType = serviceTypeAndSizing.firstOrNull { it.id == serviceId }
                ?: error("ServiceId $serviceId not exist")
            val size = serviceType.sizeList.firstOrNull { it.id == sizeId }
                ?: error("SizingId $sizeId (ServiceId $serviceId) not exist")
            deliveryTask.apply {
                serviceName = serviceType.name
                sizeName = size.name
            }
        }
    }

    private fun setParcelableTaskList(sortTaskList: List<Any>) {
        when (type) {
            PARAMS_PICKUP_TASK -> {
                directionParcelPickupList.clear()
                directionParcelPickupList.addAll(sortTaskList.map { item ->
                    PickupTaskDistance(
                        item as PickupTask
                    )
                })
            }
            PARAMS_DELIVERY_TASK -> {
                directionParcelDeliveryList.clear()
                directionParcelDeliveryList.addAll(sortTaskList.map { item ->
                    DeliveryTaskDistance(
                        item as DeliveryTask
                    )
                })
            }
            PARAMS_ALL_TASK -> {
                directionParcelAllTaskList.clear()
                for (item in sortTaskList) {
                    when (item) {
                        is PickupTask -> directionParcelAllTaskList.add(
                            AllTaskDistance(
                                PickupTaskDistance(item), null
                            )
                        )
                        is DeliveryTask -> directionParcelAllTaskList.add(
                            AllTaskDistance(
                                null,
                                DeliveryTaskDistance(item)
                            )
                        )
                        else -> throw IllegalStateException("Items must be PickupTask or DeliveryTask")
                    }
                }
            }
        }
    }

    private fun getServiceTypeAndSizing(): Single<List<ServiceTypeWithSizing>> {
        return Single.create { emitter ->
            if (serviceTypeAndSizing != null) {
                emitter.onSuccess(serviceTypeAndSizing!!)
            } else {
                dataRepo.getServiceTypeWithSizing(ContextWrapper(context))
                    .scheduleBy(rxThreadScheduler)
                    .subscribe(
                        {
                            serviceTypeAndSizing = it
                            emitter.onSuccess(it)
                        }, emitter::onError
                    )
            }
        }
    }

    fun getLatLngCurrentLocation() {
        getCurrentLocation(context).subscribe({ (lat, lng) ->
            latitude = lat.let { if (it == .0) 0.0 else it }
            longitude = lng.let { if (it == .0) 0.0 else it }
        }, {
            Timber.e(it)
        }).also {
            addDisposable(it)
        }
    }

    fun requestDirection() {
        val directionParcelList: ArrayList<Any> = arrayListOf()
        when (type) {
            PARAMS_PICKUP_TASK -> {
                directionParcelList.addAll(directionParcelPickupList)
            }
            PARAMS_DELIVERY_TASK -> {
                directionParcelList.addAll(directionParcelDeliveryList)
            }
            PARAMS_ALL_TASK -> {
                directionParcelAllTaskList.map { (pickupTask, deliveryTask) ->
                    if (pickupTask != null) {
                        directionParcelList.add(pickupTask)
                    }
                    if (deliveryTask != null) {
                        directionParcelList.add(deliveryTask)
                    }
                }
            }
        }

        findDirectionPoint(directionParcelList)
    }

    private fun getDirection(parcelTaskList: List<Any>): Single<List<Direction>> {
        return Single.create { emitter ->
            getCurrentLocation(context).map { latLngList ->
                latLngList to parcelTaskList.map {
                    when (it) {
                        is PickupTaskDistance -> it.pickupTask.location.latitude to it.pickupTask.location.longitude
                        is DeliveryTaskDistance -> it.deliveryTask.recipientLocation.latitude to it.deliveryTask.recipientLocation.longitude
                        else -> throw IllegalStateException("Items must be PickupTaskDistance or DeliveryTaskDistance")
                    }
                }
            }.map { (currentLocation, destinationLocationList) ->
                lateinit var source: LatLngLocation
                lateinit var destination: LatLngLocation
                val (sourceLat, sourceLng) = currentLocation
                if (!isStartedDirection) {
                    source = LatLngLocation("$sourceLat", "$sourceLng")
                    isStartedDirection = true
                }
                destinationLocationList.map { (lat, lng) ->
                    destination = if (lat.isNullOrEmpty() && lng.isNullOrEmpty()) {
                        LatLngLocation(source.lat, source.lng)
                    } else {
                        LatLngLocation("$lat", "$lng")
                    }
                    GoogleMapsHelper.instance.getDirection(source, destination).also {
                        source = destination
                    }
                }.let { locationDistanceList ->
                    Single.zip(locationDistanceList) { list: Array<Any> ->
                        list.map { it as Direction }.toList()
                    }.scheduleBy(rxThreadScheduler).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                it.scheduleBy(rxThreadScheduler).subscribe(
                    emitter::onSuccess,
                    emitter::onError
                )
            }, emitter::onError)
        }
    }

    private fun getDistanceOrigin(taskList: List<Any>): Single<List<LocationDistance>> {
        return Single.create { emitter ->
            getCurrentLocation(context).map { latLngList ->
                latLngList to taskList.map {
                    when (it) {
                        is PickupTask -> it.location.latitude to it.location.longitude
                        is DeliveryTask -> it.recipientLocation.latitude to it.recipientLocation.longitude
                        else -> throw IllegalStateException("Items must be PickupTask or DeliveryTask")
                    }
                }
            }.map { (currentLocation, destinationLocationList) ->
                val (sourceLat, sourceLng) = currentLocation
                val source = LatLngLocation("$sourceLat", "$sourceLng")
                destinationLocationList.map { (lat, lng) ->
                    val destination = LatLngLocation("$lat", "$lng")
                    GoogleMapsHelper.instance.getDistance(source, destination)
                }.let { locationDistanceList ->
                    Single.zip(locationDistanceList) { list: Array<Any> ->
                        list.map { it as LocationDistance }.toList()
                    }.scheduleBy(rxThreadScheduler).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                it.thenEmit(emitter, rxThreadScheduler)
            }, emitter::onError)
        }
    }

    @SuppressLint("CheckResult")
    private fun findDirectionPoint(directionParcelList: List<Any>) {
        val markerList: ArrayList<Any> = arrayListOf()
        getDirection(directionParcelList).subscribe({ directionList ->
            directionList.map { direction ->
                if (direction.isOK) {
                    val legList: ArrayList<Leg> = arrayListOf()
                    for (item in direction.routeList) {
                        legList.addAll(item.legList)
                    }
                    val directionPositionList: MutableList<LatLng> = mutableListOf()
                    for (item in legList) {
                        directionPositionList.addAll(item.directionPoint)
                    }
                    setPolyLine(directionPositionList)
                } else {
                    showSnackbar(direction.status)
                }

                when (type) {
                    PARAMS_PICKUP_TASK -> {
                        markerList.addAll(directionParcelList)
                        setMarker(directionParcelList)
                    }
                    PARAMS_DELIVERY_TASK -> {
                        markerList.addAll(directionParcelList)
                        setMarker(directionParcelList)
                    }
                    PARAMS_ALL_TASK -> {
                        setMarker(directionParcelList)
                    }
                }
            }
        }, {
            throw IllegalStateException("DistanceList size must equal taskList size")
        })
        isStartedDirection = false
    }

    private fun setPolyLine(directionPositionList: List<LatLng>) {
        getLatLngCurrentLocation()
        if (!isMoveCamera) {
            val currentLocation = LatLng(latitude, longitude)
            _moveCamera.value = currentLocation
            isMoveCamera = true
        }
        _drawPolyLine.value = directionPositionList
    }

    private fun setMarker(directionPositionList: List<Any>) {
        directionPositionList.map {
            when (it) {
                is PickupTaskDistance -> {
                    if (!it.pickupTask.location.latitude.isNullOrEmpty() && !it.pickupTask.location.longitude.isNullOrEmpty()) {
                        val latLng = LatLng(
                            it.pickupTask.location.latitude!!.toDouble(),
                            it.pickupTask.location.longitude!!.toDouble()
                        )
                        _marker.value = MarkerTask(latLng,iconBitmapPickupTask,it.pickupTask.bookingCode)
                    }
                }
                is DeliveryTaskDistance -> {
                    if (!it.deliveryTask.recipientLocation.latitude.isNullOrEmpty() && !it.deliveryTask.recipientLocation.longitude.isNullOrEmpty()) {
                        val latLng = LatLng(
                            it.deliveryTask.recipientLocation.latitude!!.toDouble(),
                            it.deliveryTask.recipientLocation.longitude!!.toDouble()
                        )
                        _marker.value = MarkerTask(latLng,iconBitmapDeliveryTask,it.deliveryTask.trackingCode)
                    }
                }
                else -> throw IllegalStateException("Items must be PickupTaskDistance or DeliveryTaskDistance")
            }
        }
    }

    fun setSortModeBy(mode: String) {
        sortTaskList.clear()
        when (type) {
            PARAMS_PICKUP_TASK -> {
                sortTaskList = pickupTaskList.toMutableList()
            }
            PARAMS_DELIVERY_TASK -> {
                sortTaskList = deliveryTaskList.toMutableList()
            }
            PARAMS_ALL_TASK -> {
                sortTaskList = taskList
            }
        }
        when (mode) {
            PARAMS_FILTER_DISTANCE_NEAR -> {
                getDistanceOrigin(sortTaskList).subscribe({ distanceList ->
                    check(sortTaskList.size == distanceList.size) { "DistanceList size must equal taskList size" }
                    sortTaskList = sortTaskList.zip(distanceList).sortedBy { (_, distance) ->
                        distance.distance
                    }.map { (task, _) ->
                        task
                    }.toMutableList()
                    _displayTaskList.value = sortTaskList
                    setParcelableTaskList(sortTaskList)
                }, {
                    throw IllegalStateException("DistanceList size must equal taskList size")
                })
            }
            PARAMS_FILTER_DISTANCE_FAR -> {
                getDistanceOrigin(sortTaskList).subscribe({ distanceList ->
                    check(sortTaskList.size == distanceList.size) { "DistanceList size must equal taskList size" }
                    sortTaskList =
                        sortTaskList.zip(distanceList).sortedByDescending { (_, distance) ->
                            distance.distance
                        }.map { (task, _) ->
                            task
                        }.toMutableList()
                    _displayTaskList.value = sortTaskList
                    setParcelableTaskList(sortTaskList)
                }, {
                    throw IllegalStateException("DistanceList size must equal taskList size")
                })
            }
            PARAMS_FILTER_DISTANCE_DEFAULT -> {
                _displayTaskList.value = sortTaskList
                setParcelableTaskList(sortTaskList)
            }
        }
    }

    private fun setSortedModeEnable(
        near: Boolean = true,
        far: Boolean = true,
        default: Boolean = false,
        custom: Boolean = true
    ) {
        _sortedModeEnable.value = mapOf(
            PARAMS_FILTER_DISTANCE_NEAR to near,
            PARAMS_FILTER_DISTANCE_FAR to far,
            PARAMS_FILTER_DISTANCE_DEFAULT to default,
            PARAMS_FILTER_DISTANCE_CUSTOM to custom
        )
    }

    fun setDisplayCategoryTaskList(type: String) {
        sortTaskList.clear()
        this.type = type
        when (type) {
            PARAMS_PICKUP_TASK -> {
                _displayTaskList.value = pickupTaskList
                setSortedModeEnable(default = true)
            }
            PARAMS_DELIVERY_TASK -> {
                _displayTaskList.value = deliveryTaskList
                setSortedModeEnable(default = true)
            }
            PARAMS_ALL_TASK -> {
                _displayTaskList.value = taskList
                setSortedModeEnable(default = false)
            }
        }
    }

    fun filterByText(lookup: String) {
        if (lookup.isEmpty()) {
            setDisplayCategoryTaskList(type)
            return
        }

        var sortList: List<Any> = listOf()
        when (type) {
            PARAMS_PICKUP_TASK -> sortList = pickupTaskList
            PARAMS_DELIVERY_TASK -> sortList = deliveryTaskList
            PARAMS_ALL_TASK -> sortList = taskList
        }

        sortList = sortList.filter { task ->
            when (task) {
                is PickupTask -> {
                    when {
                        task.bookingCode?.containsIgnoreCase(lookup) == true -> true
                        task.customerCode.containsIgnoreCase(lookup) -> true
                        task.senderCode.containsIgnoreCase(lookup) -> true
                        task.customerName.containsIgnoreCase(lookup) -> true
                        task.senderName.containsIgnoreCase(lookup) -> true
                        task.tel.containsIgnoreCase(lookup) -> true
                        else -> false
                    }
                }
                is DeliveryTask -> {
                    when {
                        task.trackingCode.containsIgnoreCase(lookup) -> true
                        task.senderName.containsIgnoreCase(lookup) -> true
                        task.recipientName.containsIgnoreCase(lookup) -> true
                        task.recipientTel.containsIgnoreCase(lookup) -> true
                        else -> false
                    }
                }
                else -> false
            }
        }
        _displayTaskList.value = sortList
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < Const.BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    fun onActionDelivery(item: DeliveryTask) {
        if (checkLastClickTime()) {
            _actionDelivery.value = item
        }
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

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }

    private fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }
}

