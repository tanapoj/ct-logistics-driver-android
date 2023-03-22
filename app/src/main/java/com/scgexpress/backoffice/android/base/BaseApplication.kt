package com.scgexpress.backoffice.android.base

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context
import android.os.Build
import com.amazonaws.mobile.client.AWSMobileClient
import com.facebook.stetho.Stetho
import com.scgexpress.backoffice.android.di.AppComponent
import dagger.android.*
import timber.log.Timber
import javax.inject.Inject

class BaseApplication : Application(), HasActivityInjector, HasFragmentInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun fragmentInjector(): AndroidInjector<Fragment> = dispatchingFragmentInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector

    private val component: AppComponent by lazy {
        com.scgexpress.backoffice.android.di.DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    companion object {
        fun get(context: Context): BaseApplication {
            return context.applicationContext as BaseApplication
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (com.scgexpress.backoffice.android.BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        component.inject(this)

        initAws()
        initStetho()
    }

    private fun initAws() {
        AWSMobileClient.getInstance().initialize(this).execute()
    }

    private fun initStetho() {
        if (!isRoboUnitTest()) {
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun isRoboUnitTest(): Boolean {
        return "robolectric" == Build.FINGERPRINT
    }
}