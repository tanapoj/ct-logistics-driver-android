package com.scgexpress.backoffice.android.ui.delivery

import com.scgexpress.backoffice.android.ui.delivery.booking.NewBookingsFragment
import com.scgexpress.backoffice.android.ui.delivery.booking.NewBookingsFragmentModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.OfdManifestFragment
import com.scgexpress.backoffice.android.ui.delivery.ofd.OfdManifestFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeliveryMainActivityModule {
    @ContributesAndroidInjector(modules = [(NewBookingsFragmentModule::class)])
    internal abstract fun bindNewBookingsFragment(): NewBookingsFragment

    @ContributesAndroidInjector(modules = [(OfdManifestFragmentModule::class)])
    internal abstract fun bindOfdManifestFragment(): OfdManifestFragment
}