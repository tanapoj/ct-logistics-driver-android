package com.scgexpress.backoffice.android.ui.pickup.summary

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.PairMovableMutableList
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.BookingPreference
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.pickup.ContextWrapper
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class PickupSummaryViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: PickupRepository,
    private val bookingPreference: BookingPreference,
    private val loginPreference: LoginPreference,
    private val dataRepository: DataRepository,
    private val calculatorRepository: CalculatorRepository
) : RxAndroidViewModel(application) {

    val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _deliveryFee: MutableLiveData<Double> = MutableLiveData()
    val deliveryFee: LiveData<Double>
        get() = _deliveryFee

    private var _serviceCharge: MutableLiveData<Double> = MutableLiveData()
    val serviceCharge: LiveData<Double>
        get() = _serviceCharge

    private var _codFee: MutableLiveData<Double> = MutableLiveData()
    val codFee: LiveData<Double>
        get() = _codFee

    private var _cartonFee: MutableLiveData<Double> = MutableLiveData()
    val cartonFee: LiveData<Double>
        get() = _cartonFee

    private var _total: MutableLiveData<Double> = MutableLiveData()
    val total: LiveData<Double>
        get() = _total

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _viewPhoto: MutableLiveData<PickupScanningTrackingEntity> = MutableLiveData()
    val viewPhoto: LiveData<PickupScanningTrackingEntity>
        get() = _viewPhoto

    private val _done = MutableLiveData<Event<Boolean>>()
    val done: LiveData<Event<Boolean>>
        get() = _done

    private val _businessCustomer = MutableLiveData<Boolean>()
    val businessCustomer: LiveData<Boolean>
        get() = _businessCustomer

    private var _serviceType: MutableLiveData<Map<Int, String>> = MutableLiveData()
    private val serviceType: LiveData<Map<Int, String>>
        get() = _serviceType

    private var _sizing: MutableLiveData<Map<Int, String>> = MutableLiveData()
    private val sizing: LiveData<Map<Int, String>>
        get() = _sizing

    //tracking list

    private var _scannedTrackingList: MutableLiveData<List<PickupScanningTrackingEntity>> =
        MutableLiveData()
    val scannedTrackingList: LiveData<List<PickupScanningTrackingEntity>>
        get() = _scannedTrackingList

    private val _enableSeeSummary = MutableLiveData<Boolean>()
    val enableSeeSummary: LiveData<Boolean>
        get() = _enableSeeSummary

    // status count

    private var _statusCount: MutableLiveData<Triple<Int, Int, Int>> = MutableLiveData()
    val statusCount: LiveData<Triple<Int, Int, Int>>
        get() = _statusCount

    // payment

    private var _paymentMethods = MutableLiveData<Event<List<Pair<Int, String>>>>()
    val paymentMethods: LiveData<Event<List<Pair<Int, String>>>>
        get() = _paymentMethods

    var taskId: String = ""
    var customerCode: String = ""
    var groupID: String = ""
    var taskTotalCount: Int = 0

    private val trackingListHolder = PairMovableMutableList<PickupScanningTrackingEntity>()

    //TODO Pickup summary : Remove this
    fun insertData() {
        loadServiceType().subscribe {
            loadParcelSize().subscribe {
                loadScanningTracking()
            }.also {
                addDisposable(it)
            }
        }.also {
            addDisposable(it)
        }
        /*loadServiceType()
        loadParcelSize()

        loadScanningTracking()*/
    }

    private fun loadScanningTracking() {
        repo.getScanningTracking()
            .scheduleBy(rxThreadScheduler)
            .subscribe({ it ->
                if (it == null) return@subscribe
                for (entry in it) {
                    if (!trackingListHolder.move { it.tracking == entry.tracking }) {
                        trackingListHolder.addExtraItem(entry)
                    }
                }
                onTrackingListChange(it)
            }) {
            }.also {
                addDisposable(it)
            }
    }

    private fun onTrackingListChange(it: List<PickupScanningTrackingEntity>) {
        trackingListHolder.distinctItems { it.tracking }

        _scannedTrackingList.value = it
        val d: ArrayList<Any> = arrayListOf(Title(""))
        d.addAll(setServiceAndSizeName(it))
        _data.value = d

        setFee(it)
        val fromBooking = trackingListHolder.second.count { !it.isExtra }
        val newTracking = trackingListHolder.second.size - fromBooking
        val total = taskTotalCount
        setStatusCount(it.size, newTracking, total)

        _enableSeeSummary.value = fromBooking + newTracking > 0
    }

    private fun setStatusCount(fromBooking: Int, newTracking: Int, total: Int) {
        _statusCount.value = Triple(fromBooking, newTracking, total)
    }


    private fun setServiceAndSizeName(data: List<PickupScanningTrackingEntity>): List<PickupScanningTrackingEntity> {
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

    private fun deletePickupTracking(trackingCode: String) {
        repo.deleteScanningTracking(trackingCode).scheduleBy(rxThreadScheduler)
            .subscribe({
                //getData()
            }) {
            }.also {
                addDisposable(it)
            }
    }

    private fun setFee(dataList: List<PickupScanningTrackingEntity>) {
        repo.getTaskById(taskId).subscribe({
            val (deliveryFee, codFee, cartonFeeSum, total) = calculatorRepository.calculateSummaryFee(
                it.customerCode,
                dataList
            )
            _deliveryFee.value = deliveryFee
            _codFee.value = codFee
            _cartonFee.value = cartonFeeSum
            _total.value = total
        }, {
            Timber.e(it)
        }).also {
            addDisposable(it)
        }
    }

    private fun showAlertMessage(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun viewPhoto(submitTracking: PickupScanningTrackingEntity) {
        _viewPhoto.value = submitTracking
    }

    fun savePayment(payment: String) {
        bookingPreference.payment = payment
    }

    fun showDialogConfirmDeletePickup(context: Context, trackingCode: String) {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(context.getString(R.string.delete_tracking))
        mBuilder.setMessage(context.getString(R.string.sentence_pickup_summary_are_you_sure_you_want_to_delete_this_tracking))
        mBuilder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
            deletePickupTracking(trackingCode)
        }
        mBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun setBusinessCustomer(isBusinessCustomer: Boolean) {
        _businessCustomer.value = isBusinessCustomer
    }

    fun setDone(isDone: Boolean) {
        _done.value = Event(isDone)
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

    fun showDialogPayment() {
        dataRepository.getActivePaymentMethodList().subscribe({ list ->
            val paymentMethods = list.filter { it.title != null }.map { it.id to it.title!! }
            _paymentMethods.value = Event(paymentMethods)
        }, {
            it.printStackTrace()
        }).also {
            addDisposable(it)
        }
    }
}
