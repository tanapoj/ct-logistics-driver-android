package com.scgexpress.backoffice.android.ui.delivery.ofd.create

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdCreateActivityModule {
    @ContributesAndroidInjector(modules = [(OfdCreateFragmentModule::class)])
    internal abstract fun bindCreateOfdManifestFragment(): OfdCreateFragment
}