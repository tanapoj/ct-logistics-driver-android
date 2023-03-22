package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class TrackingPhoto(
        @SerializedName("photo") var photo: String = ""
)

data class TrackingPhotoList(
        @SerializedName("photos") var photos: List<String> = arrayListOf()
)