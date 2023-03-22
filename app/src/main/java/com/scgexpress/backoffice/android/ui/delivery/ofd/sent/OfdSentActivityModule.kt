package com.scgexpress.backoffice.android.ui.delivery.ofd.sent

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdSentActivityModule {
    @ContributesAndroidInjector(modules = [(OfdSentFragmentModule::class)])
    internal abstract fun bindOfdSentFragment(): OfdSentFragment
}