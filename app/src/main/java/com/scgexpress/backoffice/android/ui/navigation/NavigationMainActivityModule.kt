package com.scgexpress.backoffice.android.ui.navigation

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NavigationMainActivityModule {
    @ContributesAndroidInjector(modules = [(NavigationMainFragmentModule::class)])
    internal abstract fun bindNavigationMainFragment(): NavigationMainFragment
}