package com.scgexpress.backoffice.android.model.distance

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllTaskDistance(
    val pickupTask: PickupTaskDistance?,
    val deliveryTask: DeliveryTaskDistance?
) : Parcelable