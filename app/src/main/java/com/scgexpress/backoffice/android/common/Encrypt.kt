package com.scgexpress.backoffice.android.common

import java.security.NoSuchAlgorithmException

fun md5(s: Any) = try {
    // Create MD5 Hash
    val digest = java.security.MessageDigest
        .getInstance("MD5")
    digest.update(s.toString().toByteArray())
    val messageDigest = digest.digest()
    // Create Hex String
    val hexString = StringBuilder()
    for (aMessageDigest in messageDigest) {
        var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
        while (h.length < 2)
            h = "0$h"
        hexString.append(h)
    }
    hexString.toString()
} catch (e: NoSuchAlgorithmException) {
    ""
}