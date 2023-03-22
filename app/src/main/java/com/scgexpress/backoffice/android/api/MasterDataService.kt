package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.model.MasterDataVersion
import io.reactivex.Flowable
import retrofit2.http.GET

interface MasterDataService {

    @GET("masterDataVersion")
    fun getVersion(): Flowable<MasterDataVersion>
}