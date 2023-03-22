package com.scgexpress.backoffice.android.ui.pickup.summary

import android.content.Intent
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
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_TAG_DIALOG_PHOTO_SELECT
import com.scgexpress.backoffice.android.common.Const.REQUEST_CODE_PICKUP
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.PhotoTitle
import com.scgexpress.backoffice.android.ui.dialog.PhotoExpandDialogFragment
import com.scgexpress.backoffice.android.ui.dialog.PhotoSelectDialogFragment
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_pickup_summary.*


class PickupSummaryActivity : BaseActivity(),
    PhotoSelectDialogFragment.OnOptionSelectedListener {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val viewModel: PickupSummaryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickupSummaryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pickup_summary)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
        }

        getIntentResult()

        setupDrawer(dlMenu, navMain)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, PickupSummaryFragment.newInstance(viewModel))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICKUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            } else {
                viewModel.setDone(true)
            }
        }
    }

    override fun onStatusSelected(photo: PhotoTitle) {
        viewPhoto(photo.photoStored)
    }

    private fun setupDrawer(drawer: DrawerLayout, nav: NavigationView) {

        if (nav.headerCount > 0) {

            // stopship : please change to viewmodel or something
            nav.getHeaderView(0).findViewById<TextView>(R.id.tvTitle)?.text =
                viewModel.user.personalId
            nav.getHeaderView(0).findViewById<TextView>(R.id.tvSubTitle)?.text =
                viewModel.user.branchCode
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
        intent.getStringExtra(Const.PARAMS_PICKUP_TASK_ID)?.let {
            viewModel.taskId = it
        }

        intent.getIntExtra(Const.PARAMS_PICKUP_TASK_TOTAL_COUNT,0).let {
            viewModel.taskTotalCount = it
        }
    }

    private fun viewPhoto(photoStored: PhotoStored?) {
        if (photoStored == null) return
        val fragment = PhotoExpandDialogFragment.newInstance(photoStored)
        fragment.show(supportFragmentManager, PARAMS_TAG_DIALOG_PHOTO_SELECT)
    }
}
