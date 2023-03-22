package com.scgexpress.backoffice.android.ui.pickup.scan

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.thenPassWith
import com.scgexpress.backoffice.android.common.toSingle
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.UserTopic
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.repository.topic.TopicRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import timber.log.Timber
import javax.inject.Inject

class PickupScanViewModel @Inject constructor(
    application: Application,
    private val scheduler: RxThreadScheduler,
    private val userPreference: UserPreference,
    private var pickupRepository: PickupRepository,
    private var masterDataRepository: DataRepository
) : RxAndroidViewModel(application) {

    val context: Context = application

    val user: UserTopic
        get() = Utils.convertStringToUserTopic(userPreference.user!!)

    private val _alertMessage = MutableLiveData<Event<String>>()
    val alertMessage: LiveData<Event<String>>
        get() = _alertMessage

    private val _matchTasks = MutableLiveData<List<PickupTask>?>().apply { postValue(null) }
    val matchTasks: LiveData<List<PickupTask>?>
        get() = _matchTasks

    fun lookupCustomerCode(code: String) {
        Timber.d("VM.lookupCustomerCode($code)")
        pickupRepository.getTask(code).subscribe({ tasks ->
            Timber.d("VM.match task=$tasks")
            when{
                tasks.isNotEmpty() -> {
                    _matchTasks.value = tasks
                }
                else -> {
                    Timber.d("VM.task with customerCode=$code not found, lookup org")
                    masterDataRepository.getOrganization(code).subscribe({
                        Timber.d("VM.lookup org=$it")
                        _matchTasks.value = it?.let { listOf() }
                    }, {
                        _matchTasks.value = null
                    })
                }
            }
        }, {
            Timber.e(it)
            showAlertMessage("Error on fetch Task Data")
        }).also {
            addDisposable(it)
        }
    }

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }
}
