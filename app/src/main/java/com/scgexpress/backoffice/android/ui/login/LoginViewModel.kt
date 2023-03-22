package com.scgexpress.backoffice.android.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.exception.NoConnectivityException
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginViewModel @Inject constructor(
        application: Application,
        private val repository: LoginRepository,
        private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private val _isLogin: MutableLiveData<Boolean> = MutableLiveData()
    val isLogin: LiveData<Boolean>
        get() = _isLogin

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    fun login(username: String, password: String) {
        addDisposable(repository.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        saveLoginStatus(it)

                        _isLogin.value = true
                        showSnackbar("Login Successful!")
                    }else{
                        showSnackbar("Login failed, Please try again.")
                    }
                }
                ) {
                    if (it is NoConnectivityException)
                        showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                    else
                        showSnackbar("Login failed, Please try again.")
                })
    }

    fun login(code: String) {
        repository.login(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        saveLoginStatus(it)

                        _isLogin.value = true
                        showSnackbar("Login Successful!")
                    } else {
                        showSnackbar("Login failed, Please try again.")
                    }
                }, {
                    if (it is NoConnectivityException)
                        showSnackbar(context.getString(R.string.there_is_on_internet_connection))
                    else
                        showSnackbar("Login failed, Please try again.")
                })
                .also {
                    addDisposable(it)
                }
    }

    fun isLogin(): Boolean {
        return user.accessToken != ""
    }

    private fun saveLoginStatus(user: User) {
        val jwtUser = Utils.convertStringToUser(Utils.decoded(user.accessToken))
        jwtUser.accessToken = user.accessToken
        loginPreference.loginUser = Utils.convertObjectToString(jwtUser)
    }

    private fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }
}