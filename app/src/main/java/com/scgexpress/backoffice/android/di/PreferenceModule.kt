package com.scgexpress.backoffice.android.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.scgexpress.backoffice.android.di.qualifier.ApplicationContext
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.preferrence.BookingPreference
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferenceModule {
    companion object {
        private const val PREFIX = com.scgexpress.backoffice.android.BuildConfig.APPLICATION_ID + ".pref."
        private const val BOOKING = PREFIX + "booking"
        private const val LOGIN = PREFIX + "login"
        private const val MASTERDATA = PREFIX + "masterdata"
        private const val USER = PREFIX + "user"
    }

    @Provides
    @Singleton
    @com.scgexpress.backoffice.android.di.qualifier.PreferenceDefault
    internal fun provideDefaultPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    internal fun provideBookingPreference(@ApplicationContext context: Context): BookingPreference {
        return BookingPreference(context.getSharedPreferences(BOOKING, Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    internal fun provideLoginPreference(@ApplicationContext context: Context): LoginPreference {
        return LoginPreference(context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    internal fun provideMasterDataPreference(@ApplicationContext context: Context): MasterDataPreference {
        return MasterDataPreference(context.getSharedPreferences(MASTERDATA, Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    internal fun provideUserPreference(@com.scgexpress.backoffice.android.di.qualifier.ApplicationContext context: Context): UserPreference {
        return UserPreference(context.getSharedPreferences(USER, Context.MODE_PRIVATE))
    }
}