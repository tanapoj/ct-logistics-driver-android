package com.scgexpress.backoffice.android.api.interceptor

import android.content.Context
import android.content.Intent
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.ui.pin.PinActivity
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthorizationInterceptor(private val context: Context, private val loginPreference: LoginPreference) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val mainResponse = chain.proceed(chain.request())
        if (mainResponse.code() == 401) {
            logout()
        }
        return mainResponse
    }

    private fun logout() {
        clearLoginStatus()

        val intent = Intent(context, PinActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    private fun clearLoginStatus() {
        loginPreference.loginUser = ""
        loginPreference.token = ""
        loginPreference.identityID = ""
    }
}