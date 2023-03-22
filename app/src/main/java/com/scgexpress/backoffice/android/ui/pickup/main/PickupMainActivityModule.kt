package com.scgexpress.backoffice.android.ui.pickup.main

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupMainActivityModule {
    @ContributesAndroidInjector(modules = [(PickupMainFragmentModule::class)])
    internal abstract fun bindMainFragment(): PickupMainFragment
}