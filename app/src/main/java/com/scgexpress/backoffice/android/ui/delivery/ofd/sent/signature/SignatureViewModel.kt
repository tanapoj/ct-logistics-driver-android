package com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.LocationHelper
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.DeliveryOfdParcel
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelList
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelResponse
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryLocalRepository
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

class SignatureViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository,
    private val repoLocal: DeliveryLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        private val IMAGE_DIRECTORY = "/scgexpress"
        const val OFD_STATUS_ID_SUCCESS: String = "success"
        const val BUTTON_CLICKED_DELAY: Long = 1000
    }

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _scanDataList: MutableLiveData<DeliveryOfdParcelList> = MutableLiveData()
    val scanDataList: LiveData<DeliveryOfdParcelList>
        get() = _scanDataList

    private var _manifestID: MutableLiveData<String> = MutableLiveData()
    val manifestID: LiveData<String>
        get() = _manifestID

    private var _finish: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val finish: LiveData<Event<Boolean>>
        get() = _finish

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private var _trackingNo: MutableLiveData<Event<String>> = MutableLiveData()
    val trackingNo: LiveData<Event<String>>
        get() = _trackingNo

    private var _signer: MutableLiveData<String> = MutableLiveData()
    val signer: LiveData<String>
        get() = _signer

    private var fileName = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var mLastClickTime: Long = 0

    fun accept(trackingID: String) {
        addDisposable(repoNetwork.getTracking(trackingID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                } else {
                }
            }) {
                if (it is NoConnectivityException) {
                }
            })
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun finish(finish: Boolean) {
        _finish.value = Event(finish)
    }

    fun setManifestID(manifestID: String) {
        _manifestID.value = manifestID
    }

    fun setScanDataList(scanDataList: DeliveryOfdParcelList) {
        if (scanDataList.items.size == 1) {
            _trackingNo.value = Event(scanDataList.items[0].trackingId)
            _signer.value = scanDataList.items[0].recipientName
        }
        _scanDataList.value = scanDataList
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        )
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".png")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(f.path),
                arrayOf("image/png"), null
            )
            fo.close()
            Timber.d("File Saved::--->%s", f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    fun upPhoto(transferUtility: TransferUtility, view: View, filePath: String, signer: String) {
        _signer.value = signer
        fileName = "parcel/" + manifestID.value + "/signature/" + Utils.getCurrentTimestamp() + "_" + user.id + ".png"
        val file = File(filePath)

        val uploadObserver = transferUtility.upload(fileName, file)

        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    Toast.makeText(view.context, "Upload Completed!", Toast.LENGTH_SHORT).show()
                    file.delete()

                    sendData()
                } else if (TransferState.FAILED == state) {
                    fileName = ""
                    file.delete()

                    sendData()
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

    fun sendData() {
        val signatureImage = Const.AWS_BUCKET + "::" + fileName
        val scanDataList = _scanDataList.value
        val deliveryParcel: ArrayList<DeliveryOfdParcel> = arrayListOf()
        for (data: DeliveryOfdParcel in scanDataList!!.items) {
            deliveryParcel.add(
                DeliveryOfdParcel(
                    trackingId = data.trackingId, statusCode = data.statusCode,
                    codCollected = data.codCollected, datetimeInput = data.datetimeInput,
                    signatureImage = signatureImage, recipientName = signer.value!!,
                    latitude = latitude, longitude = longitude
                )
            )
        }

        addDisposable(repoNetwork.scanOfd(manifestID.value!!, DeliveryOfdParcelList(deliveryParcel))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null) return@subscribe
                for (data: DeliveryOfdParcelResponse in it) {
                    if (data.status == OFD_STATUS_ID_SUCCESS) {
                        showSnackbar(context.getString(R.string.sentence_put_ofd_parcels_successful))
                        finish(true)
                        return@subscribe
                    }
                }
                finish(false)
                showSnackbar(context.getString(R.string.sentence_put_ofd_parcels_failed))
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    fun getLocationHelper(mContext: Context): LocationHelper {
        return LocationHelper.getInstance(mContext)
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}