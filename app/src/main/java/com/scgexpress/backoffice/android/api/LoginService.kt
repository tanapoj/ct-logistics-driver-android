package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.model.Identity
import com.scgexpress.backoffice.android.model.User
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


interface LoginService {
    @POST("login")
    fun login(@Body body: HashMap<String, Any>): Flowable<User>

    @POST("login2")
    fun login2(@Body body: HashMap<String, Any>): Flowable<User>

    @POST("registerDevice")
    fun registerDevice(@Body body: HashMap<String, Any>): Flowable<User>

    @POST("logout")
    fun logout(): Flowable<User>

    @GET("getIdentity")
    fun getIdentity(): Call<Identity>

    @GET
    fun sendRequest(@Url url: String): Call<User>

    @POST
    fun postRequest(@Url url: String): Call<User>
}
