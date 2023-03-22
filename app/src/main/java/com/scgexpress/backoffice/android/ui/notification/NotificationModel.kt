package com.scgexpress.backoffice.android.ui.notification

import com.scgexpress.backoffice.android.db.entity.NotificationEntity

data class NotificationModel(
        var id: Long = 0,
        var userID: String = "",
        var metaID: String = "",
        var type: String = "DEFAULT",
        var category: String = "",
        var message: String = "",
        var timestamp: Long = 0,
        var action: List<String> = listOf(),
        var meta: String = "",
        var seen: Boolean = false
) {
    constructor(notificationEntity: NotificationEntity) : this() {
        this.id = notificationEntity.id
        this.userID = notificationEntity.userID
        this.metaID = notificationEntity.metaID
        this.type = notificationEntity.type
        this.category = notificationEntity.category
        this.message = notificationEntity.message
        this.timestamp = notificationEntity.timestamp
        this.action = notificationEntity.action
        this.meta = notificationEntity.meta
        this.seen = notificationEntity.seen
    }
}