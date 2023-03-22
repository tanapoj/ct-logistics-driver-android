package com.scgexpress.backoffice.android.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.scgexpress.backoffice.android.BuildConfig
import com.scgexpress.backoffice.android.api.*
import com.scgexpress.backoffice.android.api.factory.KeyPathResponseConverterFactory
import com.scgexpress.backoffice.android.api.factory.MockResponseAdapterFactory
import com.scgexpress.backoffice.android.api.factory.WrappedResponseConverterFactory
import com.scgexpress.backoffice.android.api.interceptor.AuthorizationInterceptor
import com.scgexpress.backoffice.android.api.interceptor.ConnectivityInterceptor
import com.scgexpress.backoffice.android.api.interceptor.HeaderInterceptor
import com.scgexpress.backoffice.android.api.interceptor.ParameterInterceptor
import com.scgexpress.backoffice.android.api.parser.GsonJsonParser
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.di.qualifier.ApplicationContext
import com.scgexpress.backoffice.android.preference.LoginPreference
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
internal class NetworkModule {

    @Provides
    @Singleton
    fun provideApiConfig(@ApplicationContext context: Context, loginPreference: LoginPreference): ApiConfig {
        return ApiConfig(
            baseUrl = Const.API_HOST_RETROFIT,
            interceptors = listOf(
                ConnectivityInterceptor(context), HeaderInterceptor(loginPreference),
                AuthorizationInterceptor(context, loginPreference)
            ),
            isMockResponseEnabled = BuildConfig.FLAVOR == Const.FLAVOR_MOCK, // default = false
            networkInterceptors = listOf(
                StethoInterceptor()
            ),
            jsonParser = GsonJsonParser()
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(config: ApiConfig): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (config.logLevel != LogLevel.NONE) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = config.logLevel
            builder.addInterceptor(httpLoggingInterceptor)
        }

        config.defaultParameters?.let {
            builder.addInterceptor(ParameterInterceptor(it))
        }

        config.defaultHeaders?.let {
            //builder.addInterceptor(HeaderInterceptor())
        }

        config.networkInterceptors?.forEach {
            builder.addNetworkInterceptor(it)
        }

        config.interceptors?.forEach {
            builder.addInterceptor(it)
        }

        builder
            .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.MILLISECONDS)

        return config.httpClient ?: builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context, config: ApiConfig,
        client: OkHttpClient
    ): Retrofit {
        val jsonParser = config.jsonParser ?: throw IllegalArgumentException("jsonParser cannot be null")

        // When app has an onError callback we need these, A global error handler,
        // if didn't have these exceptions would occur silently
        RxJavaPlugins.setErrorHandler {
            when (it) {
                is CompositeException -> {
                    if (it.exceptions.size >= 2) {
                        config.errorHandler?.invoke(it.exceptions[1])
                    } else {
                        config.errorHandler?.invoke(it.exceptions[0])
                    }
                }
            }
        }

        return Retrofit.Builder()
            .baseUrl(config.baseUrl).client(client)
            .addConverterFactory(KeyPathResponseConverterFactory.create(jsonParser))
            .addConverterFactory(WrappedResponseConverterFactory.create())
            .addConverterFactory(jsonParser.getConverterFactory())
            .addCallAdapterFactory(
                MockResponseAdapterFactory.create(
                    config.isMockResponseEnabled,
                    context,
                    jsonParser
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDeliveryService(retrofit: Retrofit): DeliveryService {
        return retrofit.create(DeliveryService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideMasterdataService(retrofit: Retrofit): MasterDataService {
        return retrofit.create(MasterDataService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): TopicService {
        return retrofit.create(TopicService::class.java)
    }

    @Provides
    @Singleton
    fun provideRxThreadScheduler() = object : RxThreadScheduler {
        override fun computation(): Scheduler {
            return Schedulers.computation()
        }

        override fun io(): Scheduler {
            return Schedulers.io()
        }

        override fun ui(): Scheduler {
            return AndroidSchedulers.mainThread()
        }
    }
}