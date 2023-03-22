package com.scgexpress.backoffice.android.ui.delivery.detail

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.constant.RetentionStatus
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.di.glide.GlideApp
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.model.DeliveryOfdParcel
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelList
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TrackingDetailsViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: DeliveryRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val OFD_SENT_STATUS_CODE: String = "34"
        const val OFD_STATUS_ID_SUCCESS: String = "success"

        const val IMAGE_RECIPIENT: String = "imageRecipient"
        const val IMAGE_SIGNATURE: String = "imageSignature"

        const val BUTTON_CLICKED_DELAY: Long = 1000
    }

    private val fileHelper = FileHelper.helper

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    var recipientBitmap: Bitmap? = null
    var signatureBitmap: Bitmap? = null

    private var _data: MutableLiveData<DeliveryTask> = MutableLiveData()
    val data: LiveData<DeliveryTask>
        get() = _data

    private var _scanDataList: MutableLiveData<DeliveryOfdParcelList> = MutableLiveData()
    val scanDataList: LiveData<DeliveryOfdParcelList>
        get() = _scanDataList

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _warning = MutableLiveData<Event<String>>()
    val warning: LiveData<Event<String>>
        get() = _warning

    private val _finish = MutableLiveData<Event<Boolean>>()
    val finish: LiveData<Event<Boolean>>
        get() = _finish

    private val _codDialog = MutableLiveData<Event<Double>>()
    val codDialog: LiveData<Event<Double>>
        get() = _codDialog

    private val mImagePlaceholder: Drawable? =
        ContextCompat.getDrawable(application, R.drawable.ic_placeholder)

    private var retentionReason: String = ""
    private var ofdReStatus: BookingRejectStatusModel = BookingRejectStatusModel()

    var manifestID: String = ""
    var trackingID: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var mLastClickTime: Long = 0

    fun getData() {
        repo.getTaskById(trackingID)
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) {
                    showSnackbar("There is no data loaded from API or offline-data")
                }
                _data.value = it
            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                else
                    showSnackbar("cannot load data, with error: ${it.message}")
            }.also {
                addDisposable(it)
            }
    }

    fun addPhoto(trackingNumber: String, filename: String) {
        /*addDisposable(repo.addPhoto(
            trackingNumber,
            TrackingPhotoList(listOf(Const.AWS_BUCKET + "::" + filename))
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                }
            }) {
                if (it is NoConnectivityException)
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
            })*/
    }

    fun checkCod() {
        if (_data.value!!.trackingCode!!.startsWith("11"))
            showCodDialog(_data.value!!.codAmount!!.toDouble())
        else
            confirmOfdSent(0.0)
    }

    private fun confirmOfdSent(codCollected: Double) {
        val trackingNo: String
        var recipientName = ""
        if (data.value == null) {
            trackingNo = trackingID
        } else {
            trackingNo = data.value!!.trackingCode!!
            recipientName = data.value!!.recipientName!!
        }
        try {
            val deliveryParcel: ArrayList<DeliveryOfdParcel> = arrayListOf()
            deliveryParcel.add(
                DeliveryOfdParcel(
                    trackingId = trackingNo,
                    statusCode = OFD_SENT_STATUS_CODE,
                    codCollected = codCollected,
                    datetimeInput = Utils.getServerDateTimeFormat(Utils.getCurrentTimestamp()),
                    recipientName = recipientName,
                    latitude = latitude,
                    longitude = longitude
                )
            )
            _scanDataList.value = DeliveryOfdParcelList(deliveryParcel)
            //finish(true)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun showDialogCod(context: Context, codAmount: Double) {
        val mBuilder = AlertDialog.Builder(context)

        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setSingleLine(true)
        mBuilder.setView(input)

        mBuilder.setTitle(context.getString(R.string.sentence_please_insert_cod_fee))
        mBuilder.setPositiveButton(context.getString(R.string.ok), null)

        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                if (input.text.toString().trim() != "") {
                    if (input.text.toString().toDouble() <= codAmount) {
                        mDialog.dismiss()
                        confirmOfdSent(input.text.toString().toDouble())
                    } else {
                        showWarning(context.getString(R.string.sentence_please_insert_cod_fee_correctly))
                    }
                }
            }
        }
        mDialog.show()
    }

    private fun confirmOfdRetention(manifestID: String, otherReason: String) {
        val trackingNo: String = if (data.value == null) {
            trackingID
        } else {
            data.value!!.trackingCode!!
        }

        val deliveryParcel: ArrayList<DeliveryOfdParcel> = arrayListOf()
        deliveryParcel.add(
            DeliveryOfdParcel(
                trackingId = trackingNo,
                statusCode = retentionReason,
                datetimeInput = Utils.getServerDateTimeFormat(Utils.getCurrentTimestamp()),
                ofdNote = otherReason,
                latitude = latitude,
                longitude = longitude
            )
        )

        /*addDisposable(repo.scanOfd(manifestID, DeliveryOfdParcelList(deliveryParcel))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it[0].status == OFD_STATUS_ID_SUCCESS) {
                    showSnackbar(context.getString(R.string.sentence_put_ofd_parcels_successful))
                    finish(true)
                } else {
                    showWarning(context.getString(R.string.sentence_put_ofd_parcels_failed))
                    finish(false)
                }
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                } else if (it is IndexOutOfBoundsException) {
                    showWarning(context.getString(R.string.sentence_put_ofd_parcels_failed))
                    finish(false)
                }
            })*/
    }

    fun showDialogRetention(context: Context, manifestID: String) {
        val rReason = arrayOfNulls<String>(RetentionStatus.list.size)
        for (i in 0 until RetentionStatus.list.size) {
            rReason[i] = RetentionStatus.list[i].name
        }

        var isOther = false

        val mBuilder = AlertDialog.Builder(context)

        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        input.layoutParams = lp
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setSingleLine(true)
        input.hint = context.getString(R.string.note)
        mBuilder.setView(input)

        mBuilder.setTitle(context.getString(R.string.retention))
        mBuilder.setSingleChoiceItems(rReason, -1) { _, i ->
            retentionReason = RetentionStatus.list[i].id!!

            isOther = i == rReason.size - 1
        }
        mBuilder.setPositiveButton(context.getString(R.string.ok), null)

        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                if (retentionReason.isNotEmpty()) {
                    if (isOther) {
                        if (input.text.toString().isEmpty()) {
                            showWarning(context.getString(R.string.sentence_retention_please_insert_the_reason))
                        } else {
                            confirmOfdRetention(manifestID, input.text.toString())
                            mDialog.dismiss()
                        }
                    } else {
                        confirmOfdRetention(manifestID, input.text.toString())
                        mDialog.dismiss()
                    }
                }
            }
        }
        mDialog.show()
    }

    fun confirmOfdReStatus(rejectStatus: BookingRejectStatusModel, note: String) {
        /*val reStatus = OfdItemReStatus(
            _data.value!!.idStatus, rejectStatus.subCatOrderID,
            _data.value!!.trackingNumber, rejectStatus.catOrderID, note
        )

        addDisposable(repo.reStatus(manifestID, reStatus)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                showSnackbar(context.getString(R.string.sentence_tracking_details_reset_status_successful))
                getData()
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })*/
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun showWarning(msg: String) {
        _warning.value = Event(msg)
    }

    fun finish(finish: Boolean) {
        _finish.value = Event(finish)
    }

    private fun showCodDialog(cod: Double) {
        _codDialog.value = Event(cod)
    }

    fun saveImage(myBitmap: Bitmap): String {
        val filename = "${Calendar.getInstance().timeInMillis}.jpg"
        return fileHelper.saveImageJpeg(
            context,
            Const.DIRECTORY_OFD_DETAIL_TRACKING,
            filename,
            myBitmap
        )?.absolutePath
            ?: ""
//        val bytes = ByteArrayOutputStream()
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
//        val wallpaperDirectory = File(
//                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs()
//        }
//
//        try {
//            val f = File(wallpaperDirectory, ((Calendar.getInstance()
//                    .timeInMillis).toString() + ".jpg"))
//            f.createNewFile()
//            val fo = FileOutputStream(f)
//            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(context,
//                    arrayOf(f.path),
//                    arrayOf("image/jpeg"), null)
//            fo.close()
//            Timber.d("File Saved::--->%s", f.absolutePath)
//
//            return f.absolutePath
//        } catch (e1: IOException) {
//            e1.printStackTrace()
//        }
//
//        return ""
    }

    internal fun setImage(view: ImageView, url: Any) {
        GlideApp.with(view.context)
            .load(url)
            .placeholder(mImagePlaceholder)
            .centerCrop()
            .into(view)
    }

    fun upPhoto(
        transferUtility: TransferUtility,
        view: ImageView,
        filePath: String,
        trackingNo: String
    ) {
        /*val fileName =
            "parcel/" + trackingNo + "/photos/" + Utils.getCurrentTimestamp() + "_" + user.id + ".jpg"

        val file = File(filePath)

        val uploadObserver = transferUtility.upload(fileName, file)

        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    Toast.makeText(view.context, "Upload Completed!", Toast.LENGTH_SHORT).show()
                    addPhoto(trackingNo, fileName)
                    //file.delete()
                } else if (TransferState.FAILED == state) {
                    //file.delete()
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

        })*/
    }

    fun loadPhoto(
        imgView: ImageView,
        s3Client: AmazonS3Client,
        signatureImage: String,
        imageType: String
    ) {
        /*try {
            var fileName = ""
            if (signatureImage != "") {
                fileName = signatureImage.split("::")[1]
            }
            //fileName = "parcel/120207625381/photos/1546926234867_2337.jpg"
            addDisposable(Observable.create(ObservableOnSubscribe<S3Object>
            { emitter ->
                run {
                    try {
                        emitter.onNext(
                            s3Client.getObject(
                                GetObjectRequest(
                                    Const.AWS_BUCKET,
                                    fileName
                                )
                            )
                        )
                    } catch (e: Exception) {
                        //emitter.onError(e)
                    }
                }
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { result ->
                    try {
                        //val i = BufferedInputStream(result.objectContent)
                        val objectData = result.objectContent
                        convertToBitmap(objectData, imgView, imageType)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
                .subscribe({
                    Timber.d("load image success")
                }) {
                    if (it is NoConnectivityException)
                        showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                })
        } catch (e: Exception) {
            Timber.e(e)
        }*/
    }

    private fun convertToBitmap(
        objectData: S3ObjectInputStream,
        imgView: ImageView,
        imageType: String
    ) {
        /*addDisposable(Observable.create(ObservableOnSubscribe<ByteArray>
        { emitter -> emitter.onNext(IOUtils.toByteArray(objectData)) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result ->
                try {
                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                    if (imageType == IMAGE_RECIPIENT) {
                        recipientBitmap = bitmap
                    } else if (imageType == IMAGE_SIGNATURE) {
                        signatureBitmap = bitmap
                    }
                    setImage(imgView, bitmap!!)
                    objectData.close()
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            .subscribe {
                Timber.d("image display")
            })*/
    }

    fun setManifestId(manifestID: String) {
        this.manifestID = manifestID
    }

    fun setTrackingId(trackingID: String) {
        this.trackingID = trackingID
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