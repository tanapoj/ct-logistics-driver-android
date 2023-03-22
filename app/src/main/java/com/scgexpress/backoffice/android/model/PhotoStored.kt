package com.scgexpress.backoffice.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoStored(
    val url: String? = null,
    val filePath: String? = null
): Parcelable
