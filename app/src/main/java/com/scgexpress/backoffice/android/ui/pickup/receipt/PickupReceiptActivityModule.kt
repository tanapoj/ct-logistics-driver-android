package com.scgexpress.backoffice.android.ui.pickup.receipt

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupReceiptActivityModule {
    @ContributesAndroidInjector(modules = [(PickupReceiptFragmentModule::class)])
    internal abstract fun bindPickupReceiptFragment(): PickupReceiptFragment
}