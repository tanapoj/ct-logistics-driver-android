package com.scgexpress.backoffice.android.di

import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.BaseActivityModule
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivity
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivityModule
import com.scgexpress.backoffice.android.ui.delivery.detail.TrackingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.detail.TrackingDetailsActivityModule
import com.scgexpress.backoffice.android.ui.delivery.location.DeliveryLocationActivity
import com.scgexpress.backoffice.android.ui.delivery.location.DeliveryLocationActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.retention.OfdRetentionActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.retention.OfdRetentionActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivityModule
import com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime.RetentionChangeDateActivity
import com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime.RetentionChangeDateActivityModule
import com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver.RetentionChangeSDActivity
import com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver.RetentionChangeSDActivityModule
import com.scgexpress.backoffice.android.ui.delivery.retention.reason.RetentionReasonActivity
import com.scgexpress.backoffice.android.ui.delivery.retention.reason.RetentionReasonActivityModule
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskActivity
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskActivityModule
import com.scgexpress.backoffice.android.ui.delivery.task.search.DeliverySearchActivity
import com.scgexpress.backoffice.android.ui.delivery.task.search.DeliverySearchActivityModule
import com.scgexpress.backoffice.android.ui.login.LoginActivity
import com.scgexpress.backoffice.android.ui.login.LoginActivityModule
import com.scgexpress.backoffice.android.ui.main.MainActivity
import com.scgexpress.backoffice.android.ui.main.MainActivityModule
import com.scgexpress.backoffice.android.ui.masterdata.MasterDataActivity
import com.scgexpress.backoffice.android.ui.masterdata.MasterDataActivityModule
import com.scgexpress.backoffice.android.ui.masterdata.forceupdate.ForceUpdateActivity
import com.scgexpress.backoffice.android.ui.masterdata.forceupdate.ForceUpdateActivityModule
import com.scgexpress.backoffice.android.ui.menu.MenuActivity
import com.scgexpress.backoffice.android.ui.menu.MenuActivityModule
import com.scgexpress.backoffice.android.ui.navigation.NavigationMainActivity
import com.scgexpress.backoffice.android.ui.navigation.NavigationMainActivityModule
import com.scgexpress.backoffice.android.ui.navigation.map.NavigationMapActivity
import com.scgexpress.backoffice.android.ui.navigation.map.NavigationMapActivityModule
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import com.scgexpress.backoffice.android.ui.notification.NotificationActivityModule
import com.scgexpress.backoffice.android.ui.photo.PhotoConfirmActivity
import com.scgexpress.backoffice.android.ui.photo.PhotoConfirmActivityModule
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListActivity
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListActivityModule
import com.scgexpress.backoffice.android.ui.pickup.detail.PickupDetailsActivity
import com.scgexpress.backoffice.android.ui.pickup.detail.PickupDetailsActivityModule
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainActivity
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainActivityModule
import com.scgexpress.backoffice.android.ui.pickup.receipt.PickupReceiptActivity
import com.scgexpress.backoffice.android.ui.pickup.receipt.PickupReceiptActivityModule
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanActivity
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanActivityModule
import com.scgexpress.backoffice.android.ui.pickup.summary.PickupSummaryActivity
import com.scgexpress.backoffice.android.ui.pickup.summary.PickupSummaryActivityModule
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivityModule
import com.scgexpress.backoffice.android.ui.pickup.task.search.PickupSearchActivity
import com.scgexpress.backoffice.android.ui.pickup.task.search.PickupSearchActivityModule
import com.scgexpress.backoffice.android.ui.pin.PinActivity
import com.scgexpress.backoffice.android.ui.pin.PinActivityModule
import com.scgexpress.backoffice.android.ui.topic.TopicActivity
import com.scgexpress.backoffice.android.ui.topic.TopicActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [(BaseActivityModule::class)])
    internal abstract fun bindBaseActivity(): BaseActivity

    @ContributesAndroidInjector(modules = [(DeliveryMainActivityModule::class)])
    internal abstract fun bindDeliveryMainActivity(): DeliveryMainActivity

    @ContributesAndroidInjector(modules = [(DeliveryLocationActivityModule::class)])
    internal abstract fun bindDeliveryLocationActivity(): DeliveryLocationActivity

    @ContributesAndroidInjector(modules = [(DeliverySearchActivityModule::class)])
    internal abstract fun bindDeliverySearchActivity(): DeliverySearchActivity

    @ContributesAndroidInjector(modules = [(DeliveryTaskActivityModule::class)])
    internal abstract fun bindDeliveryTaskActivity(): DeliveryTaskActivity

    @ContributesAndroidInjector(modules = [(ForceUpdateActivityModule::class)])
    internal abstract fun bindForceUpdateActivity(): ForceUpdateActivity

    @ContributesAndroidInjector(modules = [(LoginActivityModule::class)])
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [(MasterDataActivityModule::class)])
    internal abstract fun bindMasterDataActivity(): MasterDataActivity

    @ContributesAndroidInjector(modules = [(MenuActivityModule::class)])
    internal abstract fun bindMenuActivity(): MenuActivity

    @ContributesAndroidInjector(modules = [(NavigationMainActivityModule::class)])
    internal abstract fun bindNavigationMainActivity(): NavigationMainActivity

    @ContributesAndroidInjector(modules = [(NavigationMapActivityModule::class)])
    internal abstract fun bindNavigationMapActivity(): NavigationMapActivity

    @ContributesAndroidInjector(modules = [(NotificationActivityModule::class)])
    internal abstract fun bindNotificationActivity(): NotificationActivity

    @ContributesAndroidInjector(modules = [(OfdRetentionActivityModule::class)])
    internal abstract fun bindOfdRetentionActivity(): OfdRetentionActivity

    @ContributesAndroidInjector(modules = [(OfdScanActivityModule::class)])
    internal abstract fun bindOfdScanActivity(): OfdScanActivity

    @ContributesAndroidInjector(modules = [(OfdSentActivityModule::class)])
    internal abstract fun bindOfdSentActivity(): OfdSentActivity

    @ContributesAndroidInjector(modules = [(PickupDetailsActivityModule::class)])
    internal abstract fun bindPickupDetailsActivity(): PickupDetailsActivity

    @ContributesAndroidInjector(modules = [(PickupSearchActivityModule::class)])
    internal abstract fun bindPickupSearchActivity(): PickupSearchActivity

    @ContributesAndroidInjector(modules = [(PickupTaskActivityModule::class)])
    internal abstract fun bindPickupTaskActivity(): PickupTaskActivity

    @ContributesAndroidInjector(modules = [(PickupReceiptActivityModule::class)])
    internal abstract fun bindPickupReceiptActivity(): PickupReceiptActivity

    @ContributesAndroidInjector(modules = [(PickupScanActivityModule::class)])
    internal abstract fun bindPickupScanActivity(): PickupScanActivity

    @ContributesAndroidInjector(modules = [(PickupSummaryActivityModule::class)])
    internal abstract fun bindPickupSummaryActivity(): PickupSummaryActivity

    @ContributesAndroidInjector(modules = [(PickupBookingListActivityModule::class)])
    internal abstract fun bindPickupTaskListActivity(): PickupBookingListActivity

    @ContributesAndroidInjector(modules = [(PickupMainActivityModule::class)])
    internal abstract fun bindPickupMainActivity(): PickupMainActivity

    @ContributesAndroidInjector(modules = [(PinActivityModule::class)])
    internal abstract fun bindPinActivity(): PinActivity

    @ContributesAndroidInjector(modules = [PhotoConfirmActivityModule::class])
    internal abstract fun bindPhotoConfirmActivityActivity(): PhotoConfirmActivity

    @ContributesAndroidInjector(modules = [(TopicActivityModule::class)])
    internal abstract fun bindTopicActivity(): TopicActivity

    @ContributesAndroidInjector(modules = [(TrackingDetailsActivityModule::class)])
    internal abstract fun bindTrackingDetailsActivity(): TrackingDetailsActivity

    @ContributesAndroidInjector(modules = [(RetentionReasonActivityModule::class)])
    internal abstract fun bindRetentionScanActivity(): RetentionReasonActivity

    @ContributesAndroidInjector(modules = [(RetentionChangeSDActivityModule::class)])
    internal abstract fun bindRetentionScanTrackingActivity(): RetentionChangeSDActivity

    @ContributesAndroidInjector(modules = [(RetentionChangeDateActivityModule::class)])
    internal abstract fun bindRetentionChangeDateActivity(): RetentionChangeDateActivity

    @ContributesAndroidInjector(modules = [(SignatureActivityModule::class)])
    internal abstract fun bindSignatureActivity(): SignatureActivity

}