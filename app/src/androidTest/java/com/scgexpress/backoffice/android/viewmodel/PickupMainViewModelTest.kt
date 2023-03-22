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
import com.scgexpress.backoffice.android.db.AppDatabase
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.dao.PickupDao
import com.scgexpress.backoffice.android.db.entity.masterdata.TblMasterParcelSizing
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.masterdata.LocalRepository
import com.scgexpress.backoffice.android.repository.pickup.IdAndValue
import com.scgexpress.backoffice.android.repository.pickup.OfflineRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainViewModel
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.hamcrest.Matchers.`is` as _is
import org.mockito.Mockito.`when` as _when


@RunWith(AndroidJUnit4::class)
class PickupMainViewModelTest {

    @Rule
    @JvmField
    var rule = AndroidInstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var mContext: Context

    private lateinit var pickupDao: PickupDao
    private lateinit var masterDao: MasterDataDao
    private lateinit var pickupService: PickupService
    private lateinit var pickupRepo: PickupRepository
    private lateinit var offlineRepo: OfflineRepository

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreference: LoginPreference
    private lateinit var loginRepo: LoginRepository
    private lateinit var loginService: LoginService

    private lateinit var mainViewModel: PickupMainViewModel

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
        val userPreference = UserPreference(sharedPreferences)
        val pickupPreference = PickupPreference(sharedPreferences)

        pickupService = NetworkTestConfig(appContext).getService(PickupService::class.java)
        loginService = NetworkTestConfig(appContext).getService(LoginService::class.java)

        val offline = OfflineRepository(rxThreadScheduler, pickupDao)
        val masterData = DataRepository(rxThreadScheduler, masterDao)
        val masterCalculator = CalculatorRepository(rxThreadScheduler, masterDao)

        pickupRepo = PickupRepository(rxThreadScheduler, pickupDao, masterDao, pickupService, pickupPreference, offline, masterData, masterCalculator)
        loginRepo = LoginRepository(loginService)

        val masterService = NetworkTestConfig(appContext).getService(MasterDataService::class.java)
        val masterDataPreference = MasterDataPreference(sharedPreferences)
        val masterLocalRepo = LocalRepository(masterDao, masterService, masterDataPreference)

        masterLocalRepo._loadMasterDataFromLocalFile(appContext.resources.openRawResource(R.raw.mdata), appContext!!.filesDir.canonicalPath)

        this.mainViewModel = PickupMainViewModel(appContext, rxThreadScheduler, loginPreference, loginRepo, pickupRepo, masterData, pickupPreference)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun get_serviceType_and_sizing() {

        val serviceTypeNonCod: List<IdAndValue> = listOf(
                2 to "Normal",
                3 to "Chilled",
                4 to "Frozen"
        )

        val serviceTypeCod: List<IdAndValue> = listOf(
                5 to "Normal",
                6 to "Chilled",
                7 to "Frozen"
        )

        val filterOnlyId = listOf(3, 4, 5, 6)
        val sizingAll = listOf(
                TblMasterParcelSizing(id = 0, name = "Undefined"),
                TblMasterParcelSizing(id = 1, name = "A"),
                TblMasterParcelSizing(id = 2, name = "B"),
                TblMasterParcelSizing(id = 3, name = "C"),
                TblMasterParcelSizing(id = 4, name = "D"),
                TblMasterParcelSizing(id = 5, name = "E"),
                TblMasterParcelSizing(id = 6, name = "F"),
                TblMasterParcelSizing(id = 7, name = "G"),
                TblMasterParcelSizing(id = 8, name = "H"),
                TblMasterParcelSizing(id = 9, name = "I"),
                TblMasterParcelSizing(id = 10, name = "J")
        )
        val sizingNormal: List<IdAndValue> = sizingAll
                .filter { it.name != "Undefined" }.map { it.id to it.name!! }
        val sizingChilledAndFrozen: List<IdAndValue> = sizingAll
                .filter { it.name != "Undefined" && it.id in filterOnlyId }.map { it.id to it.name!! }

        _when(masterDao.parcelSizingItems).thenReturn(Flowable.just(sizingAll))
//
//        //NON COD
//        mainViewModel.loadServiceType(mContext, false)
//        testScheduler.triggerActions()
//        MatcherAssert.assertThat(mainViewModel..value, _is(serviceTypeNonCod))
//
//        mainViewModel.setService(mContext, 2)
//        MatcherAssert.assertThat(mainViewModel.sizingList.value, _is(sizingNormal))
//        mainViewModel.setService(mContext, 3)
//        MatcherAssert.assertThat(mainViewModel.sizingList.value, _is(sizingChilledAndFrozen))
//        mainViewModel.setService(mContext, 4)
//        MatcherAssert.assertThat(mainViewModel.sizingList.value, _is(sizingChilledAndFrozen))
//
//        //COD
//        mainViewModel.loadServiceType(mContext, true)
//        testScheduler.triggerActions()
//        MatcherAssert.assertThat(mainViewModel.serviceTypeList.value, _is(serviceTypeCod))
//
//        mainViewModel.setService(mContext, 5)
//        MatcherAssert.assertThat(mainViewModel.sizingList.value, _is(sizingNormal))
//        mainViewModel.setService(mContext, 6)
//        MatcherAssert.assertThat(mainViewModel.sizingList.value, _is(sizingChilledAndFrozen))
//        mainViewModel.setService(mContext, 7)
//        MatcherAssert.assertThat(mainViewModel.sizingList.value, _is(sizingChilledAndFrozen))
    }


    @Test
    fun test_add_tracking() {

    }
}