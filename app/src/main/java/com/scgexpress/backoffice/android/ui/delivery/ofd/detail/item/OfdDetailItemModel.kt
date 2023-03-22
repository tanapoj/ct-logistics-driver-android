package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item

data class OfdDetailItemModel(
        val id: String,
        val status: OfdDetailItemStatus,
        val address: String,
        val date: String,
        val tel: String
)

enum class OfdDetailItemStatus(val id: Int) {
    UNKNOWN(-1),
    PICKUP(0),
    PICKUP_PICKED(1),
    OFD(2),
    OFD_DELIVERED(3),
    OFD_RETENTION(4);

    companion object {

        fun getStatusById(id: Int): OfdDetailItemStatus{
            for (status in values()){
                if (id == status.id) return status
            }
            return UNKNOWN
        }

    }
}
