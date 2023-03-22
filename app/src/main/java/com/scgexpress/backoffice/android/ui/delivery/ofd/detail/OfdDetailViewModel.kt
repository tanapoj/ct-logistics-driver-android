package com.scgexpress.backoffice.android.ui.delivery.ofd.detail

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_FILTER_ALL
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_FILTER_BOOKING
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_FILTER_TRACKING
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.FileHelper
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.repository.delivery.TrackingPositionLocalRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class OfdDetailViewModel @Inject constructor(
    application: Application,
    private val repoNetwork: DeliveryNetworkRepository,
    private val repoLocal: TrackingPositionLocalRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    companion object {
        const val BUTTON_CLICKED_DELAY: Long = 1000
    }

    private val fileHelper = FileHelper.helper

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _trackingList = MutableLiveData<ArrayList<TrackingInfo>>()
    val trackingList: LiveData<ArrayList<TrackingInfo>>
        get() = _trackingList

    private val _inProgressTrackingList = MutableLiveData<List<TrackingInfo>>()
    val inProgressTrackingList: LiveData<List<TrackingInfo>>
        get() = _inProgressTrackingList

    private val _completedTrackingList = MutableLiveData<List<TrackingInfo>>()
    val completedTrackingList: LiveData<List<TrackingInfo>>
        get() = _completedTrackingList

    private var _data: MutableLiveData<List<Any>> = MutableLiveData()
    val data: LiveData<List<Any>>
        get() = _data

    private var _dataInProgress: MutableLiveData<List<Any>> = MutableLiveData()
    val dataInProgress: LiveData<List<Any>>
        get() = _dataInProgress

    private var _dataCompleted: MutableLiveData<List<Any>> = MutableLiveData()
    val dataCompleted: LiveData<List<Any>>
        get() = _dataCompleted

    private var _dataFilterInProgress: MutableLiveData<List<Any>> = MutableLiveData()
    val dataFilterInProgress: LiveData<List<Any>>
        get() = _dataFilterInProgress

    private var _dataFilterCompleted: MutableLiveData<List<Any>> = MutableLiveData()
    val dataFilterCompleted: LiveData<List<Any>>
        get() = _dataFilterCompleted

    private var _dataTrackingPosition: MutableLiveData<List<OfdItemPosition>> = MutableLiveData()
    val dataTrackingPosition: LiveData<List<OfdItemPosition>>
        get() = _dataTrackingPosition

    private val _itemTrackingClick: MutableLiveData<TrackingInfo> = MutableLiveData()
    val itemTrackingClick: LiveData<TrackingInfo>
        get() = _itemTrackingClick

    private var _dataHeader: MutableLiveData<Manifest> = MutableLiveData()
    val dataHeader: LiveData<Manifest>
        get() = _dataHeader

    private val _itemLongClick: MutableLiveData<OfdItemInfoList> = MutableLiveData()
    val itemLongClick: LiveData<OfdItemInfoList>
        get() = _itemLongClick

    private val _itemBookingClick: MutableLiveData<BookingInfo> = MutableLiveData()
    val itemBookingClick: LiveData<BookingInfo>
        get() = _itemBookingClick

    private val _phoneCall: MutableLiveData<String> = MutableLiveData()
    val phoneCall: LiveData<String>
        get() = _phoneCall

    private val _photo: MutableLiveData<String> = MutableLiveData()
    val photo: LiveData<String>
        get() = _photo

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean>
        get() = _refreshing

    var mLastClickTime: Long = 0

    fun requestItem(manifestID: String) {
        loadTrackingPosition(manifestID)
    }

    private fun getManifestItems(manifestID: String) {
        addDisposable(repoNetwork.getManifestItems(manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null) return@subscribe
                mapData(it)
            }) {
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    private fun getTrackingInfo(manifestID: String) {
        addDisposable(repoNetwork.getTrackingInfo(manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mapData(it)
                getBookingInfo(manifestID)
            }) {
                //throw OnErrorNotImplementedException(it)
            })
    }

    private fun getBookingInfo(manifestID: String) {
        addDisposable(repoNetwork.getBookingInfo(manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateInProgressData(it)
                updateCompletedData(it)
            }) {
                //throw OnErrorNotImplementedException(it)
            })
    }

    fun loadHeader(manifestID: String) {
        repoNetwork.getManifestHeader(manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null) return@subscribe
                _dataHeader.value = it
            }) {
                //throw OnErrorNotImplementedException(it)
            }.also {
                addDisposable(it)
            }
    }

    fun refresh(manifestID: String) {
        _refreshing.value = true
        addDisposable(repoNetwork.getManifestItems(manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null) return@subscribe
                if (it.trackingList.isNotEmpty()) mapData(it)
                _refreshing.value = false
            }) {
                _refreshing.value = false
                if (it is NoConnectivityException) {
                    showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                }
            })
    }

    private fun mapData(data: ManifestDetail) {
        try {
            if (data.trackingList.isNotEmpty()) {
                _inProgressTrackingList.value = (data.trackingList.filter { d -> d.idStatus == "7" })
                _completedTrackingList.value = (data.trackingList.filter { d -> d.idStatus != "7" })
            }

            updateInProgressData(data.bookingList)
            updateCompletedData(data.bookingList)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun mapData(data: List<TrackingInfo>) {
        try {
            _inProgressTrackingList.value = ((data.filter { d -> d.idStatus == "7" }))
            _completedTrackingList.value = ((data.filter { d -> d.idStatus != "7" }))
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun loadTrackingPosition(manifestID: String) {
        addDisposable(repoLocal.getTrackingPosition(user.id, manifestID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    _dataTrackingPosition.value = it
                }
                getManifestItems(manifestID)
            }) {
            })
    }

    private fun updateInProgressData(data: List<BookingInfo>) {
        val t: ArrayList<Any> = arrayListOf()
        if (_inProgressTrackingList.value != null)
            t.addAll(_inProgressTrackingList.value!!)

        if (data.isNotEmpty()) {
            t.addAll(data.filter { d -> d.assignStatus != "done" })
        }
        _dataInProgress.value = (checkInProgressPosition(t))
    }

    private fun updateCompletedData(data: List<BookingInfo>) {
        val t: ArrayList<Any> = arrayListOf()
        if (_completedTrackingList.value != null)
            t.addAll(_completedTrackingList.value!!)

        if (data.isNotEmpty()) {
            t.addAll(data.filter { d -> d.assignStatus == "done" })
        }
        _dataCompleted.value = (t)
    }

    private fun checkInProgressPosition(items: List<Any>): List<Any> {
        val newItems: ArrayList<Any> = arrayListOf()
        var mapItems: ArrayList<Any> = ArrayList(items.size)
        mapItems.addAll(items)

        val positionItems: List<OfdItemPosition> = _dataTrackingPosition.value!!
        if (positionItems.isNotEmpty()) {
            for (itemNetwork: Any in items) {
                var old = false
                for (itemLocal: OfdItemPosition in positionItems) {
                    if (itemNetwork is TrackingInfo) {
                        if (itemLocal.itemID == itemNetwork.trackingNumber) {
                            if (itemLocal.position!! >= items.size) {
                                mapItems.add(itemNetwork)
                            } else {
                                mapItems[itemLocal.position!!] = itemNetwork
                            }
                            old = true
                        }
                    } else if (itemNetwork is BookingInfo) {
                        if (itemLocal.itemID == itemNetwork.bookingID) {
                            if (itemLocal.position!! >= items.size) {
                                mapItems.add(itemNetwork)
                            } else {
                                mapItems[itemLocal.position!!] = itemNetwork
                            }
                            old = true
                        }
                    }
                }

                if (!old)
                    newItems.add(itemNetwork)
            }
            mapItems.addAll(0, newItems)
        }
        if (mapItems.size > items.size)
            mapItems = shrinkTo(mapItems, items.size)
        return mapItems
    }

    private fun shrinkTo(list: List<Any>, newSize: Int): ArrayList<Any> {
        val items: ArrayList<Any> = ArrayList(list.size)
        items.addAll(list)
        val size = items.size
        if (newSize >= size) return items
        for (i in newSize until size) {
            items.removeAt(items.size - 1)
        }
        return items
    }

    fun itemTrackingClick(item: TrackingInfo) {
        if (checkLastClickTime())
            _itemTrackingClick.value = item
    }

    fun itemTrackingLongClick(position: Int) {
        val itemList = _dataInProgress.value!!
        _itemLongClick.value = OfdItemInfoList(itemList, position)
    }

    fun itemBookingClick(item: BookingInfo) {
        if (checkLastClickTime())
            _itemBookingClick.value = item
    }

    fun phoneCall(tel: String) {
        if (checkLastClickTime())
            _phoneCall.value = tel
    }

    fun photo(trackingNo: String) {
        if (checkLastClickTime())
            _photo.value = trackingNo
    }

    fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    fun saveImage(myBitmap: Bitmap): String {
        val filename = "${Calendar.getInstance().timeInMillis}.jpg"
        return fileHelper.saveImageJpeg(context, Const.DIRECTORY_OFD_DETAIL, filename, myBitmap)?.absolutePath
            ?: ""
//        val bytes = ByteArrayOutputStream()
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
//        val wallpaperDirectory = File(
//                (Environment.getExternalStorageDirectory()).toString() + DIRECTORY_ROOT)
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

    fun upPhoto(transferUtility: TransferUtility, filePath: String, trackingNo: String) {
        val fileName = "parcel/" + trackingNo + "/photos/" + Utils.getCurrentTimestamp() + "_" + user.id + ".jpg"

        val file = File(filePath)

        val uploadObserver = transferUtility.upload(fileName, file)

        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    showSnackbar("Upload Completed!")
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

        })
    }

    fun filterData(category: String) {
        when (category) {
            PARAMS_MANIFEST_FILTER_TRACKING -> {
                _dataFilterInProgress.value = _dataInProgress.value!!.filter { d -> d is TrackingInfo }
                _dataFilterCompleted.value = _dataCompleted.value!!.filter { d -> d is TrackingInfo }
            }
            PARAMS_MANIFEST_FILTER_BOOKING -> {
                _dataFilterInProgress.value = _dataInProgress.value!!.filter { d -> d is BookingInfo }
                _dataFilterCompleted.value = _dataCompleted.value!!.filter { d -> d is BookingInfo }
            }
            PARAMS_MANIFEST_FILTER_ALL -> {
                _dataFilterInProgress.value = _dataInProgress.value!!
                _dataFilterCompleted.value = _dataCompleted.value!!
            }
        }
    }

    fun getData(): OfdItemInfoList {
        val t = dataInProgress.value!! as ArrayList<Any>
        t.addAll(dataCompleted.value!!)

        return OfdItemInfoList(t, 0)
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }
}
