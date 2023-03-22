package com.scgexpress.backoffice.android.db

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scgexpress.backoffice.android.db.dao.TopicDao
import com.scgexpress.backoffice.android.db.entity.TopicEntity
import com.scgexpress.backoffice.android.ui.topic.TopicViewModel
import io.reactivex.observers.BaseTestConsumer
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class TopicTest {

    companion object {
        private const val AWAIT_COUNT_AT_LEAST = 1
        private const val AWAIT_COUNT_TIMEOUT: Long = 5000
    }

    private val waitStrategy = BaseTestConsumer.TestWaitStrategy.SLEEP_1MS

    private lateinit var db: AppDatabase
    private lateinit var topicDao: TopicDao

    private lateinit var mainViewModel: TopicViewModel



    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()

        db = Room.inMemoryDatabaseBuilder(
            appContext, AppDatabase::class.java
        ).build()

        topicDao = db.topicDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertedTopics_completed() {
        val topics = listOf(
            TopicEntity("1", "1", "hello", "hello world"),
            TopicEntity("2", "2", "hello", "hello world")
        )
        val longList = topicDao.insertOrReplace(topics)

        MatcherAssert.assertThat(longList.size, _is(topics.size))
    }

    @Test
    fun testInsertedTopics_empty() {
        val empty: List<TopicEntity> = listOf()
        val longList = topicDao.insertOrReplace(empty)

        MatcherAssert.assertThat(longList.size, _is(empty.size))
    }

    @Test
    fun testInsertedAndRetrieved_completed() {
        val topics = listOf(TopicEntity("1", "1", "hello", "hello world"))
        topicDao.insertOrReplace(topics)

        topicDao.all.test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(topics)
    }

    @Test
    fun testInsertedAndRetrieved_empty() {
        val topics = listOf(TopicEntity("1", "1", "hello", "hello world"))
        val empty: List<TopicEntity> = listOf()

        topicDao.insertOrReplace(topics)

        topicDao.getBy("2", "2")
            .test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(empty)
    }

    @Test
    fun testRetrievedLastRow_completed() {
        val topics = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("2", "2", "sunt aut facere", "quia et suscipit"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure")
        )

        topicDao.insertOrReplace(topics)

        topicDao.lastRow
            .test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(topics[topics.lastIndex])
    }

    @Test
    fun testRetrievedLastRowInsertTwice_completed() {
        val topics = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("2", "2", "sunt aut facere", "quia et suscipit"),
            TopicEntity("4", "4", "ea molestias quasi", "et iusto sed quo iure")
        )
        val topics2 = listOf(
            TopicEntity("2", "2", "sunt aut facere", "quia et suscipit"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure")
        )

        topicDao.insertOrReplace(topics)
        topicDao.insertOrReplace(topics2)

        topicDao.lastRow
            .test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(topics[topics.lastIndex])
    }

    @Test
    fun testRetrievedLastRowInsertTwice_noValue() {
        topicDao.lastRow
            .test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertNoValues()
    }

    @Test
    fun testInsertsReplaceConflicting() {
        val topics = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("2", "2", "sunt aut facere", "quia et suscipit"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure")
        )

        val topics2 = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("2", "2", "quia et suscipit", "quia et suscipit"),
            TopicEntity("4", "3", "qui est esse", "et iusto sed quo iure")
        )
        topicDao.insertOrReplace(topics)
        topicDao.insertOrReplace(topics2)

        val expectedUsers = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("2", "2", "quia et suscipit", "quia et suscipit"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure"),
            TopicEntity("4", "3", "qui est esse", "et iusto sed quo iure")
        )

        topicDao.all.test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(expectedUsers)
    }

    @Test
    fun testOrderedBy() {
        val topics = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure"),
            TopicEntity("2", "2", "sunt aut facere", "quia et suscipit")

        )
        topicDao.insertOrReplace(topics)

        val expectedUsers = topics.sortedBy { it.id }
        topicDao.all.test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(expectedUsers)
    }


    @Test
    fun testClearData_completed() {
        val topics = listOf(TopicEntity("1", "1", "hello", "hello world"))

        topicDao.insertOrReplace(topics)
        topicDao.delete(topics).test().assertValue(1)
    }

    @Test
    fun testClearData_empty() {
        val topicsInsert = listOf(TopicEntity("1", "1", "hello", "hello world"))
        val topicsDelete = listOf(TopicEntity("2", "2", "hello", "hello world"))

        topicDao.insertOrReplace(topicsInsert)
        topicDao.delete(topicsDelete).test().assertValue(0)
    }

    @Test
    fun testClearDataAnRetrievedAll_completed() {
        val topics = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure")
        )
        val topicsDelete = TopicEntity("2", "2", "hello", "hello world")

        val topicsInsert: ArrayList<TopicEntity> = arrayListOf()
        topicsInsert.addAll(topics)
        topicsInsert.add(topicsDelete)

        topicDao.insertOrReplace(topicsInsert)

        topicDao.delete(topicsDelete).test().awaitCount(
            AWAIT_COUNT_AT_LEAST, waitStrategy,
            AWAIT_COUNT_TIMEOUT
        ).assertValue(1)

        topicDao.all.test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(topics)
    }

    @Test
    fun testClearDataAnRetrievedAll_noValue() {
        val topics = listOf(
            TopicEntity("1", "1", "qui est esse", "est rerum tempore vitae"),
            TopicEntity("2", "2", "hello", "hello world"),
            TopicEntity("3", "3", "ea molestias quasi", "et iusto sed quo iure")
        )

        topicDao.insertOrReplace(topics)

        topicDao.delete(topics).test().awaitCount(
            AWAIT_COUNT_AT_LEAST, waitStrategy,
            AWAIT_COUNT_TIMEOUT
        ).assertValue(topics.size)

        topicDao.all.test()
            .awaitCount(
                AWAIT_COUNT_AT_LEAST, waitStrategy,
                AWAIT_COUNT_TIMEOUT
            )
            .assertValue(listOf())
    }
}