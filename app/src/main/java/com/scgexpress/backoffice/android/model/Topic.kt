package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.db.entity.TopicEntity

data class Topic(
    @SerializedName("id") var id: String = "-1",
    @SerializedName("userId") var userId: String = "-1",
    @SerializedName("title") var title: String? = "",
    @SerializedName("body") var body: String? = ""
) {
    constructor(entity: TopicEntity) : this() {
        this.id = entity.id
        this.userId = entity.userId
        this.title = entity.title
        this.body = entity.body
    }
}