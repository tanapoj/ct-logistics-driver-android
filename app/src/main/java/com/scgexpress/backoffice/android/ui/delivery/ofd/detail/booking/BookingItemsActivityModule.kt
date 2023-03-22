package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BookingItemsActivityModule {
    @ContributesAndroidInjector(modules = [(BookingItemsFragmentModule::class)])
    internal abstract fun bindBookingItemsFragment(): BookingItemsFragment
}