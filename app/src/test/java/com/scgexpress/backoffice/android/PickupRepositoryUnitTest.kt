//package com.scgexpress.backoffice.android
//
//import com.scgexpress.backoffice.android.api.PickupService
//import com.scgexpress.backoffice.android.db.dao.MasterDataDao
//import com.scgexpress.backoffice.android.db.dao.PickupDao
//import com.scgexpress.backoffice.android.db.entity.pickup.PickupTaskEntity
//import com.scgexpress.backoffice.android.di.RxThreadScheduler
//import com.scgexpress.backoffice.android.model.pickup.PickupTask
//import com.scgexpress.backoffice.android.model.pickup.PickupTaskList
//import com.scgexpress.backoffice.android.repository.pickup.OfflineRepository
//import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
//import io.reactivex.Flowable
//import io.reactivex.Scheduler
//import io.reactivex.android.plugins.RxAndroidPlugins
//import io.reactivex.schedulers.TestScheduler
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.junit.runners.JUnit4
//import org.mockito.Mockito
//import org.mockito.MockitoAnnotations
//import org.mockito.junit.MockitoJUnit
//import org.mockito.Mockito.`when` as _when
//
//
//@RunWith(JUnit4::class)
//class PickupRepositoryUnitTest {
//
//    @get:Rule
//    val mockitoRule = MockitoJUnit.rule()!!
//
//    @Rule
//    @JvmField
//    var rule = InstantTaskExecutorRule()
//
//    private lateinit var repo: PickupRepository
//    private lateinit var offlineRepo: OfflineRepository
//    private lateinit var masterDao: MasterDataDao
//    private lateinit var pickupDao: PickupDao
//    private lateinit var service: PickupService
//
//    private lateinit var rxThreadScheduler: RxThreadScheduler
//    private val testScheduler = TestScheduler()
//
//    @Before
//    fun setUp() {
//        MockitoAnnotations.initMocks(this)
//
//        rxThreadScheduler = object : RxThreadScheduler {
//            val scheduler = testScheduler
//            override fun computation(): Scheduler {
//                return scheduler
//            }
//
//            override fun io(): Scheduler {
//                return scheduler
//            }
//
//            override fun ui(): Scheduler {
//                return scheduler
//            }
//        }
//    }
//
//    @After
//    fun tearDown() {
//        RxAndroidPlugins.reset()
//    }
//
//    @Test
//    fun get_task_from_offline() {
//
//        val entities = listOf(
//            PickupTaskEntity("1", false, "BK1234567890")
//        )
//        val expected = entities.map {
//            it.toModel().apply {
//                isOfflineData = true
//            }
//        }
//
//        pickupDao = Mockito.mock(PickupDao::class.java)
//        masterDao = Mockito.mock(MasterDataDao::class.java)
//        _when(pickupDao.tasks).thenReturn(Flowable.just(entities))
//
//        offlineRepo = OfflineRepository(rxThreadScheduler, pickupDao)
//
//        val result = offlineRepo.getAllTask().test()
//        testScheduler.triggerActions()
//
//        result.assertResult(expected)
//    }
//
//    @Test
//    fun get_task_from_repo() {
//
//        val entities = listOf(
//            PickupTaskEntity("1", false, "BK1234567890"),
//            PickupTaskEntity("2", false,"BK4536457766")
//        )
//        val expected = entities.map {
//            it.toModel().apply {
//                isOfflineData = false
//            }
//        }
//
//        val models = listOf(
//            PickupTask("1", false,"BK1234567890", isOfflineData = false),
//            PickupTask("2", false,"BK4536457766", isOfflineData = false)
//        )
//
//        pickupDao = Mockito.mock(PickupDao::class.java)
//        service = Mockito.mock(PickupService::class.java)
//        _when(service.getPickupTasks()).thenReturn(Flowable.just(PickupTaskList(models)))
//
////        offlineRepo = OfflineRepository(rxThreadScheduler, pickupDao)
////        repo = PickupRepository(rxThreadScheduler, pickupDao, masterDao, service, offlineRepo)
////
////        val result = repo.getAllTask().test()
////        testScheduler.triggerActions()
////
////        result.assertResult(expected)
//    }
//
//    @Test
//    fun get_task_from_repo_no_internet() {
//
//        val entities = listOf(
//            PickupTaskEntity("1", false, "BK1234567890")
//        )
//        val expected = entities.map {
//            it.toModel().apply {
//                isOfflineData = true
//            }
//        }
//
//        pickupDao = Mockito.mock(PickupDao::class.java)
//        _when(pickupDao.tasks).thenReturn(Flowable.just(entities))
//
//        service = Mockito.mock(PickupService::class.java)
//        _when(service.getPickupTasks()).thenReturn(Flowable.error(Exception("no internet")))
//
////        offlineRepo = OfflineRepository(rxThreadScheduler, pickupDao)
////        repo = PickupRepository(rxThreadScheduler, pickupDao, masterDao, service, offlineRepo)
////
////        val result = repo.getAllTask().test()
////        testScheduler.triggerActions()
////
////        result.assertResult(expected)
//    }
//}