package com.scgexpress.backoffice.android

import com.scgexpress.backoffice.android.common.time.DateTime
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.absoluteValue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateTimeUnitTest {
    @Test
    fun test_create_datetime() {
        val d1 = DateTime.from(1573203618)
        MatcherAssert.assertThat(d1.toISO8601(), `is`("2019-11-08T16:00:18"))
        MatcherAssert.assertThat(d1.toYmd(), `is`("2019-11-08"))
        MatcherAssert.assertThat(d1.toYmdHis(), `is`("2019-11-08 16:00:18"))
        MatcherAssert.assertThat(d1.timestamp, `is`(1573203618L))
        MatcherAssert.assertThat(d1.timestampMillisec, `is`(1573203618000L))
        MatcherAssert.assertThat(d1.year, `is`(2019))
        MatcherAssert.assertThat(d1.month, `is`(11))
        MatcherAssert.assertThat(d1.date, `is`(8))

        val d2 = DateTime.fromISO8601("2019-11-08T16:00:18")
        MatcherAssert.assertThat(d2.toISO8601(), `is`("2019-11-08T16:00:18"))
        MatcherAssert.assertThat(d2.toYmd(), `is`("2019-11-08"))
        MatcherAssert.assertThat(d2.toYmdHis(), `is`("2019-11-08 16:00:18"))
        MatcherAssert.assertThat(d2.timestamp, `is`(1573203618L))
        MatcherAssert.assertThat(d2.timestampMillisec, `is`(1573203618000L))
        MatcherAssert.assertThat(d2.year, `is`(2019))
        MatcherAssert.assertThat(d2.month, `is`(11))
        MatcherAssert.assertThat(d2.date, `is`(8))

        val d3 = DateTime.fromYmdHis("2019-11-08 16:00:18")
        MatcherAssert.assertThat(d3.toISO8601(), `is`("2019-11-08T16:00:18"))
        MatcherAssert.assertThat(d3.toYmd(), `is`("2019-11-08"))
        MatcherAssert.assertThat(d3.toYmdHis(), `is`("2019-11-08 16:00:18"))
        MatcherAssert.assertThat(d3.timestamp, `is`(1573203618L))
        MatcherAssert.assertThat(d3.timestampMillisec, `is`(1573203618000L))
        MatcherAssert.assertThat(d3.year, `is`(2019))
        MatcherAssert.assertThat(d3.month, `is`(11))
        MatcherAssert.assertThat(d3.date, `is`(8))
    }

    @Test
    fun test_time_diff() {
        val d1 = DateTime.fromYmdHis("2000-01-01 00:00:00")
        val d2 = DateTime.fromYmdHis("2000-02-02 00:00:00")

        MatcherAssert.assertThat(d1.isComeBefore(d2), `is`(true))
        MatcherAssert.assertThat(d1.isComeAfter(d2), `is`(false))
        MatcherAssert.assertThat((d1 - d2).toDays(), `is`(-32.0))
        MatcherAssert.assertThat((d2 - d1).toDays(), `is`(32.0))
    }

    @Test
    fun test_time_range() {
        val d1 = DateTime.fromYmdHis("2000-01-01 00:00:00")
        val d2 = DateTime.fromYmdHis("2000-02-02 00:00:00")

        val range1 = d1..d2
        MatcherAssert.assertThat(range1.getDiff().toDays(), `is`(-32.0))
        MatcherAssert.assertThat(range1.swap().getDiff().toDays(), `is`(32.0))
        MatcherAssert.assertThat(range1.getDiff().toDays().absoluteValue, `is`(32.0))
    }
}
