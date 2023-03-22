package com.scgexpress.backoffice.android.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.scgexpress.backoffice.android.common.Const

@Entity(tableName = Const.ROOM_DATABASE_TABLE_TOPIC, primaryKeys = ["id","userId"])
data class TopicEntity(
    @ColumnInfo(name = "id") var id: String = "-1",
    @ColumnInfo(name = "userId") var userId: String = "-1",
    @ColumnInfo(name = "title") var title: String? = "",
    @ColumnInfo(name = "body") var body: String? = ""
)