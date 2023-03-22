package com.scgexpress.backoffice.android.ui.delivery.retention.reason

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.model.delivery.RetentionScanReason
import com.scgexpress.backoffice.android.repository.delivery.ContextWrapper
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RetentionReasonViewModel@Inject constructor(
    application: Application,
    private val repo: DeliveryRepository
) : RxAndroidViewModel(application)  {

    private val context: Context
        get() = getApplication()

    var mLastClickTime: Long = 0

    private val _itemClick: MutableLiveData<RetentionScanReason> = MutableLiveData()
    val itemClick: LiveData<RetentionScanReason>
        get() = _itemClick

    private val _mainReason = MutableLiveData<List<RetentionScanReason>>()
    val mainReason: LiveData<List<RetentionScanReason>>
        get() = _mainReason

    fun itemClick(item: RetentionScanReason) {
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

    fun loadMainReason() {
        repo.getRetentionMainReason(ContextWrapper(context))
            .subscribe { list ->
                _mainReason.value = list
            }
            .also {
                addDisposable(it)
            }
    }

}