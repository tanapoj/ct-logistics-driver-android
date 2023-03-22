package com.scgexpress.backoffice.android.ui.delivery.ofd.scan

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdScanActivityModule {
    @ContributesAndroidInjector(modules = [(OfdScanFragmentModule::class)])
    internal abstract fun bindOfdScanFragment(): OfdScanFragment
}