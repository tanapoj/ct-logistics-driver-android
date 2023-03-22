package com.scgexpress.backoffice.android.ui.pickup.bookingList

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupBookingListActivityModule {
    @ContributesAndroidInjector(modules = [(PickupBookingListFragmentModule::class)])
    internal abstract fun bindMainFragment(): PickupBookingListFragment
}