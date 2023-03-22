package com.scgexpress.backoffice.android.ui.delivery

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.handleDrawerNavigation
import com.scgexpress.backoffice.android.common.Const.PARAMS_DIALOG_DATE_RANGE_PICKER
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_BOOKING
import com.scgexpress.backoffice.android.common.toDateFormat
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.ui.delivery.booking.NewBookingsFragment
import com.scgexpress.backoffice.android.ui.delivery.ofd.OfdManifestFragment
import com.scgexpress.backoffice.android.ui.dialog.DateRangePickerDialog
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_delivery_main.*
import kotlinx.android.synthetic.main.activity_menu.dlMenu
import kotlinx.android.synthetic.main.activity_menu.navMain
import kotlinx.android.synthetic.main.activity_menu.toolbar
import java.util.*


class DeliveryMainActivity : BaseActivity(), RejectStatusDialogFragment.OnStatusSelectedListener,
    DateRangePickerDialog.OnDateSelectedListener {

    private val pagerAdapter: DeliveryFragmentPagerAdapter by lazy {
        DeliveryFragmentPagerAdapter(this, viewModel, supportFragmentManager)
    }

    private lateinit var viewModel: DeliveryViewModel

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private var isNewBooking: Boolean = false

    private val current: Calendar by lazy {
        Calendar.getInstance()
    }

    private var startDate: Calendar = current
    private var endDate: Calendar = current


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delivery_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DeliveryViewModel::class.java)

        getIntentResult()

        vpDeliver.adapter = pagerAdapter
        tlDisplay.setupWithViewPager(vpDeliver)

        if (isNewBooking) {
            val tab = tlDisplay.getTabAt(1)
            tab!!.select()
        }

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
            this.subtitle = getString(R.string.today)
        }

        setupDrawer(dlMenu, navMain)
        observeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData(viewModel.startDate, viewModel.endDate)
    }

    private fun observeData() {
        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(vpDeliver, it, Snackbar.LENGTH_LONG).show()
                }
            }
        })
        viewModel.ofdManifest.observe(this, Observer {
            if (it == null) return@Observer
            updatePageTitle(0, it.size)
        })

        viewModel.newBookings.observe(this, Observer {
            if (it == null) return@Observer
            updatePageTitle(1, it.size)
        })
    }

    private fun updatePageTitle(pagePosition: Int, numItems: Int) {
        val tab = tlDisplay.getTabAt(pagePosition)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tab?.text = Html.fromHtml(myCalcTabTitle(pagePosition, numItems), Html.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun myCalcTabTitle(pagePosition: Int, numItems: Int): String {
        var itemCount = ""
        if (numItems > 0) {
            itemCount = " ($numItems)"
        }
        return when (pagePosition) {
            0 -> getString(R.string.ofd_manifest).toUpperCase() + itemCount
            1 -> getString(R.string.new_bookings).toUpperCase() + "<font color=\'#DF0C1B\'> $itemCount</font>"
            else -> ""
        }
    }

    private fun getIntentResult() {
        isNewBooking = intent.getBooleanExtra(PARAMS_MANIFEST_BOOKING, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!viewModel.checkLastClickTime()) return false
        return when (item.itemId) {

            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_date_picker -> {
                val fragment = DateRangePickerDialog.newInstance(startDate, endDate)
                fragment.show(supportFragmentManager, PARAMS_DIALOG_DATE_RANGE_PICKER)
                true
            }
            R.id.menu_notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_delivery, menu)
        return true
    }

    override fun onStatusSelected(rejectStatus: BookingRejectStatusModel, note: String) {
        viewModel.rejectBooking(rejectStatus, note)
    }

    override fun onDateSelected(startDate: Calendar, endDate: Calendar) {
        this.startDate = startDate
        this.endDate = endDate
        val strStart = this.startDate.time.toDateFormat()
        val strEnd = this.endDate.time.toDateFormat()
        val date = if (strStart == strEnd) {
            if (strStart == current.time.toDateFormat())
                getString(R.string.today)
            else
                strStart
        } else {
            "$strStart to $strEnd"
        }
        toolbar.subtitle = date
        viewModel.loadData(strStart, strEnd)
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
            com.scgexpress.backoffice.android.R.string.open_drawer,
            com.scgexpress.backoffice.android.R.string.close_drawer
        )

        nav.setNavigationItemSelectedListener {

            drawer.closeDrawer(GravityCompat.START)

            if (it.itemId == R.id.nav_menu_delivery) return@setNavigationItemSelectedListener false
            return@setNavigationItemSelectedListener handleDrawerNavigation(this, it.itemId)
        }
    }
}

class DeliveryFragmentPagerAdapter(
    private val context: Context,
    private val viewModel: DeliveryViewModel,
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OfdManifestFragment.newInstance(viewModel)
            1 -> NewBookingsFragment.newInstance(viewModel) // stopship : this should be new bookings
            else -> Fragment()
        }
    }

    // This determines the title for each tab
    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return when (position) {
            0 -> context.getString(R.string.ofd_manifest).toUpperCase()
            1 -> context.getString(R.string.new_bookings).toUpperCase()
            else -> null
        }
    }
}