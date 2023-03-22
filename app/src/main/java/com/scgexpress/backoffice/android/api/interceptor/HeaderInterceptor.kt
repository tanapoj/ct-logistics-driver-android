package com.scgexpress.backoffice.android.api.interceptor

import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor(private val loginPreference: LoginPreference) : Interceptor {

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = user.accessToken
        val request = chain.request()
        if (accessToken.isEmpty()) {
            return chain.proceed(request)
        }
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(newRequest)
    }
}