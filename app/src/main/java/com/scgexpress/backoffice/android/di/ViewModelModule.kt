package com.scgexpress.backoffice.android.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.scgexpress.backoffice.android.ui.delivery.DeliveryViewModel
import com.scgexpress.backoffice.android.ui.delivery.detail.TrackingDetailsViewModel
import com.scgexpress.backoffice.android.ui.delivery.location.DeliveryLocationViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.OfdViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.retention.OfdRetentionViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureViewModel
import com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime.RetentionChangeDateViewModel
import com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver.RetentionChangeSDViewModel
import com.scgexpress.backoffice.android.ui.delivery.retention.reason.RetentionReasonViewModel
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskViewModel
import com.scgexpress.backoffice.android.ui.dialog.PhotoDialogViewModel
import com.scgexpress.backoffice.android.ui.dialog.RetentionSelectSubReasonDialogViewModel
import com.scgexpress.backoffice.android.ui.login.LoginViewModel
import com.scgexpress.backoffice.android.ui.login.PinViewModel
import com.scgexpress.backoffice.android.ui.masterdata.MasterDataViewModel
import com.scgexpress.backoffice.android.ui.menu.MenuViewModel
import com.scgexpress.backoffice.android.ui.navigation.NavigationMainViewModel
import com.scgexpress.backoffice.android.ui.notification.NotificationViewModel
import com.scgexpress.backoffice.android.ui.photo.PhotoConfirmViewModel
import com.scgexpress.backoffice.android.ui.pickup.detail.PickupDetailsViewModel
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainViewModel
import com.scgexpress.backoffice.android.ui.pickup.receipt.PickupReceiptViewModel
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanViewModel
import com.scgexpress.backoffice.android.ui.pickup.summary.PickupSummaryViewModel
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskViewModel
import com.scgexpress.backoffice.android.ui.topic.TopicViewModel
import com.scgexpress.backoffice.android.viewmodel.ViewModelFactory
import com.scgexpress.backoffice.android.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListViewModel as PickupTaskListViewModel1

@Module
internal abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

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
    @ViewModelKey(DeliveryTaskViewModel::class)
    abstract fun bindDeliveryTaskViewModel(deliveryTaskViewModel: DeliveryTaskViewModel): ViewModel

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
    @ViewModelKey(NavigationMainViewModel::class)
    abstract fun bindNavigationMainViewModel(navigationMainViewModel: NavigationMainViewModel): ViewModel

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
    @ViewModelKey(OfdRetentionViewModel::class)
    abstract fun bindOfdRetentionViewModel(ofdRetentionViewModel: OfdRetentionViewModel): ViewModel

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
    @ViewModelKey(PhotoDialogViewModel::class)
    abstract fun bindPhotoDialogViewModel(photoDialogViewModel: PhotoDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinViewModel::class)
    abstract fun bindPinViewModel(pinViewModel: PinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupDetailsViewModel::class)
    abstract fun bindPickupDetailsViewModel(pickupDetailsViewModel: PickupDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupReceiptViewModel::class)
    abstract fun bindPickupReceiptViewModel(pickupReceiptViewModel: PickupReceiptViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupSummaryViewModel::class)
    abstract fun bindPickupSummaryViewModel(pickupSummaryViewModel: PickupSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupTaskViewModel::class)
    abstract fun bindPickupTaskViewModel(pickupTaskViewModel: PickupTaskViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupScanViewModel::class)
    abstract fun bindPickupScanViewModel(pickupScanViewModel: PickupScanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupTaskListViewModel1::class)
    abstract fun bindPickupTaskListViewModel(pickupTaskListViewModel: PickupTaskListViewModel1): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickupMainViewModel::class)
    abstract fun bindPickupMainViewModel(pickupMainViewModel: PickupMainViewModel): ViewModel

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
    @ViewModelKey(RetentionReasonViewModel::class)
    abstract fun bindRetentionScanViewModel(retentionReasonViewModel: RetentionReasonViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RetentionSelectSubReasonDialogViewModel::class)
    abstract fun bindRetentionSelectSubReasonDialogViewModel(retentionSelectSubReasonDialogViewModel: RetentionSelectSubReasonDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RetentionChangeSDViewModel::class)
    abstract fun bindRetentionChangeSDViewModel(retentionChangeSDViewModel: RetentionChangeSDViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RetentionChangeDateViewModel::class)
    abstract fun bindRetentionChangeDateViewModel(retentionChangeDateViewModel: RetentionChangeDateViewModel): ViewModel

    @ViewModelKey(SignatureViewModel::class)
    abstract fun bindSignatureViewModel(signatureViewModel: SignatureViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhotoConfirmViewModel::class)
    abstract fun bindPhotoConfirmViewModel(photoConfirmViewModel: PhotoConfirmViewModel): ViewModel

}