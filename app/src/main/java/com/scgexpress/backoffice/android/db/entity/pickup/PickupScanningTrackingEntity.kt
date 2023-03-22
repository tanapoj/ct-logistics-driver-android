package com.scgexpress.backoffice.android.db.entity.pickup

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "pickup_scanning_tracking", primaryKeys = ["tracking"])
data class PickupScanningTrackingEntity(

        @ColumnInfo(name = "task_id") var taskId: String = "",

        @ColumnInfo(name = "tracking") var tracking: String = "",
        @ColumnInfo(name = "sender_img_url") var senderImgUrl: String? = null,
        @ColumnInfo(name = "sender_img_path") var senderImgPath: String? = null,
        @ColumnInfo(name = "receiver_img_url") var receiverImgUrl: String? = null,
        @ColumnInfo(name = "receiver_img_path") var receiverImgPath: String? = null,
        @ColumnInfo(name = "zipcode") var zipcode: String? = "",
        @ColumnInfo(name = "service_id") var serviceId: Int? = 0,
        @ColumnInfo(name = "size_id") var sizeId: Int? = 0,
        @ColumnInfo(name = "delivery_fee") var deliveryFee: Double = .0 ,
        @ColumnInfo(name = "carton_fee") var cartonFee: Double? = .0,
        @ColumnInfo(name = "cod_fee") var codFee: Double? = .0,
        @ColumnInfo(name = "cod_amount") var codAmount: Double? = .0,
        @ColumnInfo(name = "receipt_code") var receiptCode: String = "",
        @ColumnInfo(name = "scan_at") var scanAt: Long = 0,

        //tracking
        @ColumnInfo(name = "order_id") var orderId: String = "",
        @ColumnInfo(name = "pinbox") var pinbox: String? = null,
        @ColumnInfo(name = "is_extra") var isExtra: Boolean = false,

        var serviceName: String = "",
        var sizeName: String = ""
)