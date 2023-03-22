package com.scgexpress.backoffice.android.model.delivery

data class DeliveryReverseFilter(
    var title: String = "",
    var count: Int = 0,
    var itemList: List<DeliveryTask> = listOf()
)