package com.scgexpress.backoffice.android.ui.pickup.task.item

data class PickupTaskItemsModel(
    val id: String,
    val status: PickupTaskItemsStatus,
    val address: String,
    val date: String,
    val tel: String
)

enum class PickupTaskItemsStatus(val id: Int) {
    UNKNOWN(-1),
    PICKUP(0),
    PICKUP_PICKED(1),
    OFD(2),
    OFD_DELIVERED(3),
    OFD_RETENTION(4);

    companion object {

        fun getStatusById(id: Int): PickupTaskItemsStatus {
            for (status in values()) {
                if (id == status.id) return status
            }
            return UNKNOWN
        }

    }
}
