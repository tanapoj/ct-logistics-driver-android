package com.scgexpress.backoffice.android.ui.delivery.ofd.retention

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdRetentionActivityModule {
    @ContributesAndroidInjector(modules = [(OfdRetentionFragmentModule::class)])
    internal abstract fun bindOfdSentFragment(): OfdRetentionFragment
}