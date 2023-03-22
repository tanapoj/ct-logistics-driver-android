package com.scgexpress.backoffice.android.preference

import android.annotation.SuppressLint
import android.content.SharedPreferences

class LoginPreference(sharedPreferences: SharedPreferences) : TypedSafePreference(sharedPreferences) {

    companion object {
        private const val PREF_LOGIN_USER = "login_user"
        private const val PREF_USER_AWS_IDENTITY_ID = "user_identity_id"
        private const val PREF_USER_AWS_TOKEN = "user_token"
        private const val PREF_LOGIN_TIMESTAMP = "login_timestamp"
        const val LOGIN_DEFAULT_VALUE = ""
        const val LOGIN_DEFAULT_VALUE_TIMESTAMP = 0L
    }

    var loginUser: String?
        get() = getString(PREF_LOGIN_USER, LOGIN_DEFAULT_VALUE)
        @SuppressLint("ApplySharedPref")
        set(loginUser) {
            edit().putString(PREF_LOGIN_USER, loginUser)
                    .commit()
        }

    var identityID: String?
        get() = getString(PREF_USER_AWS_IDENTITY_ID, LOGIN_DEFAULT_VALUE)
        @SuppressLint("ApplySharedPref")
        set(identityID) {
            edit().putString(PREF_USER_AWS_IDENTITY_ID, identityID)
                    .commit()
        }

    var token: String?
        get() = getString(PREF_USER_AWS_TOKEN, LOGIN_DEFAULT_VALUE)
        @SuppressLint("ApplySharedPref")
        set(token) {
            edit().putString(PREF_USER_AWS_TOKEN, token)
                    .commit()
        }

    var activeTimestamp: Long?
        get() = getLong(PREF_LOGIN_TIMESTAMP, LOGIN_DEFAULT_VALUE_TIMESTAMP)
        @SuppressLint("ApplySharedPref")
        set(loginTimestamp) {
            edit().putLong(PREF_LOGIN_TIMESTAMP, loginTimestamp!!)
                    .commit()
        }
}