package com.scgexpress.backoffice.android.model.pickup

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.model.tracking.Tracking
import java.io.Serializable

data class TaskTrackingInfo(
    @SerializedName("trackings") var trackings: List<Tracking> = mutableListOf(),
    @SerializedName("receipts") var receipts: List<Receipt> = mutableListOf()
) : Serializable