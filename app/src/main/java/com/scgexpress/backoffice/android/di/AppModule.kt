package com.scgexpress.backoffice.android.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        AppModule.Declarations::class,
        ConfigModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        PreferenceModule::class,
        ViewModelModule::class
    ]
)
class AppModule {

    @Module
    internal interface Declarations {
        @Binds
        @Singleton
        @com.scgexpress.backoffice.android.di.qualifier.ApplicationContext
        fun bindContext(application: Application): Context
    }

    @Provides
    @Singleton
    fun provideApp(@com.scgexpress.backoffice.android.di.qualifier.ApplicationContext context: Context) = context
}
