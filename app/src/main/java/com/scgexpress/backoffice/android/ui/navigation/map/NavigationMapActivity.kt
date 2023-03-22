package com.scgexpress.backoffice.android.ui.navigation.map

import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.ui.navigation.NavigationMainViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_navigation_map.toolbar
import java.lang.IllegalStateException

class NavigationMapActivity : BaseActivity(), OnMapReadyCallback {

    private val viewModel: NavigationMainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(NavigationMainViewModel::class.java)
    }

    private var mapFragment: SupportMapFragment? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_map)
        viewModel.getLatLngCurrentLocation()
        initIntent(intent)
        initMap()
        initObserve()
        initActionBar()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.isMyLocationEnabled = true
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocationChange(locationManager)
        viewModel.requestDirection()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        locationManager.removeUpdates(locationListener)
        super.onStop()
    }

    private fun getLocationChange(
        locationManager: LocationManager
    ) {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location?) {
                viewModel.latitude = location!!.latitude
                viewModel.longitude = location.longitude
                viewModel.requestDirection()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                3000,
                50f,
                locationListener
            )
        } catch (ex: SecurityException) {
            throw IllegalStateException(ex)
        }
    }

    private fun initObserve(){
        viewModel.drawPolyLine.observe(this, Observer {directionPositionList ->
            if (directionPositionList == null) return@Observer

            googleMap.addPolyline(
                DirectionConverter.createPolyline(
                    this,
                    ArrayList(directionPositionList),
                    5,
                    this.resources.getColor(R.color.polylineBlue)
                )
            )
        })

        viewModel.moveCamera.observe(this, Observer {
            if (it == null) return@Observer
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
        })

        viewModel.marker.observe(this, Observer {
            if (it == null) return@Observer
            googleMap.addMarker(
                MarkerOptions().position(it.latLng).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        it.bitmap
                    )
                ).title(it.title)
            )
        })
    }

    private fun initIntent(intent: Intent){
        viewModel.latitude = intent.getDoubleExtra("currentLat", .0)
        viewModel.longitude = intent.getDoubleExtra("currentLng", .0)
        when (intent.getStringExtra("type")) {
            Const.PARAMS_PICKUP_TASK -> {
                viewModel.type = intent.getStringExtra("type")
                viewModel.directionParcelPickupList =
                    intent.getParcelableArrayListExtra("allTaskPickup")
            }
            Const.PARAMS_DELIVERY_TASK -> {
                viewModel.type = intent.getStringExtra("type")
                viewModel.directionParcelDeliveryList =
                    intent.getParcelableArrayListExtra("allTaskDelivery")
            }
            Const.PARAMS_ALL_TASK -> {
                viewModel.type = intent.getStringExtra("type")
                viewModel.directionParcelAllTaskList = intent.getParcelableArrayListExtra("allTask")
            }
        }
    }

    private fun initMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Route"
        }
    }
}