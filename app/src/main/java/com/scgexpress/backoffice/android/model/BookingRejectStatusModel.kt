package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BookingRejectStatusModel(
        @SerializedName("subcat_order_id") var subCatOrderID: String? = "",
        @SerializedName("cat_order_id") var catOrderID: String? = "",
        @SerializedName("cat_order_name") var catOrderName: String? = ""
) : Serializable