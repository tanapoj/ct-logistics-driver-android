package com.scgexpress.backoffice.android.common.time

data class DateTimeRange(
    val from: DateTime,
    val to: DateTime
) {
    fun swap() = DateTimeRange(to, from)
    fun getDiff() = from - to
}