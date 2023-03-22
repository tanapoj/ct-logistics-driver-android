package com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RetentionChangeSDActivityModule {
    @ContributesAndroidInjector(modules = [(RetentionChangeSDFragmentModule::class)])
    internal abstract fun bindRetentionChangeSDFragment(): RetentionChangeSDFragment
}