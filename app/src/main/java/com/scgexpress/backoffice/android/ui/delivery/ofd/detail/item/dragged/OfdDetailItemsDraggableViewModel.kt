package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.TrackingPositionLocalRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class OfdDetailItemsDraggableViewModel @Inject constructor(
    application: Application,
    private val repoLocal: TrackingPositionLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<DeliveryOfdParcelList> = MutableLiveData()
    val data: LiveData<DeliveryOfdParcelList>
        get() = _data

    private var _manifestID: MutableLiveData<String> = MutableLiveData()
    val manifestID: LiveData<String>
        get() = _manifestID


    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    fun loadTrackingPosition() {
        addDisposable(repoLocal.getTrackingPosition(user.id, _manifestID.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                } else {
                }
            }) {
                if (it is NoConnectivityException) {
                }
            })
    }

    fun updateTrackingPosition(items: List<Any>) {
        val itemList: ArrayList<OfdItemPosition> = arrayListOf()
        for ((i, item: Any) in items.withIndex()) {
            if (item is TrackingInfo) {
                itemList.add(OfdItemPosition(user.id, manifestID.value!!, item.trackingNumber, i))
            } else if (item is BookingInfo) {
                itemList.add(OfdItemPosition(user.id, manifestID.value!!, item.bookingID, i))
            }
        }
        repoLocal.updateTrackingPosition(itemList)

        /*addDisposable(Single.fromCallable {
            repoLocal.updateTrackingPosition(itemList)
        }.subscribeOn(Schedulers.io())
                .delay(3000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnSubscribe {
                }
                .doOnSuccess {
                    Timber.d("Updated")
                }
                .subscribe())*/
    }

    fun setManifestID(manifestID: String) {
        _manifestID.value = manifestID
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }
}