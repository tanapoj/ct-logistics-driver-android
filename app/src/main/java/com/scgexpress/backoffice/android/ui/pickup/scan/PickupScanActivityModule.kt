package com.scgexpress.backoffice.android.ui.pickup.scan

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupScanActivityModule {
    @ContributesAndroidInjector(modules = [(PickupScanFragmentModule::class)])
    internal abstract fun bindMainFragment(): PickupScanFragment
}