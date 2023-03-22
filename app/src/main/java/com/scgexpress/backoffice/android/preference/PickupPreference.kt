package com.scgexpress.backoffice.android.preferrence

import android.content.SharedPreferences
import com.scgexpress.backoffice.android.preference.TypedSafePreference

class PickupPreference(sharedPreferences: SharedPreferences) :
        TypedSafePreference(sharedPreferences) {

    companion object {
        private const val PREF_SCANNING_TASKID_LIST = "current_scanning_task_id_list"
        private const val PREF_PICKUP_TASK_RUNNING_NUMBER = "pickupReceiptRunningNumber"
    }

    var currentScanningTaskIdList: List<String>?
        get() = getString(PREF_SCANNING_TASKID_LIST, null)?.split(",")
        set(value) {
            edit().apply {
                putString(PREF_SCANNING_TASKID_LIST, value?.joinToString(","))
            }.run {
                apply()
            }
        }

    fun leftShiftScanningTaskIdList(): String? {
        val ids = currentScanningTaskIdList?.toMutableList() ?: return null
        if (ids.isEmpty()) return null
        val id = ids.removeAt(0)
        currentScanningTaskIdList = ids
        return id
    }

    fun resetRunningNumber() {
        edit().apply {
            putInt(PREF_PICKUP_TASK_RUNNING_NUMBER, 0)
        }.run {
            apply()
        }
    }

    fun nextRunningNumber(): Int {
        val current = getInt(PREF_PICKUP_TASK_RUNNING_NUMBER, 0)
        val next = current + 1

        edit().apply {
            putInt(PREF_PICKUP_TASK_RUNNING_NUMBER, next)
        }.run {
            apply()
        }

        return next
    }
}