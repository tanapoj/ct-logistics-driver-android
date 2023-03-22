package com.scgexpress.backoffice.android.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*


class LocationHelper private constructor(appContext: Context) : LiveData<Location>() {

    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)
    private var mLocationRequest: LocationRequest? = null

    private var mLocationCallback: LocationCallback? = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null)
                    value = location
            }
        }
    }

    init {
        createLocationRequest()
        val permission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.lastLocation!!.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    value = task.result
                } else {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback!!, null)
                }
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 10000
        mLocationRequest!!.fastestInterval = 5000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onInactive() {
        super.onInactive()
        if (mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback!!)
    }

    companion object {

        private var instance: LocationHelper? = null

        fun getInstance(appContext: Context): LocationHelper {
            if (instance == null) {
                instance = LocationHelper(appContext)
            }
            return instance as LocationHelper
        }
    }

}