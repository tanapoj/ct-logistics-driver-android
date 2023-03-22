package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.db.entity.TrackingPositionEntity
import java.io.Serializable

data class OfdItemPosition(
        @SerializedName("userID") var userId: String? = "",
        @SerializedName("manifestID") var manifestID: String? = "",
        @SerializedName("itemID") var itemID: String? = "",
        @SerializedName("senderCode") var position: Int? = 0
) : Serializable {
    constructor(entity: TrackingPositionEntity) : this() {
        this.userId = entity.userId
        this.manifestID = entity.manifestID
        this.itemID = entity.trackingNumber
        this.position = entity.position
    }
}