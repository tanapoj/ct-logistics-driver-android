package com.scgexpress.backoffice.android.ui.dialog

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.model.delivery.RetentionScanSubReason
import com.scgexpress.backoffice.android.repository.delivery.ContextWrapper
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject

class RetentionSelectSubReasonDialogViewModel @Inject constructor(
    application: Application,
    private val repo: DeliveryRepository
    ) : RxAndroidViewModel(application)  {

    private val context: Context
        get() = getApplication()

    var mLastClickTime: Long = 0
    var isSelected = false

    private val _subReason: MutableLiveData<List<RetentionScanSubReason>> = MutableLiveData()
    val subReason: LiveData<List<RetentionScanSubReason>>
        get() = _subReason

    private val _id: MutableLiveData<String> = MutableLiveData()
    val id: LiveData<String>
        get() = _id

    private val _reason: MutableLiveData<String> = MutableLiveData()
    val reason: LiveData<String>
        get() = _reason

    private val _itemClick: MutableLiveData<RetentionScanSubReason> = MutableLiveData()
    val itemClick: LiveData<RetentionScanSubReason>
        get() = _itemClick

    fun setData(reason:String,id:String) {
        _reason.value = reason
        _id.value = id
        setSubReason()
    }

    fun itemClick(item: RetentionScanSubReason) {
        if (checkLastClickTime())
            _itemClick.value = item
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < Const.BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    private fun setSubReason(){
        repo.getRetentionSubReason(ContextWrapper(context))
            .subscribe { list ->
                list.filter { it.id == (id.value)
                }.also {
                    _subReason.value = it
                }
            }.also {
                addDisposable(it)
            }
    }
}