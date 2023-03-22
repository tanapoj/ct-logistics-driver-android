package com.scgexpress.backoffice.android.common

import io.reactivex.Single
import java.io.File
import java.lang.IllegalStateException

class FileSyncable(
    var fileUploader: FileUploader,
    val path: String,
    var uploadPath: String? = null
) {

    constructor(
        fileUploader: FileUploader,
        file: File,
        uploadPath: String? = null
    ) : this(fileUploader, file.canonicalPath, uploadPath)

    private val localFile: File by lazy {
        File(path)
    }

    var url: String? = null

    fun isSynced() = url != null

    fun sync() = Single.create<String> { emitter ->
        if (isSynced()) {
            emitter.onSuccess(url!!)
            return@create
        }
        check(uploadPath != null) { "call sync file to s3 while upload-path is null or empty" }
        fileUploader.uploadFile(localFile, uploadPath!!).subscribe({ s3url ->
            if (s3url != null) {
                url = s3url
                emitter.onSuccess(s3url)
            } else {
                emitter.onError(IllegalStateException("upload file to s3 but return null"))
            }
        }, emitter::onError)
    }

}