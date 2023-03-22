@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.scgexpress.backoffice.android.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgPostalcode
import com.scgexpress.backoffice.android.model.Manifest
import com.scgexpress.backoffice.android.model.MetaBooking
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.UserTopic
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.*
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    const val JWT_HEADER: String = "jwtHeader"
    const val JWT_BODY: String = "jwtBody"

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    fun convertObjectToString(obj: Any): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    fun convertStringToUser(user: String): User {
        return if (user.isNotEmpty()) {
            val gson = Gson()
            gson.fromJson(user, object : TypeToken<User>() {}.type)
        } else User()
    }

    fun convertStringToUserTopic(user: String): UserTopic {
        return if (user.isNotEmpty()) {
            Gson().fromJson(user, object : TypeToken<User>() {}.type)
        } else UserTopic()
    }

    fun convertStringToMetaBooking(metaBooking: String): MetaBooking {
        return if (metaBooking.isNotEmpty()) {
            val gson = Gson()
            return gson.fromJson(metaBooking, object : TypeToken<MetaBooking>() {}.type)
        } else MetaBooking()
    }

    fun convertStringToManifest(manifest: String): Manifest {
        return if (manifest.isNotEmpty()) {
            val gson = Gson()
            return gson.fromJson(manifest, object : TypeToken<Manifest>() {}.type)
        } else Manifest()
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun getCurrentDateInMills(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun getServerDateFormat(timeStamp: Long): String {
        return try {
            //2018-10-08
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val netDate = Date(timeStamp)
            sdf.format(netDate)
        } catch (ex: Exception) {
            "2018-10-08"
        }
    }

    fun getServerDateTimeFormat(timeStamp: Long): String {
        return try {
            //2018-10-08
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val netDate = Date(timeStamp)
            sdf.format(netDate)
        } catch (ex: Exception) {
            "2018-10-08 00:00:00"
        }
    }

    fun checkAtLeastString(word: String, length: Int): Boolean {
        return !(word.length != length && !word.isEmpty())
    }

    fun setCurrencyFormatWithUnit(currency: Double) = "${setCurrencyFormat(currency)}฿"

    fun setCurrencyFormat(currency: Double) = currency.toScgCurrency().let {
        if (it == .0) {
            return "0"
        }
        val intVal = it.toInt()
        if (intVal == 0) {
            return "%.2f".format(it)
        }
        val fm = if (intVal.toDouble() == it) DecimalFormat("#,###") else DecimalFormat("#,###.00")
        fm.format(it)
    } ?: "0"

    fun setDoubleCurrencyFormat(currency: String): Double {
        val format = NumberFormat.getInstance(Locale.getDefault())
        val number = format.parse(currency)
        return number.toDouble()
    }

    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): String {
        try {
            val split = JWTEncoded.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            //Log.d("JWT_DECODED", "Header: " + getJson(split[0]))
            //Log.d("JWT_DECODED", "Body: " + getJson(split[1]))
            /*if (option == JWT_HEADER)
                return getJson(split[0])
            else if (option == JWT_BODY) return getJson(split[1])*/
            return getJson(split[1])
        } catch (e: UnsupportedEncodingException) {
            //Error
        }
        return ""
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getRealPathFromURI(context: Context, uri: Uri): String {

        var cursor = context.contentResolver.query(uri, null, null, null, null)
        var path = ""

        // for API 19 and above
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (cursor != null) {
                if (cursor.count != 0) {
                    cursor.moveToFirst()
                    path = cursor.getString(0)
                    path = path.substring(path.lastIndexOf(":") + 1)
                    cursor.close()

                    cursor = context.contentResolver.query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", arrayOf(path), null
                    )
                }
            }
        }
        if (cursor != null) {
            if (cursor.count != 0) {
                cursor.moveToFirst()
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                cursor.close()
            }
        }
        return path
    }

    fun getFileExtension(context: Context, uri: Uri?): String {
        val contentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri!!))!!
    }

    fun encodeToBase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeBase64(input: String): Bitmap {
        val decodedByte = Base64.decode(input, 0)
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun phoneCall(activity: Activity, tel: String) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.CALL_PHONE
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$tel")
            activity.startActivity(callIntent)
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CALL_PHONE), 1)
        }
    }

    fun getLineAppIntent(context: Context): Intent {
        var intent = context.packageManager.getLaunchIntentForPackage(Const.LINE_PACKAGE_NAME)
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${Const.LINE_PACKAGE_NAME}")
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    fun toText(context: Context, file: Int): String {
        val inputStream = context.resources.openRawResource(file)
        val writer = StringWriter()
        val buffer = CharArray(1024)
        inputStream.use { input ->
            val reader = BufferedReader(InputStreamReader(input, "UTF-8"))
            var line: Int = -1
            while ({ line = reader.read(buffer); line }() != -1) {
                writer.write(buffer, 0, line)
            }
        }
        return writer.toString()
    }
}

