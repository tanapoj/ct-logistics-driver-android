package com.scgexpress.backoffice.android.ui.notification

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NotificationActivityModule {
    @ContributesAndroidInjector(modules = [(NotificationFragmentModule::class)])
    internal abstract fun bindNotificationFragment(): NotificationFragment
}