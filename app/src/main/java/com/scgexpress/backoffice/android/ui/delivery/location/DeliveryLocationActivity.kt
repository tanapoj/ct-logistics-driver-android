package com.scgexpress.backoffice.android.ui.delivery.location

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.model.Direction
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_delivery_location.*

class DeliveryLocationActivity : BaseActivity(), OnMapReadyCallback, DirectionCallback {

    private lateinit var viewModel: DeliveryLocationViewModel

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_location)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DeliveryLocationViewModel::class.java)

        getIntentData()
        initActionbar()
        initMap()
        observeData()
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
            R.id.menu_notification -> {
                // start notification activity
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        viewModel.requestDirection(this)
    }

    override fun onDirectionSuccess(direction: Direction, rawBody: String) {
        viewModel.directionSuccess(googleMap, direction, rawBody)
    }

    override fun onDirectionFailure(t: Throwable) {
        Snackbar.make(toolbar, "something wrong", Snackbar.LENGTH_SHORT).show()
    }

    private fun getIntentData() {
    }


    private fun initActionbar() {
        setSupportActionBar(toolbar)
        //STOPSHIP mockup : Location map title
        supportActionBar!!.title = "OFD101002"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun observeData() {
        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(toolbar, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        /*viewModelRetentionReason.result.observe(this, Observer { it ->
            if (it != null) {
                    viewModelRetentionReason.addMarkersToMap(it, googleMap!!)
            }
        })*/
    }
}
