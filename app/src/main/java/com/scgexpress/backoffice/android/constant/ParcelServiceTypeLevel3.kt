package com.scgexpress.backoffice.android.constant

import com.scgexpress.backoffice.android.model.MasterParcelModel

object ParcelServiceTypeLevel3 {
    val list = (
            arrayListOf(
                    MasterParcelModel("1", "Undefined", "Undefined"),
                    MasterParcelModel("2", "TA-Q-BIN", "TA-Q-BIN"),
                    MasterParcelModel("3", "Chilled TA-Q-BIN", "Chilled TA-Q-BIN"),
                    MasterParcelModel("4", "Frozen TA-Q-BIN", "Frozen TA-Q-BIN"),
                    MasterParcelModel("5", "TA-Q-BIN Collect", "TA-Q-BIN Collect"),
                    MasterParcelModel("6", "Chilled TA-Q-BIN Collect", "Chilled TA-Q-BIN Collect"),
                    MasterParcelModel("7", "Frozen TA-Q-BIN Collect", "Frozen TA-Q-BIN Collect"),
                    MasterParcelModel("8", "INTL:Import", "INTL:Import"),
                    MasterParcelModel("9", "Chilled INTL:Import", "Chilled INTL:Import"),
                    MasterParcelModel("10", "Frozen INTL:Import", "Frozen INTL:Import"),
                    MasterParcelModel("11", "INTL:Import CTR", "INTL:Import CTR"),
                    MasterParcelModel("12", "INTL:Export", "INTL:Export"),
                    MasterParcelModel("13", "Chilled INTL:Export", "Chilled INTL:Export"),
                    MasterParcelModel("14", "Frozen INTL:Export", "Frozen INTL:Export"),
                    MasterParcelModel("15", "CTR", "CTR")))
}