package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.MasterParcelModel

object RetentionStatus {
    val list = (
            arrayListOf(MasterParcelModel("8", "ส่งไม่ทัน", "ส่งสินค้าไม่ทันตามกำหนด"),
                    MasterParcelModel("9", "ติดต่อลูกค้าไม่ได้", "ติดต่อลูกค้าปลายทางไม่ได้"),
                    MasterParcelModel("10", "ลูกค้าเลื่อนวันส่ง", "ลูกค้าปลายทางเลื่อนวันจัดส่ง"),
                    MasterParcelModel("11", "ลูกค้าปฏิเสธรับของ", "ลูกค้าปลายทางปฏิเสธการรับสินค้า"),
                    MasterParcelModel("12", "ลูกค้าไม่อยู่บ้าน", "ลูกค้าปลายทางไม่อยู่บ้าน/ออฟฟิต"),
                    MasterParcelModel("13", "ที่อยู่หรือเบอร์ผิด", "ที่อยู่หรือเบอร์ติดต่อไม่ถูกต้อง"),
                    MasterParcelModel("14", "อื่นๆ", "อื่นๆ")))
}