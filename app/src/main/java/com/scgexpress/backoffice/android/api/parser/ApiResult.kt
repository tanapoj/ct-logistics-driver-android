package com.scgexpress.backoffice.android.api.parser

interface ApiResult<out T: Any> {
    val result : T
}
