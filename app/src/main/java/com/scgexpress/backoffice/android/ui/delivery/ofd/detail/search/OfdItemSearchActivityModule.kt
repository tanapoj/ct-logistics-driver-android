package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdItemSearchActivityModule {
    @ContributesAndroidInjector(modules = [(OfdItemSearchFragmentModule::class)])
    internal abstract fun bindOfdItemSearchFragment(): OfdItemSearchFragment
}