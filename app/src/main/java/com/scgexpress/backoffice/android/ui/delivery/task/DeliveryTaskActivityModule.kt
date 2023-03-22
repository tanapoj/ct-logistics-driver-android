package com.scgexpress.backoffice.android.ui.delivery.task

import com.scgexpress.backoffice.android.ui.delivery.task.item.DeliveryTaskCompletedFragment
import com.scgexpress.backoffice.android.ui.delivery.task.item.DeliveryTaskCompletedFragmentModule
import com.scgexpress.backoffice.android.ui.delivery.task.item.DeliveryTaskInProgressFragment
import com.scgexpress.backoffice.android.ui.delivery.task.item.DeliveryTaskInProgressFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeliveryTaskActivityModule {
    @ContributesAndroidInjector(modules = [(DeliveryTaskInProgressFragmentModule::class)])
    internal abstract fun bindDeliveryTaskInProgressFragment(): DeliveryTaskInProgressFragment

    @ContributesAndroidInjector(modules = [(DeliveryTaskCompletedFragmentModule::class)])
    internal abstract fun bindDeliveryTaskCompletedFragment(): DeliveryTaskCompletedFragment
}