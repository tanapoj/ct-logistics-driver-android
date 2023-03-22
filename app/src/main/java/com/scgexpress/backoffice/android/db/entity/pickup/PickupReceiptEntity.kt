package com.scgexpress.backoffice.android.db.entity.pickup

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import com.scgexpress.backoffice.android.model.pickup.Receipt

@Entity(tableName = "pickup_receipt", primaryKeys = ["receipt_code"])
data class PickupReceiptEntity(

    @SerializedName("receipt_code")
    @ColumnInfo(name = "receipt_code") var receiptCode: String = "",

    @SerializedName("receipt_id")
    @ColumnInfo(name = "receipt_id") var receiptId: String = "",

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at") var createdAt: Long = 0,

    @SerializedName("task_id")
    @ColumnInfo(name = "task_id") var taskId: String = ""
){
    fun toModel(): Receipt = unserializeTo(Receipt::class.java)
}