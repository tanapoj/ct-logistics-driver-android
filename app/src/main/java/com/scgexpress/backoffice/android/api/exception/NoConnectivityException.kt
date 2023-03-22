package com.scgexpress.backoffice.android.api.exception

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String
        get() = "No connectivity exception"

}