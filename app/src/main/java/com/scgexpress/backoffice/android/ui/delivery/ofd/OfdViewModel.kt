package com.scgexpress.backoffice.android.ui.delivery.ofd

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.model.Manifest
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class OfdViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository
) : RxAndroidViewModel(application) {

    companion object {
        private const val from: String = "2018-09-01"
        private const val to: String = "2018-12-03"
    }

    private val context: Context
        get() = getApplication()

    private val _data: MutableLiveData<ArrayList<Manifest>> = MutableLiveData()
    val data: LiveData<ArrayList<Manifest>>
        get() = _data

    fun requestData() {
        addDisposable(repoNetwork.getManifests(from, to)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _data.value = it
            }) {
                throw OnErrorNotImplementedException(it)
            })
    }
}