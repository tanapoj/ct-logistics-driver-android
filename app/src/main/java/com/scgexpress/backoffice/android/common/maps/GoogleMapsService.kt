package com.scgexpress.backoffice.android.common.maps

import com.akexorcist.googledirection.model.Direction
import com.google.gson.Gson
import com.scgexpress.backoffice.android.common.Const.GOOGLE_MAP_API_KEY
import com.scgexpress.backoffice.android.model.GoogleMapsDirectionResponse
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.IllegalStateException

class GoogleMapsService {

    private val client: OkHttpClient by lazy {
        OkHttpClient()
    }

    fun getDirection(
        sourceLat: String,
        sourceLng: String,
        destinationLat: String,
        destinationLng: String
    ): Single<GoogleMapsDirectionResponse> {

        val url = "https://maps.googleapis.com/maps/api/directions/json"

        val httpUrl = HttpUrl.parse(url)
            ?.newBuilder()
            ?.addQueryParameter("origin", "$sourceLat,$sourceLng")
            ?.addQueryParameter("destination", "$destinationLat,$destinationLng")
            ?.addQueryParameter("mode", "driving")
            ?.addQueryParameter("sensor", "true")
            ?.addQueryParameter("key", GOOGLE_MAP_API_KEY)
            ?.build() ?: return Single.error(IllegalStateException("Can't build Google Maps Url"))

        val request = Request.Builder()
            .url(httpUrl)
            .build()

        return Single.fromCallable {
            client.newCall(request).execute().body()?.string().let {
                Gson().fromJson(it, GoogleMapsDirectionResponse::class.java)
            }
        }
    }

    fun getDirectionOnMap(
        sourceLat: String,
        sourceLng: String,
        destinationLat: String,
        destinationLng: String
    ): Single<Direction> {
        val url = "https://maps.googleapis.com/maps/api/directions/json"

        val httpUrl = HttpUrl.parse(url)
            ?.newBuilder()
            ?.addQueryParameter("origin", "$sourceLat,$sourceLng")
            ?.addQueryParameter("destination", "$destinationLat,$destinationLng")
            ?.addQueryParameter("mode", "driving")
            ?.addQueryParameter("sensor", "true")
            ?.addQueryParameter("key", GOOGLE_MAP_API_KEY)
            ?.build() ?: return Single.error(IllegalStateException("Can't build Google Maps Url"))

        val request = Request.Builder()
            .url(httpUrl)
            .build()

        return Single.fromCallable {
            client.newCall(request).execute().body()?.string().let {
                Gson().fromJson(it, Direction::class.java)
            }
        }
    }
}

