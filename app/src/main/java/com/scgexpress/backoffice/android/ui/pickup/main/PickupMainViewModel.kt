package com.scgexpress.backoffice.android.ui.pickup.main

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

class PickupMainViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repository: TopicRepository,
    private val userPreference: UserPreference
) : RxAndroidViewModel(application) {

    val context: Context = application

    val user: UserTopic
        get() = Utils.convertStringToUserTopic(userPreference.user!!)

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _topic: MutableLiveData<Topic> = MutableLiveData()
    val topic: LiveData<Topic>
        get() = _topic

    private var _topics: MutableLiveData<List<Topic>> = MutableLiveData()
    val topics: LiveData<List<Topic>>
        get() = _topics

    private var _topicsLocal: MutableLiveData<List<Topic>> = MutableLiveData()
    val topicsLocal: LiveData<List<Topic>>
        get() = _topicsLocal

    private var _deleteTopic: MutableLiveData<Boolean> = MutableLiveData()
    val deleteTopic: LiveData<Boolean>
        get() = _deleteTopic

    private val _alertMessage = MutableLiveData<Event<String>>()
    val alertMessage: LiveData<Event<String>>
        get() = _alertMessage

    fun getTopics() {
        repository.topics
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                _topics.value = it

                val d: ArrayList<Any> = arrayListOf(Title(context.getString(R.string.app_name)))
                d.addAll(it)
                _data.value = d
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    fun getTopicsLocal() {
        repository.topicsLocal
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it != null)
                    _topicsLocal.value = it
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    fun getTopic(id: String) {
        repository.getTopic(id)
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                _topic.value = it
            }) {
                if (it is NoConnectivityException)
                    showAlertMessage(context.getString(R.string.there_is_on_internet_connection))
            }.also {
                addDisposable(it)
            }
    }

    fun saveTopic(item: Topic) {
        repository.saveTopic(item)
    }

    fun saveTopics(items: List<Topic>) {
        repository.saveTopic(items)
    }

    fun deleteTopic(id: String, userId: String) {
        repository.deleteTopic(id, userId).subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                _deleteTopic.value = it > 0

            }) {
                _deleteTopic.value = false
            }.also {
                addDisposable(it)
            }
    }

    fun showAlertMessage(msg: String) {
        _alertMessage.value = Event(msg)
    }
}
