package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.BookingRejectStatusModel


object BookingRejectStatus {
    val list = (
            arrayListOf(
                    BookingRejectStatusModel("10", "4", "ลูกค้ายกเลิกการเข้ารับพัสดุ"),
                    BookingRejectStatusModel("11", "4", "ลูกค้าเลื่อนวันเข้ารับพัสดุ"),
                    BookingRejectStatusModel("12", "4", "ลูกค้าแจ้งที่อยู่เข้ารับไม่ถูกต้อง"),
                    BookingRejectStatusModel("13", "4", "ติดต่อลูกค้าไม่ได้"),
                    BookingRejectStatusModel("14", "4", "ขนาดหรือน้ำหนักของพัสดุ เกินกว่าที่บริษัทกำหนด"),
                    BookingRejectStatusModel("15", "4", "ประเภทสินค้าไม่ตรงตามเงื่อนไขของบริษัท"),
                    BookingRejectStatusModel("16", "4", "Booking ซ้ำ"),
                    BookingRejectStatusModel("17", "4", "CS แจ้งยกเลิก"),
                    BookingRejectStatusModel("19", "4", "พนักงานเข้ารับพัสดุล่าช้า"),
                    BookingRejectStatusModel("21", "4", "พนักงานไม่สามารถเข้าพื้นที่ได้เนื่องจากภัยธรรมชาติ"),
                    BookingRejectStatusModel("26", "4", "อื่นๆ")))
}