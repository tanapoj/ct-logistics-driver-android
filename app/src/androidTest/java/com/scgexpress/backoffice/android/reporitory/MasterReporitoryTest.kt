package com.scgexpress.backoffice.android.reporitory

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scgexpress.backoffice.android.AndroidInstantTaskExecutorRule
import com.scgexpress.backoffice.android.NetworkTestConfig
import com.scgexpress.backoffice.android.api.MasterDataService
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.db.AppDatabase
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.masterdata.LocalRepository
import com.scgexpress.backoffice.android.waitForResult
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.BaseTestConsumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class MasterReporitoryTest {

    @Rule
    @JvmField
    var rule = AndroidInstantTaskExecutorRule()


    private val waitStrategy = BaseTestConsumer.TestWaitStrategy.SLEEP_1MS

    private lateinit var db: AppDatabase
    private lateinit var masterDao: MasterDataDao
    private lateinit var masterData: DataRepository
    private lateinit var masterCalculator: CalculatorRepository

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreference: UserPreference

    private lateinit var rxThreadScheduler: RxThreadScheduler
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()

        db = Room.inMemoryDatabaseBuilder(
                appContext, AppDatabase::class.java
        ).build()

        rxThreadScheduler = object : RxThreadScheduler {
            val scheduler = testScheduler
            override fun computation(): Scheduler {
                return scheduler
            }

            override fun io(): Scheduler {
                return Schedulers.io()
            }

            override fun ui(): Scheduler {
                return AndroidSchedulers.mainThread()
            }
        }

        masterDao = db.masterDataDao()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        userPreference = UserPreference(sharedPreferences)

        masterData = DataRepository(rxThreadScheduler, masterDao)
        masterCalculator = CalculatorRepository(rxThreadScheduler, masterDao)

        val masterService = NetworkTestConfig(appContext).getService(MasterDataService::class.java)
        val masterDataPreference = MasterDataPreference(sharedPreferences)
        val masterLocalRepo = LocalRepository(masterDao, masterService, masterDataPreference)

//        masterLocalRepo._loadMasterDataFromLocalFile(appContext.resources.openRawResource(R.raw.mdata), appContext!!.filesDir.canonicalPath).waitForResult()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun test() {
        Single.just("test")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .scheduleBy(rxThreadScheduler)
                .test()
                .await()
                .assertValue {
                    "test" == it
                }
    }

    @Test
    fun test_query_data() {
        val assortCode = TblScgAssortCode("10120", 2, "105-06-88", "BTT", "บางคอแหลม, สาธร, ยานาวา", "กรุงเทพฯ", "")
        masterDao.insertAllTblScgAssortCode(listOf(assortCode))

        //val data = masterDao.getScgAssortCode("10120").waitForResult()
        //MatcherAssert.assertThat(data, _is(TblScgAssortCode("10120", 2, "105-06-88", "BTT", "บางคอแหลม, สาธร, ยานาวา", "กรุงเทพฯ", "")))

        val data1 = masterData.getAssortCode("10120").waitForResult()
        MatcherAssert.assertThat(data1, _is(TblScgAssortCode("10120", 2, "105-06-88", "BTT", "บางคอแหลม, สาธร, ยานาวา", "กรุงเทพฯ", "")))


        val organization = TblOrganization(1176, "SCG Chemical (Test)", "999996")
        masterDao.insertAllTblOrganization(listOf(organization))

        val data2 = masterData.getOrganization("999996").waitForResult()
        MatcherAssert.assertThat(data2, _is(organization))
    }

    @Test
    fun test_calculator() {

        //REPO calculateFreightFare 1: branchId=4, serviceTypeId=5, sizingId=4, zipCode=10120, selectedPostalId=45, parcelCount=0
        masterDao.insertAllTblScgBranch(listOf(TblScgBranch(4, "103", regionId = 2)))
        masterDao.insertAllTblScgAssortCode(listOf(TblScgAssortCode("10120", 2)))
        masterDao.insertAllTblScgRegionDiff(listOf(TblScgRegionDiff(1, idOriginRegion = 2, idDestinationRegion = 2)))
        masterDao.insertAllTblMasterParcelProperties(listOf(TblMasterParcelProperties(22, 5, 4)))
        masterDao.insertAllTblOrganization(listOf(TblOrganization(1176, priceTierId = 202, customerCode = "999996")))
        masterDao.insertAllTblScgPricingModel(listOf(TblScgPricingModel(idRegionDiff = 1, idParcelProperties = 22, idPriceTier = 202, freightFare = "90.000")))

        val price = masterCalculator.deliveryFee("999996", 4, "10120", "4", "5").waitForResult()
        MatcherAssert.assertThat(price.freightFare, _is("90.000"))
    }
}