package com.scgexpress.backoffice.android.api.factory

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.scgexpress.backoffice.android.api.annotation.MockResponse
import com.scgexpress.backoffice.android.api.annotation.Status
import com.scgexpress.backoffice.android.api.parser.ApiResult
import com.scgexpress.backoffice.android.api.parser.JsonParser
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException
import com.scgexpress.backoffice.android.common.Utils

class MockResponseAdapterFactory(
    private val isEnabled: Boolean,
    private val context: Context,
    private val jsonParser: JsonParser
) : CallAdapter.Factory() {
    companion object {
        fun create(isEnabled: Boolean, context: Context, jsonParser: JsonParser): MockResponseAdapterFactory {
            return MockResponseAdapterFactory(isEnabled, context, jsonParser)
        }
    }

    override fun get(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = getRawType(type)

        val isObservable = rawType === Observable::class.java
        val isFlowable = rawType === Flowable::class.java
        val isSingle = rawType === Single::class.java
        val isMaybe = rawType === Maybe::class.java
        val isCompletable = rawType === Completable::class.java

        if (!isObservable && !isFlowable && !isSingle && !isMaybe && !isCompletable) {
            return null
        }

        if (annotations.none { it is MockResponse } || !isEnabled) {
            return null
        }

        val mockResponse = annotations.find { it is MockResponse } as MockResponse
        val status = mockResponse.status

        if (status == Status.SUCCESS) {
            return SuccessCallAdapter(
                type,
                annotations,
                mockResponse,
                isObservable,
                isFlowable,
                isSingle,
                isMaybe,
                isCompletable
            )
        }

        return ErrorStatusCallAdapter(type, mockResponse, isObservable, isFlowable, isSingle, isMaybe, isCompletable)
    }

    inner class SuccessCallAdapter(
        type: Type,
        annotations: Array<Annotation>,
        private val mockAnnotation: MockResponse,
        private val isObservable: Boolean,
        private val isFlowable: Boolean,
        private val isSingle: Boolean,
        private val isMaybe: Boolean,
        private val isCompletable: Boolean
    ) : CallAdapter<Any?, Any?> {
        private var apiResultType: Type? = null

        init {
            if (!isCompletable) {
                val dataType = (type as ParameterizedType).actualTypeArguments[0]
                apiResultType =
                    if (annotations.any { it is com.scgexpress.backoffice.android.api.annotation.WrappedResponse }) {
                        val wrappedType =
                            annotations.find { it is com.scgexpress.backoffice.android.api.annotation.WrappedResponse } as com.scgexpress.backoffice.android.api.annotation.WrappedResponse
                        TypeToken.getParameterized(wrappedType.value.java, dataType).type
                    } else {
                        dataType
                    }
            }
        }

        override fun adapt(call: Call<Any?>?): Any? {
            val callable = {
                if (mockAnnotation.json == -1 || isCompletable) {
                    Unit
                } else {
                    val json = Utils.toText(context, mockAnnotation.json)
                    val data = jsonParser.parse<Any>(json, apiResultType!!)
                    if (data is ApiResult<Any>) {
                        data.result
                    } else {
                        data
                    }
                }
            }
            return when {
                isObservable -> Observable.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()
                )
                isFlowable -> Flowable.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                isSingle -> Single.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                isMaybe -> Maybe.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                isCompletable -> Completable.complete().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }

        override fun responseType(): Type {
            return if (isCompletable) Unit::class.java else apiResultType!!
        }
    }

    inner class ErrorStatusCallAdapter(
        private val type: Type,
        private val mockResponse: MockResponse,
        private val isObservable: Boolean,
        private val isFlowable: Boolean,
        private val isSingle: Boolean,
        private val isMaybe: Boolean,
        private val isCompletable: Boolean
    ) : CallAdapter<Any?, Any?> {
        override fun adapt(call: Call<Any?>?): Any? {
            fun error(code: Int? = null, exception: Exception? = null): Throwable {
                if (code != null) {
                    val message = if (mockResponse.json != -1) {
                        Utils.toText(context, mockResponse.json)
                    } else {
                        ""
                    }
                    val response =
                        Response.error<String>(code, ResponseBody.create(MediaType.parse("application/json"), message))
                    return HttpException(response)
                }
                return exception!!
            }

            fun errorSource(code: Int? = null, exception: Exception? = null): Any {
                return when {
                    isObservable -> Observable.error<Throwable> {
                        error(
                            code,
                            exception
                        )
                    }.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    )
                    isFlowable -> Flowable.error<Throwable> {
                        error(
                            code,
                            exception
                        )
                    }.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    )
                    isSingle -> Single.error<Throwable> {
                        error(
                            code,
                            exception
                        )
                    }.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    )
                    isMaybe -> Maybe.error<Throwable> { error(code, exception) }.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    )
                    isCompletable -> Completable.error {
                        error(
                            code,
                            exception
                        )
                    }.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    )
                    else -> throw IllegalArgumentException("Unsupported type")
                }
            }

            return when (mockResponse.status) {
                Status.AUTHENTICATION_ERROR -> {
                    errorSource(code = 403)
                }
                Status.CLIENT_ERROR -> {
                    errorSource(code = 400)
                }
                Status.SERVER_ERROR -> {
                    errorSource(code = 500)
                }
                Status.NETWORK_ERROR -> {
                    errorSource(exception = UnknownHostException())
                }
                Status.SSL_ERROR -> {
                    errorSource(exception = SSLPeerUnverifiedException("mock ssl error"))
                }
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }

        override fun responseType(): Type {
            return type
        }
    }
}