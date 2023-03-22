package com.scgexpress.backoffice.android.ui.delivery.detail

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TrackingDetailsActivityModule {
    @ContributesAndroidInjector(modules = [(TrackingDetailsFragmentModule::class)])
    internal abstract fun bindTrackingDetailsFragment(): TrackingDetailsFragment
}