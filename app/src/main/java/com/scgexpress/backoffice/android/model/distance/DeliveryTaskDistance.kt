package com.scgexpress.backoffice.android.model.distance

import android.os.Parcelable
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryTaskDistance (
    val deliveryTask: DeliveryTask
): Parcelable