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
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.LoginService
import com.scgexpress.backoffice.android.api.MasterDataService
import com.scgexpress.backoffice.android.api.PickupService
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.db.AppDatabase
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.dao.PickupDao
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.pickup.ResendReceipt
import com.scgexpress.backoffice.android.model.pickup.ResendReceiptResponse
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.BookingPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.masterdata.LocalRepository
import com.scgexpress.backoffice.android.repository.pickup.OfflineRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.ui.pickup.summary.PickupSummaryViewModel
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
class PickupSummaryViewModelTest {

    @Rule
    @JvmField
    var rule = AndroidInstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var mContext: Context

    private lateinit var pickupDao: PickupDao
    private lateinit var masterDao: MasterDataDao
    private lateinit var pickupService: PickupService
    private lateinit var pickupRepo: PickupRepository
    private lateinit var dataRepo: DataRepository
    private lateinit var calculatorRepo: CalculatorRepository

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreference: LoginPreference
    private lateinit var loginRepo: LoginRepository
    private lateinit var loginService: LoginService

    private lateinit var bookingPreference: BookingPreference

    private lateinit var viewModel: PickupSummaryViewModel

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

        dataRepo = DataRepository(rxThreadScheduler, masterDao)

        calculatorRepo = CalculatorRepository(rxThreadScheduler, masterDao)

        loginRepo = LoginRepository(loginService)
        loginPreference = LoginPreference(sharedPreferences)
        bookingPreference = BookingPreference(sharedPreferences)

        val masterService = NetworkTestConfig(appContext).getService(MasterDataService::class.java)
        val masterDataPreference = MasterDataPreference(sharedPreferences)
        val masterLocalRepo = LocalRepository(masterDao, masterService, masterDataPreference)

        masterLocalRepo._loadMasterDataFromLocalFile(
            appContext.resources.openRawResource(R.raw.mdata),
            appContext!!.filesDir.canonicalPath
        )

        this.viewModel = PickupSummaryViewModel(
            appContext,
            rxThreadScheduler,
            pickupRepo,
            bookingPreference,
            loginPreference,
            dataRepo,
            calculatorRepo
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun send_receipt() {

        val resendReceipt = ResendReceipt(
            Const.PARAMS_PICKUP_TASK_ACTION_RESEND,
            "1",
            "08412345678",
            "suprinya.pa@centrilliontech.co.th"
        )

        val response = ResendReceiptResponse(
            "resend", "1"
        )

        _when(pickupRepo.resend(resendReceipt)).thenReturn(Single.just(response))
    }

    @Test
    fun get_next_task() {

        val resendReceipt = ResendReceipt(
            Const.PARAMS_PICKUP_TASK_ACTION_RESEND,
            "1",
            "08412345678",
            "suprinya.pa@centrilliontech.co.th"
        )

        val response = ResendReceiptResponse(
            "resend", "1"
        )

        _when(pickupRepo.resend(resendReceipt)).thenReturn(Single.just(response))
    }
}