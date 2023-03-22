package com.scgexpress.backoffice.android.ui.menu

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MenuActivityModule {
    @ContributesAndroidInjector(modules = [(MenuFragmentModule::class)])
    internal abstract fun bindMenuFragment(): MenuFragment
}