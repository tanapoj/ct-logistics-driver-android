package com.scgexpress.backoffice.android.common.maps

import com.akexorcist.googledirection.model.Direction
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GoogleMapsHelper {
    companion object {
        val instance: GoogleMapsHelper by lazy {
            GoogleMapsHelper()
        }
    }

    fun getDistance(source: LatLngLocation, destination: LatLngLocation): Single<LocationDistance> {
        return GoogleMapsService().getDirection(
            source.lat,
            source.lng,
            destination.lat,
            destination.lng
        ).subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread()).map {
            val value = it.routes.firstOrNull()?.legs?.firstOrNull()?.distance?.value ?: .0
            LocationDistance(value, "meters")
        }
    }

    fun getDirection(source: LatLngLocation, destination: LatLngLocation): Single<Direction> {
        return GoogleMapsService().getDirectionOnMap(
            source.lat,
            source.lng,
            destination.lat,
            destination.lng
        ).subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread())
    }
}

data class LatLngLocation(
    val lat: String,
    val lng: String
)

data class LocationDistance(
    val distance: Double,
    val unit: String
)