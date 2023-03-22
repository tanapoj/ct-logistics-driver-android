package com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdCantSentActivityModule {
    @ContributesAndroidInjector(modules = [(OfdCantSentFragmentModule::class)])
    internal abstract fun bindOfdSentFragment(): OfdCantSentFragment
}