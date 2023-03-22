package com.scgexpress.backoffice.android.di

import com.scgexpress.backoffice.android.fcm.ScgFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServicesModule {
    @ContributesAndroidInjector
    internal abstract fun bindScgFireBaseMessagingService(): ScgFirebaseMessagingService
}