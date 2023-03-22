package com.scgexpress.backoffice.android.ui.pickup.receipt

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.handleDrawerNavigation
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_PAYMENT
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_pickup_receipt.*

class PickupReceiptActivity : BaseActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var viewModel: PickupReceiptViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickup_receipt)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PickupReceiptViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
        }

        getIntentResult()

        setupDrawer(dlMenu, navMain)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, PickupReceiptFragment.newInstance(viewModel))
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupDrawer(drawer: DrawerLayout, nav: NavigationView) {

        if (nav.headerCount > 0) {

            // stopship : please change to viewmodel or something
            nav.run { getHeaderView(0).findViewById<TextView>(R.id.tvTitle) }?.text = viewModel.user.personalId
            nav.getHeaderView(0).findViewById<TextView>(R.id.tvSubTitle)?.text = viewModel.user.branchCode
        }

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.open_drawer,
            R.string.close_drawer
        )

        nav.setNavigationItemSelectedListener {

            drawer.closeDrawer(GravityCompat.START)

            if (it.itemId == R.id.nav_menu_pickup) return@setNavigationItemSelectedListener false
            return@setNavigationItemSelectedListener handleDrawerNavigation(this, it.itemId)
        }

    }

    private fun getIntentResult() {
        intent.getStringExtra(PARAMS_PICKUP_TASK_ID)?.let {
            viewModel.taskId = it
        }

        intent.getStringExtra(PARAMS_PICKUP_PAYMENT)?.let {
            viewModel.payment = it
        }
//
//        intent.getStringExtra(PARAMS_GROUP_ID)?.let {
//            viewModelRetentionReason.groupID = it
//        }
    }
}
