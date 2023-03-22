package com.scgexpress.backoffice.android.ui.pickup.task

import com.scgexpress.backoffice.android.ui.pickup.task.item.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickupTaskActivityModule {
    @ContributesAndroidInjector(modules = [(PickupTaskNewFragmentModule::class)])
    internal abstract fun bindPickupTaskItemsFragment(): PickupTaskNewFragment

    @ContributesAndroidInjector(modules = [(PickupTaskInProgressFragmentModule::class)])
    internal abstract fun bindPickupTaskInProgressFragment(): PickupTaskInProgressFragment

    @ContributesAndroidInjector(modules = [(PickupTaskCompletedFragmentModule::class)])
    internal abstract fun bindPickupTaskCompletedFragment(): PickupTaskCompletedFragment
}