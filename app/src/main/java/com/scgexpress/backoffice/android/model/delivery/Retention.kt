package com.scgexpress.backoffice.android.model.delivery

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Retention(
    @SerializedName("reason_code") var reasonCode: String = "",
    @SerializedName("reason_data") var reasonData: String = ""
) : Serializable {
    val data: RetentionData
        get() = when (reasonCode) {
            else -> RetentionData()
        }
}

open class RetentionData {

    open val message: String
        get() = ""

    open val description: String
        get() = ""

}