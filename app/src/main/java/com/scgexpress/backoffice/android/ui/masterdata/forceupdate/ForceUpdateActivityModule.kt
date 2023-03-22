package com.scgexpress.backoffice.android.ui.masterdata.forceupdate

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ForceUpdateActivityModule {

    @ContributesAndroidInjector(modules = [(ForceUpdateFragmentModule::class)])
    internal abstract fun bindMasterDataFragment(): ForceUpdateFragment
}