package com.scgexpress.backoffice.android.repository

import android.annotation.SuppressLint
import com.scgexpress.backoffice.android.api.LoginService
import com.scgexpress.backoffice.android.model.Identity
import com.scgexpress.backoffice.android.model.User
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.http.Url
import timber.log.Timber
import javax.inject.Inject

class LoginRepository @Inject constructor(private val service: LoginService) {

    fun login(username: String, password: String): Flowable<User> {
        val map: HashMap<String, Any> = hashMapOf()
        map["username"] = username
        map["password"] = password

        return service.login(map)
    }

    fun login(code: String): Flowable<User> {
        val map: HashMap<String, Any> = hashMapOf()
        map["personal_id"] = code
        return service.login2(map)
    }
    
    @SuppressLint("CheckResult")
    fun logout() {
        Timber.d("logout!")
        service.logout().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("service logout!")
                },
                        {
                            if (it is NotImplementedError) {
                                Timber.e(it)
                            }
                        })
    }

    fun registerDevice(deviceToken: String): Flowable<User> {
        val map: HashMap<String, Any> = hashMapOf()
        map["deviceToken"] = deviceToken

        return service.registerDevice(map)
    }

    fun getIdentity(): Call<Identity> {
        return service.getIdentity()
    }

    fun sendRequest(@Url url: String): Call<User> {
        return service.sendRequest(url)
    }

    fun postRequest(@Url url: String): Call<User> {
        return service.postRequest(url)
    }
}