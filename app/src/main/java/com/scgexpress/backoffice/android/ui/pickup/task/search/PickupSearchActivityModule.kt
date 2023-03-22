package com.scgexpress.backoffice.android.ui.pickup.task.search

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupSearchActivityModule {
    @ContributesAndroidInjector(modules = [(PickupSearchFragmentModule::class)])
    internal abstract fun bindPickupSearchFragment(): PickupSearchFragment
}