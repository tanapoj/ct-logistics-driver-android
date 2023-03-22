package com.scgexpress.backoffice.android.ui.login

import android.app.Application
import android.content.Context
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.FileHelper
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.config.FirebaseRemoteConfig
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject

class PinViewModel @Inject constructor(
        application: Application,
        private val remoteConfig: FirebaseRemoteConfig,
        private val repository: LoginRepository,
        private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val fileHelper = FileHelper.helper

    val MAX_PIN = 4

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _pinCode: MutableLiveData<String> = MutableLiveData<String>().apply { postValue("") }
    val pinCode: LiveData<String>
        get() = _pinCode

    private val _pass: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(false) }
    val pass: LiveData<Boolean>
        get() = _pass

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    fun init() {
        Thread {
            fileHelper.clearAllExpiredFiles()
        }.start()
    }

    fun addPinNumber(number: Int) {
        _pinCode.value?.let {
            if (it.length < MAX_PIN) {
                _pinCode.value += number
                if (_pinCode.value?.length == MAX_PIN) {
                    Handler().postDelayed({
                        verifyPin(_pinCode.value!!)
                    }, 10)
                }
            }
        }
    }

    fun removeLastedPinNumber() {
        _pinCode.value?.let {
            if (!it.isEmpty()) {
                _pinCode.value = it.substring(0, it.length - 1)
            }
        }
    }

    private fun verifyPin(code: String) {
        remoteConfig.pinCode.value?.let {
            val match = it == code
            _pass.value = match
            if (!match) {
                _pinCode.value = ""
                _snackbar.value = Event("Pin Code Incorrect, Please try again.")
            }
        }
    }

    fun isLogin() = user.accessToken != ""
}