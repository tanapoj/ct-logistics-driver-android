package com.scgexpress.backoffice.android.api.factory

import com.google.gson.reflect.TypeToken
import com.scgexpress.backoffice.android.api.parser.ApiResult
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class WrappedResponseConverterFactory : Converter.Factory() {
    companion object {
        @JvmStatic
        fun create(): WrappedResponseConverterFactory {
            return WrappedResponseConverterFactory()
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        // if the response type is not annotated @Unwrap, delegate to next converter
        if (annotations.none { it is com.scgexpress.backoffice.android.api.annotation.WrappedResponse }) {
            return null
        }

        val wrappedType = annotations.find { it is com.scgexpress.backoffice.android.api.annotation.WrappedResponse } as com.scgexpress.backoffice.android.api.annotation.WrappedResponse

        val apiResultType = TypeToken.getParameterized(wrappedType.value.java, type).type

        val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, apiResultType, annotations)

        return Converter<ResponseBody, Any> { body ->
            (delegate.convert(body) as ApiResult<Any>).result
        }
    }
}