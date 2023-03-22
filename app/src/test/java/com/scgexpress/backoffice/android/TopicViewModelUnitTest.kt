package com.scgexpress.backoffice.android

import android.app.Application
import android.content.SharedPreferences
import com.scgexpress.backoffice.android.api.TopicService
import com.scgexpress.backoffice.android.db.dao.TopicDao
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Topic
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.repository.topic.TopicRepository
import com.scgexpress.backoffice.android.ui.topic.TopicViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.hamcrest.Matchers.`is` as _is
import org.mockito.Mockito.`when` as _when


@RunWith(JUnit4::class)
class TopicViewModelUnitTest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()!!

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private lateinit var topicDao: TopicDao
    private lateinit var topicService: TopicService
    private lateinit var topicRepo: TopicRepository

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreference: UserPreference

    private lateinit var mainViewModel: TopicViewModel

    private lateinit var rxThreadScheduler: RxThreadScheduler
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val application = Mockito.mock(Application::class.java)

        topicDao = Mockito.mock(TopicDao::class.java)
        topicService = Mockito.mock(TopicService::class.java)
        topicRepo = TopicRepository(topicService, topicDao)

        sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        userPreference = UserPreference(sharedPreferences)

        rxThreadScheduler = object : RxThreadScheduler {
            val scheduler = testScheduler
            override fun computation(): Scheduler {
                return scheduler
            }

            override fun io(): Scheduler {
                return scheduler
            }

            override fun ui(): Scheduler {
                return scheduler
            }
        }

        this.mainViewModel = TopicViewModel(application, rxThreadScheduler, topicRepo, userPreference)
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun getTopics_completed() {
        // mock data
        val topics = listOf(Topic("1", "1", "qui est esse", "est rerum tempore vitae"))

        // make the github api to return mock data
        _when(topicRepo.topics)
            .thenReturn(Single.just(topics))

        mainViewModel.getTopics()

        testScheduler.triggerActions()

        // assert that the name matches
        assertThat(mainViewModel.topics.value!![0], _is(topics[0]))
    }

    @Test
    fun getTopics_noData() {
        // mock data
        val topics: List<Topic> = listOf()

        // make the github api to return mock data
        _when(topicRepo.topics)
            .thenReturn(Single.just(topics))

        mainViewModel.getTopics()

        testScheduler.triggerActions()

        // assert that the name matches
        assertThat(mainViewModel.topics.value, _is(topics))
    }

    @Test
    fun getTopics_error() {
        // mock data
        val response = Throwable("Error response")

        // make the github api to return mock data
        _when(topicRepo.topics)
            .thenReturn(Single.error(response))

        mainViewModel.getTopics()

        testScheduler.triggerActions()

        // assert that the name matches
        AssertionError()
    }

    @Test
    fun showAlertMessage_success() {
        val message = "alertMessage"
        mainViewModel.showAlertMessage(message)

        // assert that the name matches
        mainViewModel.alertMessage.value!!.getContentIfNotHandled().let {
            assertThat(it, _is(message))
        }
    }

    @Test
    fun deleteTopic_success() {
        // make the github api to return mock data
        _when(topicRepo.deleteTopic("1", "1"))
            .thenReturn(Single.just(1))

        mainViewModel.deleteTopic("1", "1")
        testScheduler.triggerActions()

        assertThat(mainViewModel.deleteTopic.value, _is(true))
    }

    @Test
    fun deleteTopic_failed() {
        // make the github api to return mock data
        _when(topicRepo.deleteTopic("22", "22"))
            .thenReturn(Single.just(0))

        mainViewModel.deleteTopic("22", "22")
        testScheduler.triggerActions()

        assertThat(mainViewModel.deleteTopic.value, _is(false))
    }

    @Test
    fun deleteTopic_error() {
        val response = Throwable("Error response")

        // make the github api to return mock data
        _when(topicRepo.deleteTopic("22", "22"))
            .thenReturn(Single.error(response))

        mainViewModel.deleteTopic("22", "22")
        testScheduler.triggerActions()

        assertThat(mainViewModel.deleteTopic.value, _is(false))
    }
}