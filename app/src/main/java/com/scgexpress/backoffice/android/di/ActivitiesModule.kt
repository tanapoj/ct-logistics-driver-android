package com.scgexpress.backoffice.android.di

import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.BaseActivityModule
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivity
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivityModule
import com.scgexpress.backoffice.android.ui.delivery.location.DeliveryLocationActivity
import com.scgexpress.backoffice.android.ui.delivery.location.DeliveryLocationActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent.OfdCantSentActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent.OfdCantSentActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.create.OfdCreateActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.create.OfdCreateActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingDetailsActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingItemsActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingItemsActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged.OfdDetailItemsDraggableActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged.OfdDetailItemsDraggableActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search.OfdItemSearchActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search.OfdItemSearchActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentActivityModule
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivityModule
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
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import com.scgexpress.backoffice.android.ui.notification.NotificationActivityModule
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanActivity
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanActivityModule
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

    @ContributesAndroidInjector(modules = [(BookingDetailsActivityModule::class)])
    internal abstract fun bindBookingDetailsActivity(): BookingDetailsActivity

    @ContributesAndroidInjector(modules = [(BookingItemsActivityModule::class)])
    internal abstract fun bindBookingItemsActivity(): BookingItemsActivity

    @ContributesAndroidInjector(modules = [(DeliveryMainActivityModule::class)])
    internal abstract fun bindDeliveryMainActivity(): DeliveryMainActivity

    @ContributesAndroidInjector(modules = [(DeliveryLocationActivityModule::class)])
    internal abstract fun bindDeliveryLocationActivity(): DeliveryLocationActivity

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

    @ContributesAndroidInjector(modules = [(NotificationActivityModule::class)])
    internal abstract fun bindNotificationActivity(): NotificationActivity

    @ContributesAndroidInjector(modules = [(OfdCantSentActivityModule::class)])
    internal abstract fun bindOfdCantSentActivity(): OfdCantSentActivity

    @ContributesAndroidInjector(modules = [(OfdCreateActivityModule::class)])
    internal abstract fun bindOfdCreateManifestActivity(): OfdCreateActivity

    @ContributesAndroidInjector(modules = [(OfdDetailActivityModule::class)])
    internal abstract fun bindOfdDetailActivity(): OfdDetailActivity

    @ContributesAndroidInjector(modules = [(OfdDetailItemsDraggableActivityModule::class)])
    internal abstract fun bindOfdDetailItemsDraggableActivity(): OfdDetailItemsDraggableActivity

    @ContributesAndroidInjector(modules = [(OfdItemSearchActivityModule::class)])
    internal abstract fun bindOfdItemSearchActivity(): OfdItemSearchActivity

    @ContributesAndroidInjector(modules = [(OfdScanActivityModule::class)])
    internal abstract fun bindOfdScanActivity(): OfdScanActivity

    @ContributesAndroidInjector(modules = [(OfdSentActivityModule::class)])
    internal abstract fun bindOfdSentActivity(): OfdSentActivity

    @ContributesAndroidInjector(modules = [(PinActivityModule::class)])
    internal abstract fun bindPinActivity(): PinActivity

    @ContributesAndroidInjector(modules = [(TopicActivityModule::class)])
    internal abstract fun bindTopicActivity(): TopicActivity

    @ContributesAndroidInjector(modules = [(TrackingDetailsActivityModule::class)])
    internal abstract fun bindTrackingDetailsActivity(): TrackingDetailsActivity

    @ContributesAndroidInjector(modules = [(SignatureActivityModule::class)])
    internal abstract fun bindSignatureActivity(): SignatureActivity

    @ContributesAndroidInjector(modules = [(PickupScanActivityModule::class)])
    internal abstract fun bindPickupScanActivity(): PickupScanActivity
}