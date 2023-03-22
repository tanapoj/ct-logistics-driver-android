package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName

data class ManifestHeader(
        @SerializedName("manifest") val manifest: Manifest = Manifest()
)