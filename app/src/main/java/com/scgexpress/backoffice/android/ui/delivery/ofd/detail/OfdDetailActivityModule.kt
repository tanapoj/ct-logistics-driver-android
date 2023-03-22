package com.scgexpress.backoffice.android.ui.delivery.ofd.detail

import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.OfdDetailItemsFragment
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.OfdDetailItemsFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OfdDetailActivityModule {
    @ContributesAndroidInjector(modules = [(OfdDetailItemsFragmentModule::class)])
    internal abstract fun bindOfdDetailItemsFragment(): OfdDetailItemsFragment
}