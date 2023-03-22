package com.scgexpress.backoffice.android.model


data class ServiceTypeWithSizing(
    val id: Int,
    val name: String,
    val sizeList: List<ParcelSizing>
)

data class ParcelSizing(
    val id: Int,
    val name: String
)