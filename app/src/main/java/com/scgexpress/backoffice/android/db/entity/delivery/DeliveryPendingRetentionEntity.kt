package com.scgexpress.backoffice.android.db.entity.delivery

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "delivery_pending_retention")
data class DeliveryPendingRetentionEntity(

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0,

    @SerializedName("submit_at")
    @ColumnInfo(name = "submit_at") var submitAt: Long = 0,

    @SerializedName("sync")
    @ColumnInfo(name = "sync") var sync: Boolean = false
)