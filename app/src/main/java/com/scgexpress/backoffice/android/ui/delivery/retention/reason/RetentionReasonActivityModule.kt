package com.scgexpress.backoffice.android.ui.delivery.retention.reason

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RetentionReasonActivityModule {
    @ContributesAndroidInjector(modules = [(RetentionReasonFragmentModule::class)])
    internal abstract fun bindRetentionScanFragment(): RetentionReasonFragment
}