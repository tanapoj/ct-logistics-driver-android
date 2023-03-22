package com.scgexpress.backoffice.android.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "userID") var userID: String = "",
    @ColumnInfo(name = "metaID") var metaID: String = "",
    @ColumnInfo(name = "type") var type: String = "DEFAULT",
    @ColumnInfo(name = "category") var category: String = "",
    @ColumnInfo(name = "message") var message: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = 0,
    @ColumnInfo(name = "action") var action: List<String> = listOf(),
    @ColumnInfo(name = "meta") var meta: String = "",
    @ColumnInfo(name = "seen") var seen: Boolean = false
)