package com.scgexpress.backoffice.android.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.handleDrawerNavigation
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_topic.dlMenu
import kotlinx.android.synthetic.main.activity_topic.toolbar

class MenuActivity : BaseActivity() {


    private var notificationIndicator = false

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MenuViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

        setupDrawer(dlMenu, navMain)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, MenuFragment.newInstance())
                .commit()
        }

        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, 0)

        observeData()
    }

    private fun setupDrawer(drawer: DrawerLayout, nav: NavigationView) {

        if (nav.headerCount > 0) {

            // stopship : please change to viewmodel or something
            nav.getHeaderView(0).findViewById<TextView>(R.id.tvTitle)?.text = viewModel.user.personalId
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

            if (it.itemId == R.id.nav_menu_dashboard) return@setNavigationItemSelectedListener false
            return@setNavigationItemSelectedListener handleDrawerNavigation(this, it.itemId) {
                logout()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.initNotificationIndicator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        if (notificationIndicator) {
            inflater.inflate(R.menu.menu_with_notification_indicator, menu)
        } else {
            inflater.inflate(R.menu.menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!viewModel.checkLastClickTime()) return false
        return when (item.itemId) {
            android.R.id.home -> {
                dlMenu.openDrawer(GravityCompat.START)
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


    private fun observeData() {
        viewModel.notificationIndicator.observe(this, Observer {
            notificationIndicator = it ?: false
            invalidateOptionsMenu()
        })
    }


    private fun logout() {
        viewModel.logout()
    }
}
