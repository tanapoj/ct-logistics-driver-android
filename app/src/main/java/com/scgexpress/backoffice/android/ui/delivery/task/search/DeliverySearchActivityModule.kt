package com.scgexpress.backoffice.android.ui.delivery.task.search

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeliverySearchActivityModule {
    @ContributesAndroidInjector(modules = [(DeliverySearchFragmentModule::class)])
    internal abstract fun bindDeliverySearchFragment(): DeliverySearchFragment
}