package com.scgexpress.backoffice.android.ui.pickup.bookingList

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.UserTopic
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

class PickupBookingListViewModel @Inject constructor(
    application: Application,
    private val repository: PickupRepository,
    private val userPreference: UserPreference,
    private val pickupPreference: PickupPreference
) : RxAndroidViewModel(application) {

    val context: Context = application

    val user: UserTopic
        get() = Utils.convertStringToUserTopic(userPreference.user!!)

    var taskIdsRemembered: List<String>?
        get() = pickupPreference.currentScanningTaskIdList
        set(value) {
            pickupPreference.currentScanningTaskIdList = value
        }

    var taskIds: List<String> = mutableListOf()
    var shipperCode: String? = null
    var continueNextFlag: Boolean = false

    var autoContinueFirstTask: Boolean = false

    private var _enableSubmitButton: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { postValue(false) }
    val enableSubmitButton: LiveData<Boolean>
        get() = _enableSubmitButton

    private var _bookings: MutableLiveData<List<PickupTask>> = MutableLiveData()
    val bookings: LiveData<List<PickupTask>>
        get() = _bookings

    //    private var _noBookingSelected: MutableLiveData<Boolean> = MutableLiveData()
    //    val noBookingSelected: LiveData<Boolean>
    //        get() = _noBookingSelected

    private var _selectedCount: MutableLiveData<Int> = MutableLiveData()
    val selectedCount: LiveData<Int>
        get() = _selectedCount

    private var _actionBackToTaskActivity: MutableLiveData<Boolean> = MutableLiveData()
    val actionBackToTaskActivity: LiveData<Boolean>
        get() = _actionBackToTaskActivity

    //    private var _actionContinueNextTask: MutableLiveData<Boolean> = MutableLiveData()
    //    val actionContinueNextTask: LiveData<Boolean>
    //        get() = _actionContinueNextTask

    private var _actionStartTask: MutableLiveData<Pair<String?, String?>> = MutableLiveData()
    val actionStartTask: LiveData<Pair<String?, String?>>
        get() = _actionStartTask

    private var _intentExtraIncorrect: MutableLiveData<Boolean> = MutableLiveData()
    val intentExtraIncorrect: LiveData<Boolean>
        get() = _intentExtraIncorrect

    val selectedTask = mutableMapOf<String, Boolean>()

    fun initList() {
        //if no ids have set, load from preference
        if (taskIds.isEmpty() && hasNextSelectedBookingId()) {
            loadSelectedBookingId()
        }

        when {
            continueNextFlag -> {
                autoContinueNextTask()
            }
            taskIds.isNotEmpty() -> {
                getTask(taskIds)
            }
            else -> {
                _intentExtraIncorrect.value = false
            }
        }
    }

    //trigger new task from adapter list
    fun startScanningActivityWithBlankTask() {
        //_actionStartTask.value = null to shipperCode
        shipperCode?.let { customerCode ->
            repository.createTemporaryTask(customerCode).subscribe({ taskId ->
                _actionStartTask.value = taskId to customerCode
            }, {
                Timber.e(it)
            })
        }
    }

    fun startNextTask() {
        val id = getNextSelectedBookingId()
        Timber.d("VM.startNextTask taskIds=$taskIdsRemembered nextId=$id ")
        when {
            id == null || id.isBlank() -> {
                _actionBackToTaskActivity.value = true
            }
            else -> {
                _actionStartTask.value = id to shipperCode
            }
        }
    }

    fun selectTask(taskId: String, isSelect: Boolean) {
        selectedTask[taskId] = isSelect
        checkState()
    }

    fun saveSelectedBookingId() {
        taskIdsRemembered = getSelectedBookingId()
    }

    private fun getTask(ids: List<String>) {
        Timber.d("VM.getTask ids=$ids / shipperCode=$shipperCode/ taskIds=$taskIds")
        when {
            !shipperCode.isNullOrBlank() -> repository.getTask(shipperCode!!)
            else -> repository.getTask(ids)
        }.also {
            onTaskLoaded(it)
        }
    }

    private fun onTaskLoaded(pickupTask: Flowable<List<PickupTask>>) {
        pickupTask.subscribe({ tasks ->

            val newBookingTasks = tasks.filter { it.isNewBookingStatus() }

            if (finishToTaskActivity(newBookingTasks)) return@subscribe
            if (autoContinueNextTask(newBookingTasks)) return@subscribe

            selectedTask.clear()

            //taskId = tasks.first().id
            newBookingTasks.forEach {
                val selectingIds = taskIdsRemembered ?: listOf()
                selectedTask[it.id] = when {
                    selectingIds.isNotEmpty() -> it.id in selectingIds
                    else -> true
                }
            }
            Timber.d("VM. tasks loaded=${newBookingTasks.size} selectedTask=$selectedTask taskIds=$taskIds")
            _bookings.value = newBookingTasks
            checkState()
        }, {
            it.printStackTrace()
        }).also {
            addDisposable(it)
        }
    }

    private fun autoContinueNextTask() {
        autoContinueFirstTask = true
        //pickupPreference.leftShiftScanningTaskIdList()
        taskIdsRemembered?.let {
            getTask(it)
        }
    }

    private fun finishToTaskActivity(tasks: List<PickupTask>): Boolean {
        if (tasks.isEmpty()) {
            taskIdsRemembered = null
            _actionBackToTaskActivity.value = true
            return true
        }
        return false
    }

    private fun autoContinueNextTask(tasks: List<PickupTask>): Boolean {
        if (autoContinueFirstTask) {
            startNextTask()
            return true
        }
        return false
    }

    private fun checkState() {
        Timber.d("VM selectedTask=$selectedTask")
        _enableSubmitButton.value = selectedTask.values.any { it }
        _selectedCount.value = selectedTask.count { it.value }
    }

    private fun getSelectedBookingId() = selectedTask.filterValues { it }.keys.toList()

    private fun loadSelectedBookingId() {
        taskIdsRemembered?.let {
            getTask(it)
        }
    }

    private fun getNextSelectedBookingId() = taskIdsRemembered?.firstOrNull()
    private fun hasNextSelectedBookingId() = getNextSelectedBookingId() != null
}
