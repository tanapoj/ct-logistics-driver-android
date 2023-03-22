package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.db.entity.DeliveryEntity
import java.io.Serializable

data class Delivery(
        @SerializedName("groupID") var groupID: String? = "",
        @SerializedName("userId") var userId: String? = "",
        @SerializedName("manifestID") var manifestID: String? = "",
        @SerializedName("trackingNumber") var trackingNumber: String? = "",
        @SerializedName("senderCode") var senderCode: String? = "",
        @SerializedName("senderName") var senderName: String? = "",
        @SerializedName("statusID") var statusID: String? = "",
        @SerializedName("codAmount") var codAmount: Double? = 0.0,
        @SerializedName("codCollected") var codCollected: Double? = 0.0,
        @SerializedName("orderDate") var orderDate: String? = "",
        @SerializedName("deleted") var deleted: Boolean? = false,
        @SerializedName("sync") var sync: Boolean? = false,
        @SerializedName("timestamp") var timestamp: Long? = Utils.getCurrentTimestamp(),
        @SerializedName("note") var note: String? = ""
) : Serializable {
    constructor(entity: DeliveryEntity) : this() {
        this.groupID = entity.groupID
        this.userId = entity.userId
        this.manifestID = entity.manifestID
        this.trackingNumber = entity.trackingNumber
        this.senderCode = entity.senderCode
        this.senderName = entity.senderName
        this.statusID = entity.statusID
        this.codAmount = entity.codAmount
        this.codCollected = entity.codCollected
        this.orderDate = entity.orderDate
        this.deleted = entity.deleted
        this.sync = entity.sync
        this.timestamp = entity.timestamp
        this.note = entity.note
    }

    constructor(item: DeliveryOfdParcelResponse) : this() {
        this.groupID = ""
        this.userId = ""
        this.manifestID = ""
        this.trackingNumber = item.trackingId
        this.senderCode = item.parcelsInfo.senderCode
        this.senderName = item.parcelsInfo.senderName
        this.statusID = item.status
        this.codAmount = item.parcelsInfo.codAmount
        this.codCollected = 0.0
        this.orderDate = item.parcelsInfo.userOrderDate
        this.deleted = false
        this.sync = true
        this.timestamp = Utils.getCurrentTimestamp()
        this.note = ""
    }
}

