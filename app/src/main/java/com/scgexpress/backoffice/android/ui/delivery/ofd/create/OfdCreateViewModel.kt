package com.scgexpress.backoffice.android.ui.delivery.ofd.create

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.DeliveryOfdCreate
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class OfdCreateViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val OFD_MANIFEST_CODE: String = "OFD"
    }

    private val context: Context
        get() = getApplication()

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    private val _data: MutableLiveData<String> = MutableLiveData()
    val data: LiveData<String>
        get() = _data

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    init {
        _user.value = Utils.convertStringToUser(loginPreference.loginUser!!)
    }


    fun createOfdManifest(vehicle: String) {
        addDisposable(repoNetwork.createManifest(
            arrayListOf(
                DeliveryOfdCreate(
                    OFD_MANIFEST_CODE,
                    user.value!!.branchId, user.value!!.branchCode,
                    Utils.getServerDateTimeFormat(Utils.getCurrentTimestamp()), vehicle
                )
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    if (it == "Created success") {
                        _data.value = it
                        showSnackbar("Create OFD manifest successful!")
                    } else
                        showSnackbar("Created OFD manifest failed!")
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }
}