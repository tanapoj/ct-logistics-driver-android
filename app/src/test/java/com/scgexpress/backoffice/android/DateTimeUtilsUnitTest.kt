package com.scgexpress.backoffice.android

import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.toDateFormat
import com.scgexpress.backoffice.android.common.toDateTimeFormat
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is` as _is
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateTimeUtilsUnitTest {
    @Test
    fun datetime_format() {
        val iso = "2019-08-26T15:51:32"
        val ymdhis = "2019-08-26 15:51:32"
        val timestamp = 1566809492191L
        val date = Date(timestamp)

        assertThat(iso.toDateFormat(), _is("2019-08-26"))
        assertThat(ymdhis.toDateFormat(Const.DATETIME_FORMAT_YmdHis), _is("2019-08-26"))

        assertThat(iso.toDateTimeFormat(), _is("2019-08-26 15:51:32"))
        assertThat(ymdhis.toDateTimeFormat(Const.DATETIME_FORMAT_ISO8601, Const.DATETIME_FORMAT_YmdHis), _is("2019-08-26T15:51:32"))

        assertThat(timestamp.toDateTimeFormat(), _is("2019-08-26 15:51:32"))

        assertThat(date.toDateFormat(), _is("2019-08-26"))
        assertThat(date.toDateTimeFormat(), _is("2019-08-26 15:51:32"))
    }
}
