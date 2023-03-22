package com.scgexpress.backoffice.android.ui.delivery.location

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Event
import com.scgexpress.backoffice.android.repository.delivery.DeliveryNetworkRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject


class DeliveryLocationViewModel @Inject constructor(
        application: Application,
        private val repoNetwork: DeliveryNetworkRepository) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val _snackbar = MutableLiveData<Event<String>>()
    val snackbar: LiveData<Event<String>>
        get() = _snackbar

    /*private val _result = MutableLiveData<DirectionsResult>()
    val result: LiveData<DirectionsResult>
        get() = _result*/

    //STOPSHIP mockup : Location map
    private val origin = LatLng(13.7684614, 100.569088)
    private val destination1 = LatLng(13.778759, 100.585548)
    private val destination2 = LatLng(13.7617408, 100.569088)

    fun requestDirection(directionCallback: DirectionCallback) {
        GoogleDirection.withServerKey(Const.GOOGLE_MAP_API_KEY)
                .from(origin)
                .to(destination1)
                .transportMode(TransportMode.DRIVING)
                .execute(directionCallback)
    }

    fun directionSuccess(googleMap: GoogleMap?, direction: Direction, rawBody: String) {
        if (direction.isOK) {
            val route = direction.routeList[0]
            googleMap!!.addMarker(MarkerOptions().position(origin))
            googleMap.addMarker(MarkerOptions().position(destination1))

            val directionPositionList = route.legList[0].directionPoint
            googleMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList, 5, Color.parseColor("#FF3E9AE9")))
            setCameraWithCoordinationBounds(googleMap, route)
        } else {
            showSnackbar(direction.status)
        }
    }

    private fun setCameraWithCoordinationBounds(googleMap: GoogleMap?, route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun showSnackbar(msg: String) {
        _snackbar.value = Event(msg)
    }

    //region fix later
    /*fun getDirection(googleMap: GoogleMap) {
        DirectionsApi.newRequest(getGeoContext())
        .origin(origin)
        .destination(destination)
        .mode(TravelMode.DRIVING)
        .setCallback(object : com.google.maps.PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Timber.i("Call Success")
                _result.postValue(result)
                //addMarkersToMap(result, googleMap)
                //addPolyline(result, googleMap)
            }

            override fun onFailure(e: Throwable) {
                Timber.e(e.message)
            }
        })
    }

    private fun getGeoContext(): GeoApiContext {
        return GeoApiContext.Builder().queryRateLimit(3)
                .apiKey(Const.GOOGLE_MAP_API_KEY)
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build()
    }

    fun addMarkersToMap(results: DirectionsResult, googleMap: GoogleMap) {
        val route = results.routes[0]
        googleMap!!.addMarker(MarkerOptions().position(origin))
        googleMap.addMarker(MarkerOptions().position(destination))

        val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
        googleMap.addPolyline(PolylineOptions().addAll(decodedPath))
        setCameraWithCoordinationBounds(googleMap, route)
    }

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable
    }*/
    //endregion
}