/*
ex. currency = 10.00155
14:20 Jojoe เราจะปัดก็ต่อเมื่อทศนิยมตำแหน่งที่ 3 ครับ ปัดขึ้นทั้งหมด
14:49 Mongkolchai ทศนิยมแสดงแค่​2 ตำแหน่ง​ จำแหน่งที่3  ปัดขึ้น
14:49 Mongkolchai ตามตัวอย่างต้องเป็น​ 10.01 ครับ
 */
fun Double.toScgCurrency(): Double = Math.ceil(this * 100) / 100

fun Double.toCurrencyFormat() = Utils.setCurrencyFormat(this)
fun Double.toCurrencyFormatWithUnit() = Utils.setCurrencyFormatWithUnit(this)

fun String.decodeBase64() = Base64.decode(this, Base64.DEFAULT).toString(Charset.defaultCharset())

fun String.isTrackingId(): Boolean {
    if (length != 12) {
        return false
    }

    val first11 = substring(0, 11).toLongOrNull() ?: return false
    val verifyBit = substring(11).toIntOrNull() ?: return false

    return verifyBit == (first11 - (first11 / 7) * 7).toInt()
}

fun String.isTrackingCod(): Boolean = isTrackingId() && startsWith("11")
fun String.trimTrackingCode(): String = replace("A", "")

fun String?.toTrackingId(): String {
    if (this == null) return ""
    if (length != 12) {
        return this
    }

    val tracking = StringBuilder(this)
    tracking.insert(8, "-")
    tracking.insert(4, "-")

    return tracking.toString()
}

fun String.toDateFormat(): String {
    return try {
        //2018-10-08
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val netDate = sdf.parse(this)
        sdf.format(netDate)
    } catch (ex: Exception) {
        ""
    }
}

fun Date.toDateFormat(): String {
    return try {
        //2018-10-08
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(this)
    } catch (ex: Exception) {
        ""
    }
}

fun TblOrganization.isAllowCod(): Boolean = codAllowed == "Y"
fun TblScgPostalcode.isRemoteArea(): Boolean = remoteArea == "Y"
fun TblScgPostalcode.hasExtraCharge(): Boolean = extraCharge?.let { it.toDouble() > 0 } ?: false

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.showWarningDialog(message: String) {
    AlertDialog.Builder(this).apply {
        setTitle(resources.getString(R.string.warning))
        setMessage(message)
        setPositiveButton(resources.getString(R.string.ok), null)
        setCancelable(false)
    }.run {
        create()
    }.also { dialog ->
        dialog.show()
    }
}

fun <T> Flowable<T>.toSingle(): Single<T> = Single.create { emitter ->
    subscribe({
        emitter.onSuccess(it)
    }, {
        emitter.onError(it)
    })
}

fun <T> Completable.toSingle(value: T): Single<T> = Single.create { emitter ->
    subscribe({
        emitter.onSuccess(value)
    }, {
        emitter.onError(it)
    })
}