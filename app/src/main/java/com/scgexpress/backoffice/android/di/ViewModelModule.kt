package com.scgexpress.backoffice.android.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.scgexpress.backoffice.android.ui.delivery.DeliveryViewModel
import com.scgexpress.backoffice.android.ui.delivery.location.DeliveryLocationViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.OfdViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent.OfdCantSentViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.create.OfdCreateViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingDetailsViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingItemsViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged.OfdDetailItemsDraggableViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search.OfdItemSearchViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureViewModel
import com.scgexpress.backoffice.android.ui.login.LoginViewModel
import com.scgexpress.backoffice.android.ui.login.PinViewModel
import com.scgexpress.backoffice.android.ui.masterdata.MasterDataViewModel
import com.scgexpress.backoffice.android.ui.menu.MenuViewModel
import com.scgexpress.backoffice.android.ui.notification.NotificationViewModel
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanViewModel
import com.scgexpress.backoffice.android.ui.topic.TopicViewModel
import com.scgexpress.backoffice.android.viewmodel.ViewModelFactory
import com.scgexpress.backoffice.android.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(BookingDetailsViewModel::class)
    abstract fun bindBookingDetailsViewModel(bookingDetailsViewModel: BookingDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookingItemsViewModel::class)
    abstract fun bindBookingItemsViewModel(bookingItemsViewModel: BookingItemsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DeliveryViewModel::class)
    abstract fun bindDeliveryViewModel(deliveryViewModel: DeliveryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DeliveryLocationViewModel::class)
    abstract fun bindDeliveryLocationViewModel(deliveryLocationViewModel: DeliveryLocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel::class)
    abstract fun bindMenuViewModel(menuViewModel: MenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationViewModel(notificationViewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdViewModel::class)
    abstract fun bindOfdViewModel(ofdViewModel: OfdViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdCantSentViewModel::class)
    abstract fun bindOfdCantSentViewModel(ofdCantSentViewModel: OfdCantSentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdCreateViewModel::class)
    abstract fun bindOfdCreateViewModel(ofdCreateViewModel: OfdCreateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdDetailViewModel::class)
    abstract fun bindOfdDetailViewModel(ofdDetailViewModel: OfdDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdDetailItemsDraggableViewModel::class)
    abstract fun bindOfdDetailItemsDraggableViewModel(ofdDetailItemsDraggableViewModel: OfdDetailItemsDraggableViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdItemSearchViewModel::class)
    abstract fun bindOfdItemSearchViewModel(ofdItemSearchViewModel: OfdItemSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdScanViewModel::class)
    abstract fun bindOfdScanViewModel(ofdScanViewModel: OfdScanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OfdSentViewModel::class)
    abstract fun bindOfdSentViewModel(ofdSentViewModel: OfdSentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinViewModel::class)
    abstract fun bindPinViewModel(pinViewModel: PinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MasterDataViewModel::class)
    abstract fun bindMasterDataViewModel(masterDataViewModel: MasterDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopicViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: TopicViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TrackingDetailsViewModel::class)
    abstract fun bindTrackingDetailsViewModel(trackingDetailsViewModel: TrackingDetailsViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SignatureViewModel::class)
    abstract fun bindSignatureViewModel(signatureViewModel: SignatureViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupScanViewModel::class)
    abstract fun bindPickupScanViewModel(pickupScanViewModel: PickupScanViewModel): ViewModel
}