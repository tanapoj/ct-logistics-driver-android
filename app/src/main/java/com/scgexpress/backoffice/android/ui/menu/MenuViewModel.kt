package com.scgexpress.backoffice.android.ui.menu

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.repository.notification.NotificationLocalRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MenuViewModel @Inject constructor(
    application: Application,
    private val repository: LoginRepository,
    private val loginPreference: LoginPreference,
    private val notiRepo: NotificationLocalRepository
) : RxAndroidViewModel(application) {

    companion object {
        const val BUTTON_CLICKED_DELAY: Long = 1000
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

    private fun registerDevice() {
        Timber.d("registerDevice()")
        addDisposable(repository.registerDevice(FirebaseInstanceId.getInstance().token!!)
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

    fun logout() {
        repository.logout()
        clearLoginStatus()
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
        notiRepo.getNotificationByUser(user.id)
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
