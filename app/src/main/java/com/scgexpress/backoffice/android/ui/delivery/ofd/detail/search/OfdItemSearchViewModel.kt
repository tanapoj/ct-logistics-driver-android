package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.OfdItemInfoList
import com.scgexpress.backoffice.android.model.TrackingInfo
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

class OfdItemSearchViewModel @Inject constructor(
    application: Application,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<OfdItemInfoList> = MutableLiveData()
    val data: LiveData<OfdItemInfoList>
        get() = _data

    private var _dataSearch: MutableLiveData<List<Any>> = MutableLiveData()
    val dataSearch: LiveData<List<Any>>
        get() = _dataSearch

    private var _manifestBarcode: MutableLiveData<String> = MutableLiveData()
    val manifestBarcode: LiveData<String>
        get() = _manifestBarcode

    private val _itemTrackingClick: MutableLiveData<TrackingInfo> = MutableLiveData()
    val itemTrackingClick: LiveData<TrackingInfo>
        get() = _itemTrackingClick

    private val _itemBookingClick: MutableLiveData<BookingInfo> = MutableLiveData()
    val itemBookingClick: LiveData<BookingInfo>
        get() = _itemBookingClick

    private val _phoneCall: MutableLiveData<String> = MutableLiveData()
    val phoneCall: LiveData<String>
        get() = _phoneCall

    private val _photo: MutableLiveData<String> = MutableLiveData()
    val photo: LiveData<String>
        get() = _photo

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean>
        get() = _refreshing


    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val transferUtility by lazy {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            Const.AWS_POOL_ID,
            Regions.AP_SOUTHEAST_1
        )
        val s3Client = AmazonS3Client(credentialsProvider)
        TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(s3Client)
            .build()
    }

    fun search(id: String) {
        val resultList: ArrayList<Any> = arrayListOf()
        for (item: Any in data.value!!.item) {
            if (item is TrackingInfo) {
                if (item.trackingNumber!!.toLowerCase().contains(id.toLowerCase()))
                    resultList.add(item)
            }
            if (item is BookingInfo) {
                if (item.bookingID!!.toLowerCase().contains(id.toLowerCase()))
                    resultList.add(item)
            }
        }
        _dataSearch.value = resultList
    }

    fun refresh() {
        _refreshing.value = true
        if (dataSearch.value!!.isNotEmpty()) {
            _dataSearch.value = dataSearch.value
            _refreshing.value = false
        }
        _refreshing.value = false
    }


    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + Const.DIRECTORY_ROOT
        )
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Timber.d("File Saved::--->%s", f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    fun upPhoto(filePath: String, trackingNo: String) {
        val fileName = "parcel/" + trackingNo + "/photos/" + Utils.getCurrentTimestamp() + "_" + user.id + ".jpg"

        val file = File(filePath)

        val uploadObserver = transferUtility.upload(fileName, file)

        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    showSnackbar("Upload Completed!")
                    file.delete()
                } else if (TransferState.FAILED == state) {
                    file.delete()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()

                //txtPhoto.text = "ID:$id|bytesCurrent: $bytesCurrent|bytesTotal: $bytesTotal|$percentDone%"
            }

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
            }

        })
    }

    fun setManifestBarcode(manifestBarcode: String) {
        _manifestBarcode.value = manifestBarcode
    }

    fun setData(data: OfdItemInfoList) {
        _data.value = data
    }

    fun itemTrackingClick(item: TrackingInfo) {
        _itemTrackingClick.value = item
    }

    fun itemBookingClick(item: BookingInfo) {
        _itemBookingClick.value = item
    }

    fun phoneCall(tel: String) {
        _phoneCall.value = tel
    }

    fun photo(trackingNo: String) {
        _photo.value = trackingNo
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }
}