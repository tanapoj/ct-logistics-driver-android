package com.scgexpress.backoffice.android.ui.menu

import android.app.Application
import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.stream.MalformedJsonException
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.getCurrentLocation
import com.scgexpress.backoffice.android.common.toSingle
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.repository.pickup.PickupRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MenuViewModel @Inject constructor(
        application: Application,
        private val loginPreference: LoginPreference,
        private val loginRepo: LoginRepository,
        private val notificationRepo: NotificationLocalRepository,
        private var pickupRepo: PickupRepository
) : RxAndroidViewModel(application) {

    companion object {
        const val BUTTON_CLICKED_DELAY: Long = 1000

        enum class SyncState {
            NONE,
            HAS_PENDING,
            SYNCING,
            SYNC_DONE
        }
    }

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _menuItems: MutableLiveData<ArrayList<MenuModel>> = MutableLiveData()
    val menuItems: LiveData<ArrayList<MenuModel>>
        get() = _menuItems

    private val _menuClicked: MutableLiveData<MenuModel> = MutableLiveData()
    val menuClicked: LiveData<MenuModel>
        get() = _menuClicked

    private val _notificationIndicator: MutableLiveData<Boolean> = MutableLiveData()
    val notificationIndicator: LiveData<Boolean>
        get() = _notificationIndicator

    private val _syncState: MutableLiveData<Pair<SyncState, Int>> = MutableLiveData<Pair<SyncState, Int>>().apply { postValue(SyncState.NONE to 0) }
    val syncState: LiveData<Pair<SyncState, Int>>
        get() = _syncState

    private val _dialogMessage: MutableLiveData<String> = MutableLiveData()
    val dialogMessage: LiveData<String>
        get() = _dialogMessage

    private val _logout: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(false) }
    val logout: LiveData<Boolean>
        get() = _logout

    var mLastClickTime: Long = 0

    // stopship : this is just stub
    private val stubMenuItem: ArrayList<MenuModel> = arrayListOf(
            MenuModel(R.id.menu_pickup,
                    context.getString(R.string.pickup),
                    color(R.color.menuOrange),
                    color(R.color.white),
                    R.drawable.ic_store_24dp),
            MenuModel(R.id.menu_delivery,
                    context.getString(R.string.delivery),
                    color(R.color.menuPurple),
                    color(R.color.white),
                    R.drawable.ic_shipping_24dp),
            MenuModel(R.id.menu_navigation,
                context.getString(R.string.navigate),
                color(R.color.menuLightBlue),
                color(R.color.white),
                R.drawable.ic_baseline_navigation_24dp),
            MenuModel(R.id.menu_line_haul,
                    context.getString(R.string.line_haul),
                    color(R.color.menuPink),
                    color(R.color.white),
                    R.drawable.ic_zoom_out_map_24dp),
            MenuModel(R.id.menu_sdreport,
                    context.getString(R.string.sale_driver_report),
                    color(R.color.menuLightBlue),
                    color(R.color.white),
                    R.drawable.ic_baseline_assignment),
            MenuModel(R.id.menu_line,
                    context.getString(R.string.line),
                    color(R.color.menuLine),
                    color(R.color.white),
                    R.drawable.ic_menu_24dp)
    )

    init {
        registerDevice()
    }

    fun requestMenu() {
        _menuItems.value = stubMenuItem
    }


    private fun color(c: Int) = ContextCompat.getColor(context, c)

    fun loadOfflineData() {
        pickupRepo.initFetchTask()
                .subscribe({
                    Timber.d("load offline-data successful with ${it.size} rows")
                }, {
                    it.printStackTrace()
                    if(it is MalformedJsonException){
                        _dialogMessage.value = "API response Mal-Formed Json Data!"
                    }
                    else {
                        //_dialogMessage.value = "cannot load offline-data (${it.message})"
                        Timber.e("cannot load offline-data (${it.message})")
                    }
                })
                .also {
                    addDisposable(it)
                }
    }

    private fun registerDevice() {
        Timber.d("registerDevice()")
        addDisposable(loginRepo.registerDevice(FirebaseInstanceId.getInstance().token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("register device successful")
                }
                ) {
                    if (it is NoConnectivityException) {

                    }
                })
    }

    fun _clearPickupData(){
        pickupRepo.deleteAllPendingReceipt().subscribe({},{})
    }

    fun logout() {
        pickupRepo.hasPendingTask().subscribe({ hasPendingTask ->
            if (hasPendingTask) {
                _syncState.value = SyncState.HAS_PENDING to 0
                syncPendingPickupTask()
            } else {
                loginRepo.logout()
                clearLoginStatus()
                _logout.value = true
            }
        }, {

        }).also {
            addDisposable(it)
        }
    }

    private fun syncPendingPickupTask() {
        pickupRepo.syncAll().subscribe({ (complete, total) ->
            _syncState.value = SyncState.SYNCING to (100 * complete / total)
        }, {
            _syncState.value = SyncState.NONE to 0
            if(it is HttpException){
                val msg = it.response().errorBody()?.string()
                _dialogMessage.value = msg
            }
        }, {
            _syncState.value = SyncState.SYNC_DONE to 0
        }).also {
            addDisposable(it)
        }
    }

    private fun clearLoginStatus() {
        loginPreference.loginUser = ""
        loginPreference.token = ""
        loginPreference.identityID = ""
    }

    fun menuClicked(item: MenuModel) {
        _menuClicked.value = item
    }

    fun checkLastClickTime(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < BUTTON_CLICKED_DELAY) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    fun initNotificationIndicator() {
        notificationRepo.getNotificationByUser(user.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _notificationIndicator.value = it.isNotEmpty()
                }
                .also {
                    addDisposable(it)
                }
    }
}
