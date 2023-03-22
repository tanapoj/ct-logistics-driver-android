package com.scgexpress.backoffice.android.preference

import android.annotation.SuppressLint
import android.content.SharedPreferences

class UserPreference(sharedPreferences: SharedPreferences) : TypedSafePreference(sharedPreferences) {

    companion object {
        private const val PREF_USER = "user"
        private const val PREF_USER_LOGIN_TIMESTAMP = "userLoginTimestamp"
        const val USER_DEFAULT_VALUE = ""
        const val USER_LOGIN_DEFAULT_VALUE_TIMESTAMP = 0L
    }

    var user: String?
        get() = getString(PREF_USER, USER_DEFAULT_VALUE)
        @SuppressLint("ApplySharedPref")
        set(loginUser) {
            edit().putString(PREF_USER, loginUser)
                    .commit()
        }

    var activeTimestamp: Long?
        get() = getLong(PREF_USER_LOGIN_TIMESTAMP, USER_LOGIN_DEFAULT_VALUE_TIMESTAMP)
        @SuppressLint("ApplySharedPref")
        set(loginTimestamp) {
            edit().putLong(PREF_USER_LOGIN_TIMESTAMP, loginTimestamp!!)
                    .commit()
        }
}