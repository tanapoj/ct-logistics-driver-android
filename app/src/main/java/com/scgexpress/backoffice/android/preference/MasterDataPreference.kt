package com.scgexpress.backoffice.android.preferrence

import android.content.SharedPreferences
import android.util.Log
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.preference.TypedSafePreference
import timber.log.Timber

class MasterDataPreference(sharedPreferences: SharedPreferences) : TypedSafePreference(sharedPreferences) {

    companion object {
        private const val PREF_SYNC_MASTERDATA_VERSION = "masterdata_version"
        private const val PREF_SYNC_MASTERDATA_VERSION_FILENAME = "masterdata_version_filename"
        private const val PREF_SYNC_MASTERDATA_NOTICE_TIME = "masterdata_notice_time"
        private const val PREF_SYNC_MASTERDATA_HASH = "masterdata_hash"
        const val POSTPONE_NOW = 0
        const val POSTPONE_SHORT = 15 * 60
        const val POSTPONE_LONG = 30 * 60
    }

    var lastestVersionFilename: String
        get() = getString(PREF_SYNC_MASTERDATA_VERSION_FILENAME, null) ?: ""
        set(version) {
            edit().putString(PREF_SYNC_MASTERDATA_VERSION_FILENAME, version).apply()
        }

    var lastestVersion: Long
        get() = getLong(PREF_SYNC_MASTERDATA_VERSION, 0)
        set(version) {
            edit().putLong(PREF_SYNC_MASTERDATA_VERSION, version).apply()
        }

    var hash: String
        get() = getString(PREF_SYNC_MASTERDATA_HASH, "") ?: ""
        set(version) {
            edit().putString(PREF_SYNC_MASTERDATA_HASH, version).apply()
        }

    var noticeTime: Long
        get() = getLong(PREF_SYNC_MASTERDATA_NOTICE_TIME, 0)!!
        set(status) {
            edit().putLong(PREF_SYNC_MASTERDATA_NOTICE_TIME, status).apply()
        }

    private fun getMasterDataTimeout() = 12 * 60 * 60 * 1000//expire in 12 hours
    //86400 * 1000

    fun isExpire(): Boolean {
        val current = Utils.getCurrentTimestamp()
        val timeout = getMasterDataTimeout()
        Timber.d("-masterdata check expire current=${current} > ${lastestVersion + timeout} is ${current > lastestVersion + timeout}")
        return current > lastestVersion + timeout
    }

    fun setNotice(postponeMinute: Int) {
        val postponeTo = postponeMinute * 1000
        lastestVersion = 0
        noticeTime = Utils.getCurrentTimestamp() + postponeTo
    }

    fun hasNotice(): Boolean {
        val current = Utils.getCurrentTimestamp()
        val noticeTime = noticeTime
        return noticeTime in 1..current
    }

    fun expired() {
        lastestVersion = 0
        setNotice(POSTPONE_NOW)
        Log.i("-master", "MasterDataPreference:: lastestVersion=${lastestVersion} noticeTime=${noticeTime}")
    }
}