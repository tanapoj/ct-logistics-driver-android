package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.MasterParcelModel

object ParcelStatus {
    val list = (
            arrayListOf(
                    MasterParcelModel("1", "รับข้อมูลจากลูกค้า", "ลูกค้าจัดส่งข้อมูลผ่านระบบEDI"),
                    MasterParcelModel("2", "พนักงานรับสินค้า", "พนักงานรับสินค้าจริงจากลูกค้าโดยยังไม่ได้รับข้อมูลผ่านระบบ"),
                    MasterParcelModel("3", "สแกนสินค้า", "สแกนสินค้า"),
                    MasterParcelModel("4", "ทำการสแกนสินค้าออก", "ทำการสแกนสินค้าออก"),
                    MasterParcelModel("5", "รับข้อมูลจากลูกค้า (INTL)", "ลูกค้าจัดส่งข้อมูลผ่านระบบEDI(INTL)"),
                    MasterParcelModel("6", "พนักงานรับสินค้า (INTL)", "พนักงานรับสินค้าจริงจากลูกค้าโดยยังไม่ได้รับข้อมูลผ่านระบบ (INTL)"),
                    MasterParcelModel("7", "ออกจัดส่งสินค้า", "พนักงานกำลังทำการจัดส่ง"),
                    MasterParcelModel("8", "ส่งไม่ทัน", "ส่งสินค้าไม่ทันตามกำหนด"),
                    MasterParcelModel("9", "ติดต่อลูกค้าไม่ได้", "ติดต่อลูกค้าปลายทางไม่ได้"),
                    MasterParcelModel("10", "ลูกค้าเลื่อนวันส่ง", "ลูกค้าปลายทางเลื่อนวันจัดส่ง"),
                    MasterParcelModel("11", "ลูกค้าปฏิเสธรับของ", "ลูกค้าปลายทางปฏิเสธการรับสินค้า"),
                    MasterParcelModel("12", "ลูกค้าไม่อยู่บ้าน", "ลูกค้าปลายทางไม่อยู่บ้าน/ออฟฟิต"),
                    MasterParcelModel("13", "ที่อยู่หรือเบอร์ผิด", "ที่อยู่หรือเบอร์ติดต่อไม่ถูกต้อง"),
                    MasterParcelModel("14", "อื่นๆ", "อื่นๆ"),
                    MasterParcelModel("34", "จัดส่งสินค้าเรียบร้อย", "สินค้าถูกจัดส่งเรียบร้อย")))
}