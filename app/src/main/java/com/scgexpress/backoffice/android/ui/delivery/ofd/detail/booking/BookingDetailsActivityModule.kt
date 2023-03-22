package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BookingDetailsActivityModule {
    @ContributesAndroidInjector(modules = [(BookingDetailsFragmentModule::class)])
    internal abstract fun bindBookingDetailsFragment(): BookingDetailsFragment
}