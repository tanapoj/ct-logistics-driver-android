package com.scgexpress.backoffice.android.api.exception

import java.io.IOException

class TaskFailButSavedOfflineException : IOException() {

    override val message: String
        get() = "No connectivity then saved task offline"

}