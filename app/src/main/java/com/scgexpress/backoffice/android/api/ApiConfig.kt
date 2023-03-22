package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.api.parser.JsonParser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

typealias LogLevel = HttpLoggingInterceptor.Level

data class ApiConfig(
    var baseUrl: String = "",
    var connectTimeout: Long = TimeUnit.MINUTES.toMillis(1),
    var readTimeout: Long = TimeUnit.MINUTES.toMillis(1),
    var writeTimeout: Long = TimeUnit.MINUTES.toMillis(1),
    var defaultHeaders: Map<String, String>? = null,
    var defaultParameters: Map<String, String>? = null,
    var logLevel: LogLevel = LogLevel.NONE,
    var networkInterceptors: List<Interceptor>? = null,
    var interceptors: List<Interceptor>? = null,
    var httpClient: OkHttpClient? = null,
    var isMockResponseEnabled: Boolean = false,
    var errorHandler: ((Throwable) -> Unit)? = null,
    var jsonParser: JsonParser? = null
)
