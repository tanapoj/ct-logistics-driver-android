package com.scgexpress.backoffice.android.ui.pin

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PinActivityModule {
    @ContributesAndroidInjector(modules = [(PinFragmentModule::class)])
    internal abstract fun bindPinFragment(): PinFragment
}