package com.scgexpress.backoffice.android.ui.pickup.scan

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.Topic
import com.scgexpress.backoffice.android.model.UserTopic
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.repository.topic.TopicRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject

class PickupScanViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repository: TopicRepository,
    private val userPreference: UserPreference
) : RxAndroidViewModel(application) {

    val context: Context = application

    val user: UserTopic
        get() = Utils.convertStringToUserTopic(userPreference.user!!)

    private val _alertMessage = MutableLiveData<Event<String>>()
    val alertMessage: LiveData<Event<String>>
        get() = _alertMessage

    fun lookupCustomerCode(code: String) {
    }

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }
}
