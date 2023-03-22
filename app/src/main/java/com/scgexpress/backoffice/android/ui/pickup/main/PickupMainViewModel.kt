package com.scgexpress.backoffice.android.ui.pickup.main

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgPostalcode
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.tracking.Tracking
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.pickup.ContextWrapper
import com.scgexpress.backoffice.android.repository.pickup.IdAndValue
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.math.max

class PickupMainViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val loginPreference: LoginPreference,
    private val loginRepository: LoginRepository,
    private val pickupRepo: PickupRepository,
    private val masterDataRepo: DataRepository,
    private val pickupPreference: PickupPreference
) : RxAndroidViewModel(application) {

    val context: Context = application

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    var taskIdList: List<String>?
        get() = pickupPreference.currentScanningTaskIdList
        set(value) {
            pickupPreference.currentScanningTaskIdList = value
        }

    private var organization: TblOrganization = TblOrganization()

    private var customerCode: String = ""
    private var taskId: String = ""

    lateinit var task: PickupTask

    fun setTaskIdAndCustomerCode(taskId: String, customerCode: String){

        this.taskId = taskId
        this.customerCode = customerCode

        if (taskId.isBlank()) {
            task = PickupTask(id = taskId, isNewGenerateTask = true, customerCode = customerCode)
            return
        }

        pickupRepo.getTaskById(taskId).subscribe({
            task = it
        }, {
            Timber.e(it)
            _dialogAlertMessage.value =
                Event("cannot load task id $it, with error: (${it.message})")
        }).also {
            addDisposable(it)
        }
    }

    private var _shipperCode: MutableLiveData<String> = MutableLiveData()
    val shipperCode: LiveData<String>
        get() = _shipperCode

    private var _bookingCode: MutableLiveData<String> = MutableLiveData()
    val bookingCode: LiveData<String>
        get() = _bookingCode

    //goto

    private var _gotoSummary: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val gotoSummary: LiveData<Event<Boolean>>
        get() = _gotoSummary

    // status count

    private var _statusCount: MutableLiveData<Triple<Int, Int, Int>> = MutableLiveData()
    val statusCount: LiveData<Triple<Int, Int, Int>>
        get() = _statusCount

    //list

    enum class SpinnerType {
        ServiceType, Sizing
    }

    private var _spinnerList: MutableLiveData<Pair<SpinnerType, List<IdAndValue>>> =
        MutableLiveData()
    val spinnerList: LiveData<Pair<SpinnerType, List<IdAndValue>>>
        get() = _spinnerList

    private var _spinnerSelectIndex: MutableLiveData<Pair<Int?, Int?>> = MutableLiveData()
    val spinnerSelectIndex: LiveData<Pair<Int?, Int?>>
        get() = _spinnerSelectIndex

    // info

    private var _postalCodeList: MutableLiveData<List<TblScgPostalcode>> = MutableLiveData()
    val postalCodeList: LiveData<List<TblScgPostalcode>>
        get() = _postalCodeList

    private var _remember: MutableLiveData<Boolean> = MutableLiveData()
    val remember: LiveData<Boolean>
        get() = _remember

    //info

    private var _hasCod: MutableLiveData<Boolean> = MutableLiveData()
    val hasCod: LiveData<Boolean>
        get() = _hasCod

    private var _hasCarton: MutableLiveData<Boolean> = MutableLiveData()
    val hasCarton: LiveData<Boolean>
        get() = _hasCarton

    private var _setHasCarton: MutableLiveData<Boolean> = MutableLiveData()
    val setHasCarton: LiveData<Boolean>
        get() = _setHasCarton

    private var _assortCodeText: MutableLiveData<String> = MutableLiveData()
    val assortCodeText: LiveData<String>
        get() = _assortCodeText

    //edittext

    private var _trackingCode: MutableLiveData<String> = MutableLiveData()
    val trackingCode: LiveData<String>
        get() = _trackingCode

    private var _isTrackingCodeOk: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { postValue(false) }
    val isTrackingCodeOk: LiveData<Boolean>
        get() = _isTrackingCodeOk

    private var _zipCode: MutableLiveData<String> = MutableLiveData()
    val zipCode: LiveData<String>
        get() = _zipCode

    private var _codAmount: MutableLiveData<String> = MutableLiveData()
    val codAmount: LiveData<String>
        get() = _codAmount

    //tv fee

    private var _totalFee: MutableLiveData<Double> = MutableLiveData()
    val totalFee: LiveData<Double>
        get() = _totalFee

    private var _deliveryFee: MutableLiveData<Double> = MutableLiveData()
    val deliveryFee: LiveData<Double>
        get() = _deliveryFee

    private var _cartonFee: MutableLiveData<Double> = MutableLiveData()
    val cartonFee: LiveData<Double>
        get() = _cartonFee

    private var _codFee: MutableLiveData<Double> = MutableLiveData()
    val codFee: LiveData<Double>
        get() = _codFee

    //tracking list

    private val _pendingTrackingList: MutableLiveData<List<PickupScanningTrackingEntity>> =
        MutableLiveData()
    val pendingTrackingList: LiveData<List<PickupScanningTrackingEntity>>
        get() = _pendingTrackingList

    private var _scannedTrackingList: MutableLiveData<List<PickupScanningTrackingEntity>> =
        MutableLiveData()
    val scannedTrackingList: LiveData<List<PickupScanningTrackingEntity>>
        get() = _scannedTrackingList

    //msg

    private val _dialogConfirmAddExtraTracking = MutableLiveData<Event<String>>()
    val dialogConfirmAddExtraTracking: LiveData<Event<String>>
        get() = _dialogConfirmAddExtraTracking

    private val _dialogAlertMessage = MutableLiveData<Event<String>>()
    val dialogAlertMessage: LiveData<Event<String>>
        get() = _dialogAlertMessage

    //photo
    private val _photoBitmapSender = MutableLiveData<Bitmap?>()
    val photoBitmapSender: LiveData<Bitmap?>
        get() = _photoBitmapSender

    private val _photoBitmapRecipient = MutableLiveData<Bitmap?>()
    val photoBitmapRecipient: LiveData<Bitmap?>
        get() = _photoBitmapRecipient

    //exit (on finish)

    private val _onFinish = MutableLiveData<Boolean>()
    val onFinish: LiveData<Boolean>
        get() = _onFinish

    private val _enableRegister = MutableLiveData<Boolean>()
    val enableRegister: LiveData<Boolean>
        get() = _enableRegister

    private val _enableSeeSummary = MutableLiveData<Boolean>()
    val enableSeeSummary: LiveData<Boolean>
        get() = _enableSeeSummary

    //Data Holder

    object DataHolder {
        var serviceId = ""
        var sizeId = ""
        var senderImgUrl: String? = null
        var receiverImgUrl: String? = null
        var isRemember = false
        var trackingCode = ""
        var allowExtraTrackingCode = false
        var selectedPostalId = 0
        var zipCode = ""
        var deliveryFee = .0
        var codFee = .0
        var cartonFee = .0
        var codAmount = .0
        var hasCarton = false
        var parcelCount = 0
        var senderPhoto: File? = null
        var recipientPhoto: File? = null
        var isExtra = false

        fun toTracking() = Tracking(
            trackingCode, "", null, serviceId.toInt(), sizeId.toInt(), zipCode, isExtra = true
        )

        fun toScanningTracking(taskId: String) = PickupScanningTrackingEntity(
            taskId = taskId,
            tracking = trackingCode,
            senderImgUrl = senderImgUrl,
            senderImgPath = senderPhoto?.canonicalPath,
            receiverImgUrl = receiverImgUrl,
            receiverImgPath = recipientPhoto?.canonicalPath,
            zipcode = zipCode,
            serviceId = serviceId.toInt(),
            sizeId = sizeId.toInt(),
            deliveryFee = deliveryFee,
            cartonFee = cartonFee,
            codFee = codFee,
            codAmount = codAmount,
            scanAt = Utils.getCurrentTimestamp(),
            isExtra = isExtra
        )
    }

    private fun clearData() = with(DataHolder) {

        _trackingCode.value = ""
        _zipCode.value = ""
        if (codAmount > 0) {
            _codAmount.value = ""
        }
        _enableRegister.value = false

        _photoBitmapSender.value = null
        _photoBitmapRecipient.value = null

        if (!isRemember) {
            loadServiceType()()
            _spinnerSelectIndex.value = 2 to 0 //service:Normal(2), size=0
        }
        _setHasCarton.value = false

        //serviceId = ""
        //sizeId = ""
        senderImgUrl = null
        receiverImgUrl = null
        senderPhoto = null
        recipientPhoto = null
        //isRemember = false
        trackingCode = ""
        allowExtraTrackingCode = false
        selectedPostalId = 0
        zipCode = ""
        deliveryFee = .0
        codFee = .0
        cartonFee = .0
        codAmount = .0
        hasCarton = false
        parcelCount = 0
        isExtra = false
    }

    private val trackingListHolder = PairMovableMutableList<PickupScanningTrackingEntity>()
    private var hasPendingTrackingList = true

//    fun loadData1() {
//
//        val list = listOf(
//            PickupScanningTrackingEntity("100", "112345678900", null, null, null, null, "10120", 5, 2, 100.0, null, null, null, "", 1562744135171, "3245436345", "111222333", false),
//            PickupScanningTrackingEntity("100", "112345678901", null, null, null, null, "10120", 5, 2, 200.0, null, null, null, "", 1562744145171, "3245436345", "111222333", false),
//            PickupScanningTrackingEntity("100", "112345678902", null, null, null, null, "10120", 5, 2, 300.0, null, 1000.0, null, "", 1562744145171, "3245436345", "111222333", false),
//            PickupScanningTrackingEntity("100", "112345678903", null, null, null, null, "10120", 5, 2, 400.0, null, 2000.0, null, "", 1562744145171, "3245436345", "111222333", false),
//            PickupScanningTrackingEntity("100", "112345678904", null, null, null, null, "10120", 5, 2, 500.0, 40.0, null, null, "", 1562744145171, "3245436345", "111222333", false),
//            PickupScanningTrackingEntity("100", "112345678905", null, null, null, null, "10120", 5, 2, 600.0, 50.0, null, null, "", 1562744145171, "3245436345", "111222333", false)
//        )
//
//        pickupRepo.submitScanningTracking("100", "1543", "100148", 2, "", "", 13.0 to 100.0, list).subscribe({
//            Timber.d("TEST 1 = $it")
//        }, {
//
//        }).also { addDisposable(it) }
//    }

    fun loadData() {

        Timber.d("VM.loadData taskId=$taskId")
        _setHasCarton.value = DataHolder.hasCarton

        if (taskId.isBlank()) {
            _bookingCode.value = null
            _shipperCode.value = null
            hasPendingTrackingList = false
            onTrackingListChange()
            masterDataRepo.getOrganization(customerCode).subscribe({ org ->
                Timber.d("1 load organization from master=$org")
                organization = org
                _shipperCode.value = formatShipperTitle(org.customerCode, org.name.toString())
            }, Timber::e).also {
                addDisposable(it)
            }
            return
        }

        pickupRepo.getTaskById(taskId).flatMap {
            task = it
            Timber.d("VM.loadData done get task=$task")
            _bookingCode.value = "${it.bookingCode}"
            masterDataRepo.getOrganization(task.customerCode)
        }.onErrorResumeNext {
            Timber.d("1 load organization from master customerCode:$customerCode missing use senderCode:${task.senderCode}")
            masterDataRepo.getOrganization(task.senderCode)
        }.subscribe({ org ->
            Timber.d("2 load organization from master=$org")
            organization = org
            _shipperCode.value = formatShipperTitle(task.customerCode, task.customerName)

            loadTodoTrackingList(taskId)()
            loadServiceType()()

        }, {
            Timber.e(it)
            _dialogAlertMessage.value =
                Event("cannot load pickup task info, with error: (${it.message})")
        }).also {
            addDisposable(it)
        }
    }

    private fun formatShipperTitle(code: String? = "-", name: String? = "-") = "$code: $name"

    private fun loadScanningTracking(): Completable {
        return Completable.create { emitter ->
            pickupRepo.getScanningTracking().subscribe({ entries ->
                for (entry in entries) {
                    if (!trackingListHolder.move { it.tracking == entry.tracking }) {
                        trackingListHolder.addExtraItem(entry)
                    }
                }
                emitter.onComplete()
            }, emitter::onError)
        }
    }

    private fun loadTodoTrackingList(taskId: String): Completable {
        return pickupRepo.getTrackingsOf(taskId).flatMap { list ->

            trackingListHolder.first = list.map { info ->
                with(info) {
                    PickupScanningTrackingEntity(
                        tracking = tracking,
                        orderId = orderId,
                        pinbox = pinbox,
                        serviceId = service,
                        sizeId = size,
                        zipcode = zipcode
                    )
                }
            }.toMutableList()

            onTrackingListChange()
            Single.just(list)
        }.toCompletable().andThen {
            loadScanningTracking().subscribe({
                onTrackingListChange()
            }, {
                Timber.e(it)
            })
        }
    }

    fun loadServiceType(isCod: Boolean = false): Completable = Completable.create { emitter ->
        pickupRepo.getServiceType(ContextWrapper(context), isCod).scheduleBy(rxThreadScheduler)
            .flatMap {
                Timber.d("loadServiceType: $it")
                val (id, _) = it.first()
                loadParcelSize(id.mapServiceIdToType()) thenPass it
            }.subscribe({
                _spinnerList.value = SpinnerType.ServiceType to it
                emitter.onComplete()
            }, emitter::onError).also {
                addDisposable(it)
            }
    }

    private fun loadParcelSize(serviceType: PickupRepository.ServiceType): Completable {
        return Completable.create { emitter ->
            pickupRepo.getParcelSize(ContextWrapper(context), serviceType)
                .scheduleBy(rxThreadScheduler).subscribe({
                    val (id, _) = it.first()
                    _spinnerList.value = SpinnerType.Sizing to it
                    emitter.onComplete()
                }, emitter::onError).also {
                    addDisposable(it)
                }
        }
    }

    fun onRegister() = with(DataHolder) {

        fun uploadImage(
            photoFileSender: File?, photoFileRecipient: File?, trackingCode: String, userId: String
        ) {
            val fileUploader = FileUploader(context, loginRepository, loginPreference)
            pickupRepo.uploadAdditionalPhoto(
                fileUploader, photoFileSender, photoFileRecipient, trackingCode, userId
            ).flatMap { (sender, recipient) ->
                Timber.d("uploadAdditionalPhoto to S3 get url: sender:$sender recipient=$recipient")
                pickupRepo.updateScanningTrackingImageUrl(trackingCode, sender, recipient)
            }.subscribe({
                Timber.d("uploadAdditionalPhoto update url to ScanningTracking: affected rows = $it")
            }, {
                Timber.e(it)
                _dialogAlertMessage.value = Event("cannot save Image, with error: (${it.message})")
            })
        }

        when {
            //not tracking format
            !trackingCode.isTrackingId() -> {
                _dialogAlertMessage.value = Event(
                    context.resources.getString(
                        R.string.dialog_alert_tracking_wrong_format, trackingCode
                    )
                )
                _trackingCode.value = ""
            }
            //already exist
            trackingListHolder.second.any { it.tracking == trackingCode } -> {
                _dialogAlertMessage.value = Event(
                    context.resources.getString(
                        R.string.dialog_alert_tracking_is_already_exist, trackingCode
                    )
                )
                _trackingCode.value = ""
            }
            //able to move
            trackingListHolder.move { it.tracking == trackingCode } -> {
                allowExtraTrackingCode = false
                val entity = toScanningTracking(taskId).apply {
                    orderId =
                        trackingListHolder.second.first { it.tracking == trackingCode }.orderId
                }
                pickupRepo.addScanningTracking(entity)()
                uploadImage(senderPhoto, recipientPhoto, trackingCode, user.id)
                onTrackingListChange()
                clearData()
            }
            else -> if (allowExtraTrackingCode || !hasPendingTrackingList) {
                val scanTracking = toScanningTracking(taskId).apply {
                    isExtra = hasPendingTrackingList
                }

                Timber.d("VM.onRegister 1 : scanTracking=$scanTracking / trackingListHolder=${trackingListHolder.first.map { it.tracking }},${trackingListHolder.second.map { it.tracking }}")
                trackingListHolder.addExtraItem(scanTracking)
                pickupRepo.addScanningTracking(scanTracking)()
                uploadImage(senderPhoto, recipientPhoto, trackingCode, user.id)
                allowExtraTrackingCode = false
                Timber.d("VM.onRegister 2 : scanTracking=$scanTracking / trackingListHolder=${trackingListHolder.first.map { it.tracking }},${trackingListHolder.second.map { it.tracking }}")
                onTrackingListChange()
                clearData()
            } else {
                _dialogConfirmAddExtraTracking.value = Event(trackingCode)
            }
        }

    }

    fun holdBackScannedTracking(trackingCode: String) {
        Timber.d("VM.holdBackScannedTracking 1 : trackingCode=$trackingCode / trackingListHolder=${trackingListHolder.first.map { it.tracking }},${trackingListHolder.second.map { it.tracking }}")
        if (trackingListHolder.moveBack { it.tracking == trackingCode }) {
            Timber.d("VM.holdBackScannedTracking 2 : trackingCode=$trackingCode / trackingListHolder=${trackingListHolder.first.map { it.tracking }},${trackingListHolder.second.map { it.tracking }}")
            pickupRepo.deleteScanningTracking(trackingCode).subscribe({
                onTrackingListChange()
            }, {
                Timber.e(it)
            }).also {
                addDisposable(it)
            }
        }
    }

    fun removeScannedTracking(trackingCode: String) {
        trackingListHolder.second.removeAll { it.tracking == trackingCode }
        pickupRepo.deleteScanningTracking(trackingCode).subscribe({
            onTrackingListChange()
        }, {
            Timber.e(it)
        }).also {
            addDisposable(it)
        }
    }

    private fun onDataChange() = with(DataHolder) {
        Timber.d("VM.onDataChange 1: DataHolder=$serviceId, $sizeId, $zipCode, $selectedPostalId")
        Timber.d("VM.onDataChange 2: DataHolder=${serviceId.isNotBlank()} && ${sizeId.isNotBlank()} && (${zipCode.isNotBlank()} || ${selectedPostalId > 0})")

        val (customerCode, branchId) = try {
            organization.customerCode!! to user.branchId.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException("organization or branchId must have value")
        }

        val deliveryFeeTask =
            if (serviceId.isNotBlank() && sizeId.isNotBlank() && (zipCode.isNotBlank() || selectedPostalId > 0)) {
                pickupRepo.calculateFreightFare(
                    branchId,
                    serviceId,
                    sizeId,
                    zipCode,
                    selectedPostalId,
                    parcelCount + 1,
                    customerCode
                )
            } else {
                Single.just(.0)
            }

        val cartonFeeTask = if (hasCarton && serviceId.isNotBlank() && sizeId.isNotBlank()) {
            pickupRepo.calculateCartonPrice(serviceId, sizeId).map {
                it.second //it = id to fee
            }
        } else {
            Single.just(-1.0)
        }

        val codFeeTask = if (codAmount > 0) {
            pickupRepo.calculateCodFee(codAmount, customerCode)
        } else {
            Single.just(.0)
        }

        Single.zip(deliveryFeeTask,
            cartonFeeTask,
            codFeeTask,
            Function3 { deFee: Double, ctFee: Double, codFee: Double ->
                Triple(deFee, ctFee, codFee)
            }).subscribe({ (rawDelivery, rawCarton, cod) ->
            val delivery = max(.0, rawDelivery)
            val carton = max(.0, rawCarton)
            Timber.d("deliveryFee=$delivery, carton=$carton, cod=$cod, serviceId=$serviceId, sizeId=$sizeId")

            _deliveryFee.value = delivery
            deliveryFee = delivery

            _cartonFee.value = rawCarton
            cartonFee = carton

            _codFee.value = cod
            codFee = cod

            val total = delivery + carton + cod
            _totalFee.value = total

            _enableRegister.value =
                !(trackingCode.isTrackingCod() && cod <= 0) && delivery > 0 && total > 0
        }, Timber::e).also {
            addDisposable(it)
            if(it is IllegalArgumentException){
                _dialogAlertMessage.value = Event(it.message!!)
            }
        }
    }

    private fun onTrackingListChange() {

        trackingListHolder.distinctItems { it.tracking }

        _pendingTrackingList.value = when {
            hasPendingTrackingList -> trackingListHolder.first.map { it.copy() }
            else -> null
        }
        _scannedTrackingList.value = when {
            !hasPendingTrackingList && trackingListHolder.second.isEmpty() -> null
            else -> trackingListHolder.second.map { it.copy() }
        }

        val fromBooking = trackingListHolder.second.count { !it.isExtra }
        val newTracking = trackingListHolder.second.size - fromBooking
        val total = task.totalCount
        //        val total = when {
        //            task.totalCount > 0 -> task.totalCount
        //            else -> trackingListHolder.first.size + fromBooking
        //        }
        setStatusCount(fromBooking, newTracking, total)

        DataHolder.parcelCount = total

        _enableSeeSummary.value =
            fromBooking + newTracking > 0 || trackingListHolder.second.isNotEmpty()

        Timber.d("VM.onTrackingListChange : trackingListHolder=${trackingListHolder.first.map { it.tracking }},${trackingListHolder.second.map { it.tracking }}")
        Timber.d("VM.onTrackingListChange : fromBooking=$fromBooking newTracking=$newTracking total=$total")

    }

    private fun setStatusCount(fromBooking: Int, newTracking: Int, total: Int) {
        _statusCount.value = Triple(fromBooking, newTracking, total)
    }

    fun onStart() {
        if (!DataHolder.isRemember) {
            _remember.value = false
        }

        // if ScanningTracking compare to ScannedTracking has changed
        pickupRepo.getScanningTracking().subscribe { list ->
            // remove remain item back to waiting for scan
            trackingListHolder.second.minusBy(list) { it.tracking }.forEach { removable ->
                holdBackScannedTracking(removable.tracking)
            }
        }.also {
            addDisposable(it)
        }
    }

    //value set from VIEW

    fun setService(serviceId: Int): Completable {
//        if (serviceId.toString() == DataHolder.serviceId) return Completable.complete()
        Timber.d("VM.setService($serviceId) ${serviceId.mapServiceIdToType()}")
        DataHolder.serviceId = "$serviceId"
        val emitter = loadParcelSize(serviceId.mapServiceIdToType())
        onDataChange()
        return emitter
    }

    fun setSize(sizeId: Int) {
        DataHolder.sizeId = "$sizeId"
        onDataChange()
    }

    fun setRemember(remember: Boolean) {
        DataHolder.isRemember = remember
    }

    fun setTrackingCode(trackingCode: String, alsoSetTrackingToView: Boolean = false) {
        Timber.d("VM.setTrackingCode: trackingCode=$trackingCode is=${trackingCode.isTrackingId()} isCod=${trackingCode.isTrackingCod()} orgCodAllow=${organization.codAllowed} organization=$organization")
        if (alsoSetTrackingToView) {
            _trackingCode.value = trackingCode
            return
        }

        if (trackingCode.isTrackingMismatch()) {
            _dialogAlertMessage.value = Event(
                context.resources.getString(
                    R.string.dialog_alert_tracking_wrong_format, trackingCode
                )
            )
            return
        }

        if (!trackingCode.isTrackingId()) {
            _isTrackingCodeOk.value = false
            return
        }


        val org = when {
            task.customerCode == PickupRepository.C2_CUSTOMER ->
                masterDataRepo
                    .getOrganization(task.senderCode)
                    .onErrorReturnItem(organization)
            else -> Single.just(organization)
        }

        org.subscribe({ lookupOrganization ->

            if (trackingCode.isTrackingCod() && lookupOrganization.isNotAllowCod()) {
                Timber.e("COD not allow for: $lookupOrganization\nof task: $task")
                _dialogAlertMessage.value =
                    Event(context.resources.getString(R.string.dialog_alert_tracking_cod_not_allow))
                return@subscribe
            }

            _isTrackingCodeOk.value = true
            _hasCod.value = trackingCode.isTrackingCod()
            DataHolder.trackingCode = trackingCode

            pickupRepo.getTracking(trackingCode).flatMap {
                Timber.d("load trackingCode=$trackingCode info = $it")
                loadServiceType(trackingCode.isTrackingCod()) thenPass it
            }.flatMap {
                Timber.d("set Service=${it.service}")
                val serviceId = it.service
                try {
                    checkNotNull(serviceId)
                    setService(serviceId) thenPass it
                } catch (e: IllegalStateException) {
                    Single.just(it)
                }
            }.subscribe({
                Timber.d("VM. pickupRepo.getTracking=$it")
                Timber.d("VM. DataHolder isRemember=${DataHolder.isRemember} service=${DataHolder.serviceId} size=${DataHolder.sizeId}")
                with(it) {
                    val serviceId = when {
                        service != DataHolder.serviceId.toInt() -> service
                        else -> null
                    }
                    val sizeId = when {
                        DataHolder.isRemember -> DataHolder.sizeId.toInt()
                        size ?: 0 > 0 -> size
                        else -> null
                    }
                    _spinnerSelectIndex.value = serviceId to sizeId
                    _zipCode.value = zipcode
                }
                onDataChange()
            }, {
                Timber.e(it)
                onDataChange()
            }).also {
                addDisposable(it)
            }

        }, {
            throw it
        }).also { addDisposable(it) }
    }

    fun setAllowExtraTrackingCode(allow: Boolean) {
        DataHolder.allowExtraTrackingCode = allow
    }

    fun setZipCode(zipCode: String) {
        DataHolder.zipCode = zipCode
        pickupRepo.getPostalInformation(zipCode).subscribe({
            val (postalList, assortCode) = it
            if (postalList.isNotEmpty()) {
                _postalCodeList.value = postalList
            }
            _assortCodeText.value =
                "${assortCode.address1} ${assortCode.address2} ${assortCode.assortCode}"
        }, {
            Timber.e("setZipcode: e ${it.message}")
            _assortCodeText.value = null
            _dialogAlertMessage.value = Event("zipcode not exist")
        }).also {
            addDisposable(it)
        }
    }

    fun setPostalId(id: Int?) {
        if (id == null) {
            DataHolder.selectedPostalId = 0
            _zipCode.value = ""
        } else {
            DataHolder.selectedPostalId = id
            onDataChange()
        }
    }

    fun setCodAmount(amount: Double) {
        if (DataHolder.codAmount == amount) return
        DataHolder.codAmount = amount
        onDataChange()
    }

    fun setHasCarton(hasSell: Boolean) {
        if (DataHolder.hasCarton == hasSell) return
        DataHolder.hasCarton = hasSell
        onDataChange()
    }

    fun onFinish() {
        pickupRepo.deleteAllScanningTracking().subscribe {
            _onFinish.value = true
        }.also {
            addDisposable(it)
        }
    }

    //Take Photo

    private val fileHelper = FileHelper.helper

    fun setSenderPhoto(bitmap: Bitmap) {
        val filename = "${Utils.getCurrentDateInMills()}-sender"
        fileHelper.saveImageJpeg(context, Const.DIRECTORY_PICKUP, filename, bitmap)
            ?.let { savedFile ->
                _photoBitmapSender.value = bitmap
                DataHolder.senderPhoto = savedFile
            }
    }

    fun setRecipientPhoto(bitmap: Bitmap) {
        val filename = "${Utils.getCurrentDateInMills()}-recipient"
        fileHelper.saveImageJpeg(context, Const.DIRECTORY_PICKUP, filename, bitmap)
            ?.let { savedFile ->
                _photoBitmapRecipient.value = bitmap
                DataHolder.recipientPhoto = savedFile
            }
    }

    private fun Int.mapServiceIdToType() = when (this) {
        2, 5 -> PickupRepository.ServiceType.Normal
        3, 6 -> PickupRepository.ServiceType.Chilled
        4, 7 -> PickupRepository.ServiceType.Frozen
        else -> throw IllegalStateException("service id ($this) does not allow")
    }

//    fun test() {
//        val list = listOf(
//            PickupScanningTrackingEntity(
//                "1",
//                "t-normal-1",
//                null,
//                null,
//                null,
//                null,
//                "10120",
//                5,
//                2,
//                100.0,
//                null,
//                null,
//                null,
//                "",
//                1562744135171,
//                "3245436345",
//                "111222333",
//                false
//            ), PickupScanningTrackingEntity(
//                "1",
//                "t-normal-2",
//                null,
//                null,
//                null,
//                null,
//                "10120",
//                5,
//                2,
//                200.0,
//                null,
//                null,
//                null,
//                "",
//                1562744145171,
//                "3245436345",
//                "111222333",
//                false
//            ), PickupScanningTrackingEntity(
//                "1",
//                "t-cod-3",
//                null,
//                null,
//                null,
//                null,
//                "10120",
//                5,
//                2,
//                300.0,
//                null,
//                1000.0,
//                null,
//                "",
//                1562744145171,
//                "3245436345",
//                "111222333",
//                false
//            ), PickupScanningTrackingEntity(
//                "1",
//                "t-cod-4",
//                null,
//                null,
//                null,
//                null,
//                "10120",
//                5,
//                2,
//                400.0,
//                null,
//                2000.0,
//                null,
//                "",
//                1562744145171,
//                "3245436345",
//                "111222333",
//                false
//            ), PickupScanningTrackingEntity(
//                "1",
//                "t-carton-5",
//                null,
//                null,
//                null,
//                null,
//                "10120",
//                5,
//                2,
//                500.0,
//                40.0,
//                null,
//                null,
//                "",
//                1562744145171,
//                "3245436345",
//                "111222333",
//                false
//            ), PickupScanningTrackingEntity(
//                "1",
//                "t-carton-6",
//                null,
//                null,
//                null,
//                null,
//                "10120",
//                5,
//                2,
//                600.0,
//                50.0,
//                null,
//                null,
//                "",
//                1562744145171,
//                "3245436345",
//                "111222333",
//                false
//            )
//        )
//
//        pickupPreference.resetRunningNumber()
//        pickupRepo.deleteAllPendingReceipt()()
//
//        val (lat, long) = 37.0 to 122.0
//
//        pickupRepo.submitScanningTracking(
//            "1", "999996", "1234", 1, "012-345-6789", "test@test.io", lat to long, list
//        ).subscribe({
//            Timber.d("VM.TEST 1: $it")
//            pickupRepo._getAllPendingReceipt().subscribe({
//                Timber.d("VM.TEST 2: -------------------")
//                for (x in it) Timber.d("VM.TEST 2: $x")
//            }, {
//
//            })
//        }, Timber::e).also {
//            addDisposable(it)
//        }
//    }
}
