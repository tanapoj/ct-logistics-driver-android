package com.scgexpress.backoffice.android.ui.pickup.summary

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupSummaryActivityModule {
    @ContributesAndroidInjector(modules = [(PickupSummaryFragmentModule::class)])
    internal abstract fun bindPickupSummaryFragment(): PickupSummaryFragment
}