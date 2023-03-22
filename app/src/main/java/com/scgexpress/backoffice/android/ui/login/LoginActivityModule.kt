package com.scgexpress.backoffice.android.ui.login

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LoginActivityModule {
    @ContributesAndroidInjector(modules = [(LoginFragmentModule::class)])
    internal abstract fun bindLoginFragment(): LoginFragment
}