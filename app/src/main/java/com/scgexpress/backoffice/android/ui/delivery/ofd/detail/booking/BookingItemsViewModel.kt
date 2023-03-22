package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.db.entity.masterdata.TblMasterParcelSizing
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.model.Pickup
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BookingItemsViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val IMAGE_DIRECTORY = "/scgexpress"
    }

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _bookingID: MutableLiveData<String> = MutableLiveData()
    val bookingID: LiveData<String>
        get() = _bookingID

    private val _organization: MutableLiveData<TblOrganization> = MutableLiveData()
    val organization: LiveData<TblOrganization>
        get() = _organization

    private var _deliveryFee: MutableLiveData<Double> = MutableLiveData()
    val deliveryFee: LiveData<Double>
        get() = _deliveryFee

    private var _serviceCharge: MutableLiveData<Double> = MutableLiveData()
    val serviceCharge: LiveData<Double>
        get() = _serviceCharge

    private var _codFee: MutableLiveData<Double> = MutableLiveData()
    val codFee: LiveData<Double>
        get() = _codFee

    private var _total: MutableLiveData<Double> = MutableLiveData()
    val total: LiveData<Double>
        get() = _total

    private val _sizes: MutableLiveData<List<TblMasterParcelSizing>> = MutableLiveData()
    val sizes: LiveData<List<TblMasterParcelSizing>>
        get() = _sizes

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    fun loadData() {
        addDisposable(repoNetwork.getBookingItems(bookingID.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null) return@subscribe
                val d: ArrayList<Any> = arrayListOf(BookingSummaryTitle("0"))
                d.addAll(it)
                _data.value = d
            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
            })
    }

    private fun mapSizeName(list: List<Pickup>): List<Pickup> {
        val sizesList = _sizes.value!!
        if (sizesList.isNotEmpty()) {
            for (item: Pickup in list) {
                val sizeName = _sizes.value!!.filter { i ->
                    i.id.toString() == item.sizes
                }.map { j -> j.name!! }[0]
                item.sizes = sizeName
            }
        }
        return list
    }

    private fun setFee(dataList: List<Pickup>) {
        var deliveryFee = 0.0
        var serviceCharge = 0.0
        var codFee = 0.0
        for (data: Pickup in dataList) {
            deliveryFee += data.deliveryFee
            serviceCharge += data.serviceCharge
            codFee += data.codFee
        }
        val total = deliveryFee + serviceCharge + codFee
        _deliveryFee.value = deliveryFee
        _serviceCharge.value = serviceCharge
        _codFee.value = codFee
        _total.value = total
    }

    private fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun setBookingID(bookingID: String) {
        _bookingID.value = bookingID
    }
}