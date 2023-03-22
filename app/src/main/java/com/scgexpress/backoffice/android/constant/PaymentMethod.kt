package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.MasterParcelModel

object PaymentMethod {
    val list = (
            arrayListOf(MasterParcelModel("0", "cash", "เงินสด"),
                    MasterParcelModel("1", "credit_card", "บัตรเครดิต")))
}

