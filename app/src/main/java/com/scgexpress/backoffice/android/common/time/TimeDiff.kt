package com.scgexpress.backoffice.android.common.time

data class TimeDiff(
    val diffInMillisec: Long
) {

    fun toDays() = diffInMillisec / (24 * 60 * 60 * 1000).toDouble()

}