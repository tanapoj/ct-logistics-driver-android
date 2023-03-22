package com.scgexpress.backoffice.android.preferrence

import android.content.SharedPreferences
import com.scgexpress.backoffice.android.preference.TypedSafePreference

class DeliveryPreference(sharedPreferences: SharedPreferences) :
    TypedSafePreference(sharedPreferences) {

    companion object {
        private const val PREF_OFD_CODE = "ofd_code"
    }

    var ofdCode: String?
        get() = getString(PREF_OFD_CODE, null)
        set(value) {
            edit().apply {
                putString(PREF_OFD_CODE, value)
            }.run {
                apply()
            }
        }

    fun clearOfdCode() {
        ofdCode = null
    }
}