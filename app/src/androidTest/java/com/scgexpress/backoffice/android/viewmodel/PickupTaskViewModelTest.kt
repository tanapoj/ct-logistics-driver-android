package com.scgexpress.backoffice.android.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scgexpress.backoffice.android.AndroidInstantTaskExecutorRule
import com.scgexpress.backoffice.android.NetworkTestConfig
import com.scgexpress.backoffice.android.api.LoginService
import com.scgexpress.backoffice.android.api.PickupService
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ACCEPTED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_REJECTED
import com.scgexpress.backoffice.android.db.AppDatabase
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.dao.NotificationDao
import com.scgexpress.backoffice.android.db.dao.PickupDao
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.pickup.PickupTaskAcception
import com.scgexpress.backoffice.android.model.pickup.PickupTaskAcceptionResponse
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.repository.pickup.OfflineRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when` as _when


@RunWith(AndroidJUnit4::class)
class PickupTaskViewModelTest {

    @Rule
    @JvmField
    var rule = AndroidInstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var mContext: Context

    private lateinit var pickupDao: PickupDao
    private lateinit var masterDao: MasterDataDao
    private lateinit var pickupService: PickupService
    private lateinit var pickupRepo: PickupRepository

    private lateinit var notiDao: NotificationDao
    private lateinit var notiRepo: NotificationLocalRepository

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreference: LoginPreference
    private lateinit var loginService: LoginService

    private lateinit var viewModel: PickupTaskViewModel

    private lateinit var rxThreadScheduler: RxThreadScheduler
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {

        val appContext = ApplicationProvider.getApplicationContext<Application>()
        mContext = appContext

        db = Room.inMemoryDatabaseBuilder(
            appContext, AppDatabase::class.java
        ).build()

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

        pickupDao = db.pickupDao()
        masterDao = db.masterDataDao()
        notiDao = db.notificationDao()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        val pickupPreference = PickupPreference(sharedPreferences)

        pickupService = NetworkTestConfig(appContext).getService(PickupService::class.java)
        loginService = NetworkTestConfig(appContext).getService(LoginService::class.java)

        val offline = OfflineRepository(rxThreadScheduler, pickupDao)
        val masterData = DataRepository(rxThreadScheduler, masterDao)
        val masterCalculator = CalculatorRepository(rxThreadScheduler, masterDao)

        pickupRepo = PickupRepository(
            rxThreadScheduler,
            pickupDao,
            masterDao,
            pickupService,
            pickupPreference,
            offline,
            masterData,
            masterCalculator
        )
        notiRepo = NotificationLocalRepository(notiDao)


        this.viewModel = PickupTaskViewModel(
            appContext,
            rxThreadScheduler,
            pickupRepo,
            notiRepo,
            loginPreference
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun search_tasks() {

    }

    @Test
    fun accept_task() {

        val id = "1"
        val rejectTask = PickupTaskAcception(PARAMS_PICKUP_TASK_ACCEPTED)

        val response = PickupTaskAcceptionResponse(1, "ACCEPTED")

        _when(pickupRepo.acceptTask(id, rejectTask)).thenReturn(Single.just(response))
    }

    @Test
    fun reject_task() {

        val id = "1"
        val rejectTask = PickupTaskAcception(
            PARAMS_PICKUP_TASK_REJECTED,
            8,
            ""
        )

        val response = PickupTaskAcceptionResponse(1, "REJECTED")

        _when(pickupRepo.acceptTask(id, rejectTask)).thenReturn(Single.just(response))
    }
}