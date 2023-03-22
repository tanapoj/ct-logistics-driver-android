package com.scgexpress.backoffice.android.model.delivery

data class TitleDeliveryCompleted(
    val title: String = "",
    val itemList: List<DeliveryTask> = listOf(DeliveryTask())
)