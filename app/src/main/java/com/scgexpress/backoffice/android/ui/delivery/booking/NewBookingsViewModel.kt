package com.scgexpress.backoffice.android.ui.delivery.booking

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewBookingsViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository
) : RxAndroidViewModel(application) {

    companion object {
        private const val from: String = "2018-09-15"
        private const val to: String = "2018-10-08"
    }

    private val context: Context
        get() = getApplication()

    private val _data: MutableLiveData<ArrayList<BookingInfo>> = MutableLiveData()
    val data: LiveData<ArrayList<BookingInfo>>
        get() = _data

    fun requestData() {
        addDisposable(repoNetwork.getBookings(from, Utils.getServerDateFormat(Utils.getCurrentTimestamp()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _data.value = it
            }) {
                throw OnErrorNotImplementedException(it)
            })
    }

    fun setBookingInfo(it: BookingInfo) {

    }

}