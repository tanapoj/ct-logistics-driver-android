package com.scgexpress.backoffice.android.model.distance

import android.os.Parcelable
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PickupTaskDistance (
    val pickupTask: PickupTask
) :Parcelable