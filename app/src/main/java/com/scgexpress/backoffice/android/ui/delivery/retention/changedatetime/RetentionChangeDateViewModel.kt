package com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime

import android.app.Application
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject

class RetentionChangeDateViewModel@Inject constructor(
    application: Application,
    private val repo: DeliveryRepository
) : RxAndroidViewModel(application)    {
}