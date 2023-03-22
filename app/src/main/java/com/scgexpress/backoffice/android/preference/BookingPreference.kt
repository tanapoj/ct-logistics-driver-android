package com.scgexpress.backoffice.android.preferrence

import android.content.SharedPreferences
import com.scgexpress.backoffice.android.preference.TypedSafePreference

class BookingPreference(sharedPreferences: SharedPreferences) : TypedSafePreference(sharedPreferences) {

    companion object {
        private const val PREF_BOOKING = "booking"
        private const val PREF_PAYMENT = "payment"
        private const val BOOKING_DEFAULT_VALUE = ""
    }

    var booking: String?
        get() = getString(PREF_BOOKING, BOOKING_DEFAULT_VALUE)
        set(booking) {
            edit().putString(PREF_BOOKING, booking)
                .apply()
        }

    var payment: String?
        get() = getString(PREF_PAYMENT, BOOKING_DEFAULT_VALUE)
        set(payment) {
            edit().putString(PREF_PAYMENT, payment)
                .apply()
        }
}