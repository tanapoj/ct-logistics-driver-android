package com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RetentionChangeDateActivityModule {
    @ContributesAndroidInjector(modules = [(RetentionChangeDateFragmentModule::class)])
    internal abstract fun bindRetentionChangeSDFragment(): RetentionChangeDateFragment
}