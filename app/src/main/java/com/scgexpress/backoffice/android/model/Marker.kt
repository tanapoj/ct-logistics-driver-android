package com.scgexpress.backoffice.android.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class MarkerTask(
    val latLng: LatLng,
    val bitmap: Bitmap,
    val title: String?
)