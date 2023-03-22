package com.scgexpress.backoffice.android.ui.pickup.detail

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupDetailsActivityModule {
    @ContributesAndroidInjector(modules = [(PickupDetailsFragmentModule::class)])
    internal abstract fun bindPickupDetailsFragment(): PickupDetailsFragment
}