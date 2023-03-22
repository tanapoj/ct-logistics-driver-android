package com.scgexpress.backoffice.android.di

import android.app.Application
import com.scgexpress.backoffice.android.base.BaseApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ActivitiesModule::class,
        FragmentsModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ServicesModule::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: BaseApplication)
}