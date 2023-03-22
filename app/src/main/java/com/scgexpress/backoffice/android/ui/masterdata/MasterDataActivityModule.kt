package com.scgexpress.backoffice.android.ui.masterdata

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MasterDataActivityModule {

    @ContributesAndroidInjector(modules = [(MasterDataFragmentModule::class)])
    internal abstract fun bindMasterDataFragment(): MasterDataFragment
}