package com.scgexpress.backoffice.android.api.annotation

import androidx.annotation.RawRes

enum class Status {
    SUCCESS, AUTHENTICATION_ERROR, CLIENT_ERROR, SERVER_ERROR, NETWORK_ERROR, SSL_ERROR
}

annotation class MockResponse(@RawRes val json: Int = -1, val status: Status = Status.SUCCESS)