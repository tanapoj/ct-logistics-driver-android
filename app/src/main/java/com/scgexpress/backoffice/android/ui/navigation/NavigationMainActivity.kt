package com.scgexpress.backoffice.android.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_ALL_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK
import com.scgexpress.backoffice.android.model.distance.AllTaskDistance
import com.scgexpress.backoffice.android.model.distance.DeliveryTaskDistance
import com.scgexpress.backoffice.android.model.distance.PickupTaskDistance
import com.scgexpress.backoffice.android.ui.navigation.map.NavigationMapActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationMainActivity : BaseActivity() {

    private val viewModel: NavigationMainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(NavigationMainViewModel::class.java)
    }

    private val mFragment by lazy {
        NavigationMainFragment.newInstance(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, mFragment)
                .commit()
        }
        initFab()
        initActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_retention, menu)
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

    private fun initFab() {
        fabMap.setOnClickListener {
            when (viewModel.type) {
                PARAMS_PICKUP_TASK -> {
                    setExtraPickup(
                        viewModel.latitude,
                        viewModel.longitude,
                        viewModel.directionParcelPickupList
                    )
                }
                PARAMS_DELIVERY_TASK -> {
                    setExtraDelivery(
                        viewModel.latitude,
                        viewModel.longitude,
                        viewModel.directionParcelDeliveryList
                    )
                }
                PARAMS_ALL_TASK -> {
                    setExtraAllTask(
                        viewModel.latitude,
                        viewModel.longitude,
                        viewModel.directionParcelAllTaskList
                    )
                }
            }
        }
    }

    private fun setExtraPickup(
        currentLat: Double,
        currentLng: Double,
        pickupList: ArrayList<PickupTaskDistance>
    ) {
        val intent = Intent(this, NavigationMapActivity::class.java)
        intent.putExtra("type", PARAMS_PICKUP_TASK).apply {
            putExtra("currentLat", currentLat)
            putExtra("currentLng", currentLng)
            putExtra("allTaskPickup", pickupList)
        }
        startActivity(intent)
    }

    private fun setExtraDelivery(
        currentLat: Double,
        currentLng: Double,
        deliveryList: ArrayList<DeliveryTaskDistance>
    ) {
        val intent = Intent(this, NavigationMapActivity::class.java)
        intent.putExtra("type", PARAMS_DELIVERY_TASK).apply {
            putExtra("currentLat", currentLat)
            putExtra("currentLng", currentLng)
            putExtra("allTaskDelivery", deliveryList)
        }
        startActivity(intent)
    }

    private fun setExtraAllTask(
        currentLat: Double,
        currentLng: Double,
        allTaskList: ArrayList<AllTaskDistance>
    ) {
        val intent = Intent(this, NavigationMapActivity::class.java)
        intent.putExtra("type", PARAMS_ALL_TASK).apply {
            putExtra("currentLat", currentLat)
            putExtra("currentLng", currentLng)
            putExtra("allTask", allTaskList)
        }
        startActivity(intent)
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Route"
        }
    }
}