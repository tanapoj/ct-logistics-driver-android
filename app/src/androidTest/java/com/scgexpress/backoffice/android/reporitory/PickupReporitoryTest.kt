package com.scgexpress.backoffice.android.reporitory

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scgexpress.backoffice.android.*
import com.scgexpress.backoffice.android.api.MasterDataService
import com.scgexpress.backoffice.android.api.PickupService
import com.scgexpress.backoffice.android.api.TopicService
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.scheduleBy
import com.scgexpress.backoffice.android.db.AppDatabase
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.dao.PickupDao
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.scgexpress.backoffice.android.db.entity.pickup.PickupPendingReceiptEntity
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.model.Location
import com.scgexpress.backoffice.android.preference.UserPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.repository.masterdata.CalculatorRepository
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.repository.masterdata.LocalRepository
import com.scgexpress.backoffice.android.repository.pickup.ContextWrapper
import com.scgexpress.backoffice.android.repository.pickup.OfflineRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.BaseTestConsumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import org.hamcrest.Matchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class PickupReporitoryTest {

    companion object {
        private const val AWAIT_COUNT_AT_LEAST = 1
        private const val AWAIT_COUNT_TIMEOUT: Long = 5000
    }

    @Rule
    @JvmField
    var rule = AndroidInstantTaskExecutorRule()

    @Inject
    lateinit var topicService: TopicService


    private val waitStrategy = BaseTestConsumer.TestWaitStrategy.SLEEP_1MS

    private lateinit var db: AppDatabase
    private lateinit var appContext: Application
    private lateinit var pickupDao: PickupDao
    private lateinit var masterDao: MasterDataDao
    private lateinit var service: PickupService
    private lateinit var repo: PickupRepository
    private lateinit var offline: OfflineRepository
    private lateinit var masterData: DataRepository
    private lateinit var masterCalculator: CalculatorRepository

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreference: UserPreference
    private lateinit var pickupPreferences: PickupPreference

    private lateinit var rxThreadScheduler: RxThreadScheduler
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext<Application>()

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

        pickupDao = db.pickupDao()
        masterDao = db.masterDataDao()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        userPreference = UserPreference(sharedPreferences)
        pickupPreferences = PickupPreference(sharedPreferences)

        service = NetworkTestConfig(appContext).getService(PickupService::class.java)
        offline = OfflineRepository(rxThreadScheduler, pickupDao)
        masterData = DataRepository(rxThreadScheduler, masterDao)
        masterCalculator = CalculatorRepository(rxThreadScheduler, masterDao)

        repo = PickupRepository(rxThreadScheduler, pickupDao, masterDao, service, pickupPreferences, offline, masterData, masterCalculator)

        val masterService = NetworkTestConfig(appContext).getService(MasterDataService::class.java)
        val masterDataPreference = MasterDataPreference(sharedPreferences)
        val masterLocalRepo = LocalRepository(masterDao, masterService, masterDataPreference)

        //masterLocalRepo._loadMasterDataFromLocalFile(appContext.resources.openRawResource(R.raw.mdata), appContext!!.filesDir.canonicalPath)
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
    fun test_write_data_to_db() {
        //Variant: mockDebug
        val r1 = repo.initFetchTask()
                .scheduleBy(rxThreadScheduler)
                .future()
        //MatcherAssert.assertThat(r1, hasSize(4))

        val r2 = repo.getAllTask()
                .scheduleBy(rxThreadScheduler)
                .future()
        MatcherAssert.assertThat(r2, hasSize(4))

        val r3 = repo.getTracking("122501570100")
                .scheduleBy(rxThreadScheduler)
                .future()
        MatcherAssert.assertThat(r3.orderId, _is("PK4253646453425435"))

        val r4 = repo.getTrackingsOf("1")
                .scheduleBy(rxThreadScheduler)
                .future()
        MatcherAssert.assertThat(r4, hasSize(10))

    }


    @Test
    fun test_has_pending_task() {

//        val has1 = repo.hasPendingTask().scheduleBy(rxThreadScheduler).waitForResult()
//        MatcherAssert.assertThat(has1, _is(false))

        pickupDao.insertPendingReceipt(
            listOf(
                PickupPendingReceiptEntity(
                    "P-BXC-0246-1569216507-002",
                    "P-SDF-0246-1569215354-001",
                    "123",
                    "999999",
                    false,
                    1,
                    1,
                    .0,
                    .0,
                    .0,
                    .0,
                    1,
                    "089-123-4567",
                    "test@test.com",
                    15069458345452,
                    Location(latitude = "13.34346254", longitude = "100.34223"),
                    "1"
                )
            )
        )

        val has2 = repo.hasPendingTask().waitForResult()
        MatcherAssert.assertThat(has2, _is(true))
    }

    @Test
    fun test_get_service_and_sizing() {
        val serviceNonCod = repo.getServiceType(ContextWrapper(appContext), false).waitForResult()
        MatcherAssert.assertThat(serviceNonCod, _is(listOf(2 to "Normal", 3 to "Chilled", 4 to "Frozen")))
        val serviceCod = repo.getServiceType(ContextWrapper(appContext), true).waitForResult()
        MatcherAssert.assertThat(serviceCod, _is(listOf(5 to "Normal", 6 to "Chilled", 7 to "Frozen")))
    }

    @Test
    fun test_calculate_FreightFare() {
        masterDao.insertAllTblScgBranch(listOf(TblScgBranch(4, "103", regionId = 2)))
        masterDao.insertAllTblScgAssortCode(listOf(TblScgAssortCode("10120", 2)))
        masterDao.insertAllTblScgRegionDiff(listOf(TblScgRegionDiff(1, idOriginRegion = 2, idDestinationRegion = 2)))
        masterDao.insertAllTblMasterParcelProperties(listOf(TblMasterParcelProperties(22, 5, 4)))
        masterDao.insertAllTblOrganization(listOf(TblOrganization(1176, priceTierId = 202, customerCode = "999996")))
        masterDao.insertAllTblScgPricingModel(listOf(TblScgPricingModel(idRegionDiff = 1, idParcelProperties = 22, idPriceTier = 202, freightFare = "90.000")))
        masterDao.insertAllTblScgPostalcode(listOf(TblScgPostalcode(id = 45, extraCharge = "0.00", remoteArea = "N")))

        val data1 = repo.calculateFreightFare(4, "5", "4", "10120", 45, 1, "999996").future()
        MatcherAssert.assertThat(data1, _is(90.0))

        masterDao.insertAllTblScgBranch(listOf(TblScgBranch(10, "1100", regionId = 100)))
        masterDao.insertAllTblScgAssortCode(listOf(TblScgAssortCode("10120", 2)))
        masterDao.insertAllTblScgRegionDiff(listOf(TblScgRegionDiff(1, idOriginRegion = 100, idDestinationRegion = 2)))
        masterDao.insertAllTblMasterParcelProperties(listOf(TblMasterParcelProperties(22, 5, 4)))
        masterDao.insertAllTblOrganization(listOf(TblOrganization(1176, priceTierId = 202, customerCode = "999996")))
        masterDao.insertAllTblScgPricingModel(listOf(TblScgPricingModel(idRegionDiff = 1, idParcelProperties = 22, idPriceTier = 202, freightFare = "100.000")))
        masterDao.insertAllTblScgPostalcode(listOf(TblScgPostalcode(id = 20, extraCharge = "20.00", remoteArea = "Y")))

        val data2 = repo.calculateFreightFare(10, "5", "4", "10120", 20, 1, "999996").future()
        MatcherAssert.assertThat(data2, _is(120.0))
    }

    @Test
    fun test_calculate_Cod() {
        masterDao.insertAllTblOrganization(listOf(TblOrganization(1176, priceTierId = 202, codTierId = 15, customerCode = "999996")))
        masterDao.insertAllTblScgCodModel(listOf(TblScgCodModel(17, idCodTier = 15, codAmountMin = "5000.000", codPricePercent = "1.5", codPriceMin = "40.000", codFeeMin = "0.000", idPaymentType = 2, statusActive = "Y", statusDeleted = "N")))

        val data1 = repo.calculateCodFee(1000.0, "999996").future()
        MatcherAssert.assertThat(data1, _is(40.0))
    }

    @Test
    fun test_calculate_carton() {
        masterDao.insertAllTblMasterParcelProperties(listOf(TblMasterParcelProperties(22, 5, 4, statusActive = "Y", parcelSizingPrice = 25.0)))

        val data1 = repo.calculateCartonPrice("5", "4").future()
        MatcherAssert.assertThat(data1, _is(22 to 25.0))
    }

    @Test
    fun test_split_tracking() {

        with(repo) {

            val list = listOf(
                    PickupScanningTrackingEntity("1", "112345678900", null, null, null, null, "10120", 5, 2, 100.0, null, null, null, "", 1562744135171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678904", null, null, null, null, "10120", 5, 2, 300.0, null, 1000.0, null, "", 1562744145171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678902", null, null, null, null, "10120", 5, 2, 200.0, 40.0, null, null, "", 1562744145171, "3245436345", "111222333", false)
//                    PickupScanningTrackingEntity("1", "112345678901", null, null, null, null, "10120", 5, 2, 200.0, null, null, null, "", 1562744145171, "3245436345", "111222333", false),
//                    PickupScanningTrackingEntity("1", "112345678903", null, null, null, null, "10120", 5, 2, 400.0, 2000.0, null, null, "", 1562744145171, "3245436345", "111222333", false),
//                    PickupScanningTrackingEntity("1", "112345678905", null, null, null, null, "10120", 5, 2, 600.0, null, 50.0, null, "", 1562744145171, "3245436345", "111222333", false)
            )

            val expectedNormal = listOf(list[0], list[1], list[2]).map { it.copy() }.map {
                it.cartonFee = null
                it.codFee = null
                it
            }

            val expectedCod = listOf(list[1]).map { it.copy() }.map {
                it.deliveryFee = .0
                it.cartonFee = null
                it
            }

            val expectedCarton = listOf(list[2]).map { it.copy() }.map {
                it.deliveryFee = .0
                it.codFee = null
                it
            }

            val splitReceipt = splitScanningTrackingByType(list)

//            MatcherAssert.assertThat(splitReceipt, _is(mapOf()))

            val normal = splitReceipt[PickupRepository.ReceiptType.Normal]
            MatcherAssert.assertThat(normal, _is(expectedNormal))

            val cod = splitReceipt[PickupRepository.ReceiptType.Cod]
            MatcherAssert.assertThat(cod, _is(expectedCod))

            val carton = splitReceipt[PickupRepository.ReceiptType.Carton]
            MatcherAssert.assertThat(carton, _is(expectedCarton))


            //

            val list2 = listOf(
                    PickupScanningTrackingEntity("1", "112345678900", null, null, null, null, "10120", 5, 2, 100.0, null, null, null, "", 1562744135171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678904", null, null, null, null, "10120", 5, 2, 300.0, null, 1000.0, null, "", 1562744145171, "3245436345", "111222333", false)
            )

            MatcherAssert.assertThat(list2, hasSize(2))


            val expectedNormal2 = listOf(list[0], list[1]).map { it.copy() }.map {
                it.cartonFee = null
                it.codFee = null
                it
            }

            val expectedCod2 = listOf(list[1]).map { it.copy() }.map {
                it.deliveryFee = .0
                it.cartonFee = null
                it
            }

            val splitReceipt2 = splitScanningTrackingByType(list2)
            val normal2 = splitReceipt2[PickupRepository.ReceiptType.Normal]
            MatcherAssert.assertThat(normal2, _is(expectedNormal2))
            val cod2 = splitReceipt2[PickupRepository.ReceiptType.Cod]
            MatcherAssert.assertThat(cod2, _is(expectedCod2))

        }
    }

    @Test
    fun test_submit_tracking() {

        with(repo) {

            pickupPreferences.resetRunningNumber()
            deleteAllPendingReceipt()

            deleteAllScanningTracking()
//            "P-SDF-3442533632-4352563124-001-201907101402218"
            val list = listOf(
                    PickupScanningTrackingEntity("1", "112345678900", null, null, null, null, "10120", 5, 2, 100.0, null, null, null, "", 1562744135171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678901", null, null, null, null, "10120", 5, 2, 200.0, null, null, null, "", 1562744145171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678902", null, null, null, null, "10120", 5, 2, 300.0, null, 1000.0, null, "", 1562744145171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678903", null, null, null, null, "10120", 5, 2, 400.0, null, 2000.0, null, "", 1562744145171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678904", null, null, null, null, "10120", 5, 2, 500.0, 40.0, null, null, "", 1562744145171, "3245436345", "111222333", false),
                    PickupScanningTrackingEntity("1", "112345678905", null, null, null, null, "10120", 5, 2, 600.0, 50.0, null, null, "", 1562744145171, "3245436345", "111222333", false)
            )

            for (e in list) {
                addScanningTracking(e).future()
            }

            val scanningTrackingEntities = getScanningTracking().future()
            MatcherAssert.assertThat(scanningTrackingEntities, _is(list))

            val time = Utils.getCurrentTimestamp().toString().substring(0, 10)
            val x = submitScanningTracking(
                "1",
                "1543",
                "1234",
                1,
                "012-345-6789",
                "test@test.io",
                1.0 to 1.0,
                list
            ).future()

            val submitTracking = getSubmitTracking().future()
            MatcherAssert.assertThat(submitTracking, hasSize(list.size))

            val pendingReceipt = _getAllPendingReceipt().future()
//            MatcherAssert.assertThat(pendingReceipt, _is(listOf(PickupPendingReceiptEntity("", "", false, 0, 0, .0, .0, .0, .0, 0, "", "", 0, Location(), ""))))
            MatcherAssert.assertThat(pendingReceipt.map { it.receiptCode to it.sync }, _is(listOf(
                    "P-SDF-1543-$time-001" to true,
                    "P-SCA-1543-$time-002" to true,
                    "P-BXC-1543-$time-003" to true
            )))

        }
    }
}