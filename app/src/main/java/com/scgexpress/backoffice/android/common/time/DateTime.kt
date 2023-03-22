package com.scgexpress.backoffice.android.common.time

import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.toDateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

data class DateTime(
    private val datetime: Date
) {

    companion object {
        fun now() = from(Utils.getCurrentTimestamp())

        fun from(timestamp: Long) = DateTime(Date(timestamp * 1000))

        fun from(format: String, inputFormat: String) =
            DateTime(SimpleDateFormat(inputFormat, Locale.getDefault()).parse(format))

        fun fromISO8601(format: String) = from(format, Const.DATETIME_FORMAT_ISO8601)
        fun fromYmd(format: String) = from(format, Const.DATETIME_FORMAT_Ymd)
        fun fromYmdHis(format: String) = from(format, Const.DATETIME_FORMAT_YmdHis)
    }

    private val calendar: Calendar by lazy {
        val calendar = Calendar.getInstance()!!
        calendar.time = datetime
        calendar
    }

    val date: Int by lazy {
        calendar.get(Calendar.DATE)
    }

    val month: Int by lazy {
        calendar.get(Calendar.MONTH) + 1
    }

    val year: Int by lazy {
        calendar.get(Calendar.YEAR)
    }

    fun toISO8601() = datetime.toDateTimeFormat(Const.DATETIME_FORMAT_ISO8601)
    fun toYmdHis() = datetime.toDateTimeFormat(Const.DATETIME_FORMAT_YmdHis)
    fun toYmd() = datetime.toDateTimeFormat(Const.DATETIME_FORMAT_Ymd)

    fun isComeBefore(that: DateTime) = datetime.time - that.datetime.time < 0
    fun isComeAfter(that: DateTime) = datetime.time - that.datetime.time > 0

    val timestamp: Long
        get() = timestampMillisec / 1000

    val timestampMillisec: Long
        get() = datetime.time

    infix operator fun minus(that: DateTime) =
        TimeDiff(datetime.time - that.datetime.time)

    infix operator fun rangeTo(that: DateTime) = DateTimeRange(this, that)
}