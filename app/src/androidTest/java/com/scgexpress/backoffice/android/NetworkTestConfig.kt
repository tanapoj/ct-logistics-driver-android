package com.scgexpress.backoffice.android

import android.content.Context
import com.scgexpress.backoffice.android.api.ApiConfig
import com.scgexpress.backoffice.android.api.LogLevel
import com.scgexpress.backoffice.android.api.TopicService
import com.scgexpress.backoffice.android.api.factory.KeyPathResponseConverterFactory
import com.scgexpress.backoffice.android.api.factory.MockResponseAdapterFactory
import com.scgexpress.backoffice.android.api.factory.WrappedResponseConverterFactory
import com.scgexpress.backoffice.android.api.interceptor.ConnectivityInterceptor
import com.scgexpress.backoffice.android.api.interceptor.ParameterInterceptor
import com.scgexpress.backoffice.android.api.parser.GsonJsonParser
import com.scgexpress.backoffice.android.common.Const
import io.reactivex.exceptions.CompositeException
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


class NetworkTestConfig(private val context: Context) {

    private fun getApiConfig(): ApiConfig {
        return ApiConfig(
            baseUrl = Const.API_HOST_RETROFIT,
            interceptors = listOf(ConnectivityInterceptor(context)),
            isMockResponseEnabled = BuildConfig.FLAVOR == Const.FLAVOR_MOCK, // default = false
            jsonParser = GsonJsonParser()
        )
    }

    private fun getOkHttpClient(): OkHttpClient {
        val config = getApiConfig()
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

    private fun getRetrofit(): Retrofit {
        val config = getApiConfig()
        val client = getOkHttpClient()
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

    fun getUserService(): TopicService {
        return getRetrofit().create(TopicService::class.java)
    }

    fun <T> getService(clazz: Class<T>): T {
        return getRetrofit().create(clazz)
    }
}