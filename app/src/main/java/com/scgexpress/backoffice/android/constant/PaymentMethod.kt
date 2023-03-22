package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.MasterParcelModel

@Deprecated("read method from master data (exclude undefined)")
object PaymentMethod {
    val list = (
            arrayListOf(MasterParcelModel("0", "cash", "เงินสด"),
                    MasterParcelModel("1", "credit_card", "บัตรเครดิต"),
                MasterParcelModel("2", "qr_code", "คิวอาร์โค้ด"),
                MasterParcelModel("3", "e_wallet", "อี วอลเล็ท")))
}

