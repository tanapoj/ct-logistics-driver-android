package com.scgexpress.backoffice.android.model.delivery

import com.google.gson.annotations.SerializedName

data class SentSubmitResponse(
    @SerializedName("status") var status: String = ""
){
    fun isAccept() = status == "ACCEPT"
}