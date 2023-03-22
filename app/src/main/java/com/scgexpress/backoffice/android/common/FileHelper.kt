package com.scgexpress.backoffice.android.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileHelper {

    companion object {
        const val DEFAULT_MAX_FILE_AGE: Long = 14 * 24 * 60 * 60 * 1000//14 days

        val MAX_PHOTO_LENGTH = 2000

        val ROOT_DIR = Environment.getExternalStorageDirectory().toString() + Const.DIRECTORY_ROOT
        val ROOT_IMAGE_DIR = ROOT_DIR + Const.DIRECTORY_IMAGE_ROOT

        private val directoryToClear = mapOf(
                "$ROOT_IMAGE_DIR/${Const.DIRECTORY_PICKUP}" to DEFAULT_MAX_FILE_AGE,
                "$ROOT_IMAGE_DIR/${Const.DIRECTORY_OFD_DETAIL}" to DEFAULT_MAX_FILE_AGE,
                "$ROOT_IMAGE_DIR/${Const.DIRECTORY_OFD_DETAIL_TRACKING}" to DEFAULT_MAX_FILE_AGE
        )

        val helper = FileHelper()
    }

    fun clearAllExpiredFiles(fileExpireAge: Long? = null) {
        for ((path, timeout) in directoryToClear) {
            clearExpiredFiles(File(path), fileExpireAge ?: timeout)
        }
    }

    fun clearExpiredFiles(dir: File, fileExpireAge: Long? = null) {
        if (!dir.isDirectory) {
            return
        }
        dir.listFiles()?.forEach { file ->
            file.lastModified()?.let {
                if (it + (fileExpireAge ?: DEFAULT_MAX_FILE_AGE) < Utils.getCurrentTimestamp()) {
                    file.deleteRecursively()
                }
            }
        }
    }

    fun saveImageJpeg(context: Context, folder: String, name: String, bitmap: Bitmap, quality: Int = 90): File? {
        //val fileName = Calendar.getInstance().timeInMillis.toString() + ".jpg"
        val folder = if (folder.startsWith("/")) ROOT_IMAGE_DIR else "$ROOT_IMAGE_DIR/$folder"
        return saveImage(context, folder, name.toJpegFileName(), bitmap, Bitmap.CompressFormat.JPEG, quality)
    }

    private fun saveImage(context: Context, dir: String, fileName: String, bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): File? {
        return saveImage(context, File(dir), fileName, bitmap, format, quality)
    }

    private fun saveImage(context: Context, dir: File, fileName: String, bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): File? {

        val bitmap = preProcessImage(bitmap)

        val mimeTypes = when (format) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            else -> throw IllegalArgumentException("un-support file type format $format")
        }

        val bytes = ByteArrayOutputStream()
        bitmap.compress(format, quality, bytes)

        try {
            val calendar = Calendar.getInstance().apply {
                time = Date()
            }
            val y = "${calendar.get(Calendar.YEAR)}".padStart(4, '0')
            val m = "${calendar.get(Calendar.MONTH) + 1}".padStart(2, '0')
            val d = "${calendar.get(Calendar.DATE)}".padStart(2, '0')

            val subFileName = "$y-$m-$d"

            val dir = dir.apply {
                if (!exists()) mkdirs()
            }

            val subDir = File("${dir.absoluteFile}/$subFileName").apply {
                if (!exists()) mkdirs()
            }

            val file = File(subDir, fileName).apply {
                createNewFile()
            }

            with(FileOutputStream(file)) {
                write(bytes.toByteArray())
                MediaScannerConnection.scanFile(
                        context,
                        arrayOf(file.path),
                        arrayOf(mimeTypes), null
                )
                close()
            }

            Log.i("-scgex129", "File Saved at ${file.absolutePath} / $fileName")
            return file
        } catch (e: IOException) {
            Log.e("-scgex129", "err")
            e.printStackTrace()
        }

        return null
    }

    private fun preProcessImage(bitmap: Bitmap): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_PHOTO_LENGTH && height <= MAX_PHOTO_LENGTH) {
            return bitmap
        }

        var scaleWidth: Int
        var scaleHeight: Int

        if (width > height) {
            scaleWidth = MAX_PHOTO_LENGTH
            scaleHeight = height * MAX_PHOTO_LENGTH / width
        } else {
            scaleWidth = width * MAX_PHOTO_LENGTH / height
            scaleHeight = MAX_PHOTO_LENGTH
        }

        return Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, false)
    }

    private fun String.toJpegFileName() = if (toLowerCase().run { endsWith(".jpg") || endsWith(".jpeg") }) this else "$this.jpg"

    @Throws(IOException::class)
    fun createTempImageFile(externalFilesDir: File): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = externalFilesDir//getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.i("-camera", "storageDir = $storageDir")
        return File.createTempFile(
                "${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            //currentPhotoPath = absolutePath
        }
    }

    fun getDispatchTakePhotoIntent(context: Context, packageManager: PackageManager, externalFilesDir: File): Pair<Intent, Uri> {

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        takePhotoIntent.resolveActivity(packageManager)?.also {
            val photoFile: File? = try {
                createTempImageFile(externalFilesDir)
            } catch (ex: IOException) {
                null
            }

            photoFile?.also { file ->
                val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        Const.PACKAGE_NAME,
                        file
                )
                Log.i("-camera", photoFile.absolutePath)
                takePhotoIntent.apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //putExtra("uri", photoURI)
                    //putExtra("absoluteFile", file.absoluteFile)

                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    //}
                }
                return Pair(takePhotoIntent, photoURI)
            }
        }
        throw IOException()
    }


}