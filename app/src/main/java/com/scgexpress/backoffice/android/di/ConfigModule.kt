package com.scgexpress.backoffice.android.di

import com.scgexpress.backoffice.android.config.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConfigModule {

    @Provides
    @Singleton
    internal fun provideRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig().also {
            it.init()
        }
    }
}