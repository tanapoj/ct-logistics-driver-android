package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.delivery.RetentionScanReason

object RetentionScanReasonStatus {
    var NoContactId = "RTN0100"
    var WrongAddressId = "RTN0201"
    var CustomerAbsentId = "RTN0202"
    var CustomerRejectId = "RTN0300"
    var CustomerPostPoneId = "RTN0400"
    var DeliveryFailedId = "RTN0500"
    var ProductDamagedId = "RTN0601"
    var LostProductId = "RTN0602"
    var ChangeAddressId = "RTN0701"
    var ReceiveAtBranchId = "RTN0801"
    var CancelDeliveryId = "RTN0901"
    var ChangeSDId = "RTN01001"

    var reasonList = listOf(
        RetentionScanReason(NoContactId,"ติดต่อไม่ได้"),
        RetentionScanReason(WrongAddressId,"Wrong Address"),
        RetentionScanReason(CustomerAbsentId,"Customer Absent"),
        RetentionScanReason(CustomerRejectId,"ลูกค้า Reject"),
        RetentionScanReason(CustomerPostPoneId,"ลูกค้าเลื่อนส่ง"),
        RetentionScanReason(DeliveryFailedId,"สินค้าส่งไม่ได้"),
        RetentionScanReason(ProductDamagedId,"สินค้าเสียหาย"),
        RetentionScanReason(LostProductId,"สินค้าสูญหาย"),
        RetentionScanReason(ChangeAddressId,"ลูกค้าขอเปลี่ยนที่อยู่ในการจัดส่ง"),
        RetentionScanReason(ReceiveAtBranchId,"ลูกค้าขอรับที่สาขา"),
        RetentionScanReason(CancelDeliveryId,"ลูกค้าต้นทางแจ้งยกเลิกการจัดส่ง"),
        RetentionScanReason(ChangeSDId,"เปลี่ยนพนักงานจัดส่ง")
    )

    var noContactList = listOf(
        RetentionScanReason("SRTN0100","ลูกค้าปิดเครื่อง"),
        RetentionScanReason("SRTN0100","เบอร์โทรไม่ถูกต้อง"),
        RetentionScanReason("SRTN0100","ลูกค้าไม่รับสาย")
    )

    var wrongAddressList = listOf(
        RetentionScanReason("SRTN0201","ที่อยู่ไม่ถูกต้อง")
    )

    var customerAbsentList = listOf(
        RetentionScanReason("SRTN0202","ลูกค้าไม่อยู่บ้าน")
    )

    var customerRejectList = listOf(
        RetentionScanReason("SRTN0300","ไม่ได้สั่งสินค้า"),
        RetentionScanReason("SRTN0300","ได้รับสินค้าแล้ว"),
        RetentionScanReason("SRTN0300","แจ้งยกเลิกแล้ว"),
        RetentionScanReason("SRTN0300","สินค้าไม่ครบ"),
        RetentionScanReason("SRTN0300","สภาพสินค้าไม่สมบูรณ์"),
        RetentionScanReason("SRTN0300","ลูกค้าปฏิเสธจ่าย COD")
    )

    var customerPostPoneList = listOf(
        RetentionScanReason("SRTN0400","ไม่ได้เตรียมเงิน COD"),
        RetentionScanReason("SRTN0400","เลื่อนเวลาส่งใหม่")
    )

    var deliveryFailedList = listOf(
        RetentionScanReason("SRTN0500","ส่งสินค้าไม่ทัน"),
        RetentionScanReason("SRTN0500","บริษัทปิด"),
        RetentionScanReason("SRTN0500","น้ำท่วม"),
        RetentionScanReason("SRTN0500","ปิดถนน")
    )

    var productDamagedList = listOf(
        RetentionScanReason("SRTN0601","จาก SD/SR")
    )

    var lostProductList = listOf(
        RetentionScanReason("SRTN0602","จาก SD/SR")
    )

    var changeAddressList = listOf(
        RetentionScanReason("SRTN0701","ลูกค้าขอเปลี่ยนที่อยู่การจัดส่ง")
    )

    var receiveAtBranchList = listOf(
        RetentionScanReason("SRTN0801","ลูกค้าขอรับที่สาขา")
    )

    var cancelDeliveryList = listOf(
        RetentionScanReason("SRTN0901","ลูกค้าต้นทางแจ้งยกเลิกการจัดส่ง")
    )

    var changeSDList = listOf(
        RetentionScanReason("SRTN1001","เปลี่ยนพนักงานจัดส่ง โดยระบุ\n- สาขา\n- รหัส และชื่อพนักงาน")
    )
}