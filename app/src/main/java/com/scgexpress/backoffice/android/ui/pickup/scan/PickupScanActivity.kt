package com.scgexpress.backoffice.android.ui.pickup.scan

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.handleDrawerNavigation
import com.scgexpress.backoffice.android.common.Const
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_topic.*
import javax.inject.Inject

class PickupScanActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val fragment: PickupScanFragment by lazy {
        PickupScanFragment.newInstance()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        initActionBar()
        initDrawer(dlMenu, navigation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.REQUEST_CODE_PICKUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Pickup"
        }
    }

    private fun initDrawer(drawer: DrawerLayout, nav: NavigationView) {

        if (nav.headerCount > 0) {
            // stopship : please change to viewmodel or something
            nav.getHeaderView(0).findViewById<TextView>(R.id.tvTitle)?.text = getString(R.string.this_is_title)
            nav.getHeaderView(0).findViewById<TextView>(R.id.tvSubTitle)?.text = getString(R.string.this_is_subtitle)
        }

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.open_drawer,
            R.string.close_drawer
        )

        nav.setNavigationItemSelectedListener {

            drawer.closeDrawer(GravityCompat.START)

            //if (it.itemId == R.id.nav_menu_one) return@setNavigationItemSelectedListener false
            return@setNavigationItemSelectedListener handleDrawerNavigation(this, it.itemId)
        }

    }
}
