package com.scgexpress.backoffice.android.ui.delivery.ofd.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.handleDrawerNavigation
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_IN_PROGRESS
import com.scgexpress.backoffice.android.model.Manifest
import com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent.OfdCantSentActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.OfdDetailItemsFragment
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentActivity
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_ofd_detail.*
import kotlinx.android.synthetic.main.section_summary.*
import timber.log.Timber
import kotlin.math.abs

class OfdDetailActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OfdDetailViewModel::class.java)
    }

    private val pagerAdapter: OfdDetailFragmentPagerAdapter by lazy {
        OfdDetailFragmentPagerAdapter(this, supportFragmentManager, manifest!!.id!!)
    }

    private val onOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

        val total = appBarLayout.totalScrollRange
        val offsetRatio = abs(verticalOffset / total.toFloat())

        val height = clSummary.height
        //clSummary.scaleY = 1 - offsetRatio

        val top = offsetRatio * height
        clSummary.translationY = top

        actionbarTitle.translationY = -verticalOffset.toFloat()

        Timber.d("Ratio $offsetRatio , ${((1 - offsetRatio) * height / 2f).toInt()}")
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private var manifest: Manifest? = Manifest()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ofd_detail)
        setSupportActionBar(toolbar)

        getIntentHeader()

        vpDeliver.adapter = pagerAdapter
        tlDisplay.setupWithViewPager(vpDeliver)

        actionbarTitle.text = manifest?.barcode ?: ""
        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
            this.title = ""
        }

        setupDrawer(dlMenu, navMain)
        observeData()
    }

    override fun onResume() {
        super.onResume()
        loadHeader()
    }

    private fun loadHeader() {
        if (manifest != null) {
            viewModel.loadHeader(manifest!!.id!!)
        }
    }

    override fun onStart() {
        super.onStart()
        appbar.addOnOffsetChangedListener(onOffsetChangeListener)
    }

    override fun onStop() {
        super.onStop()
        appbar.removeOnOffsetChangedListener(onOffsetChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!viewModel.checkLastClickTime()) return false
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_search -> {
                false
            }
            R.id.menu_ofd -> {
                false
            }
            R.id.menu_booking -> {
                false
            }
            R.id.menu_all -> {
                false
            }
            R.id.menu_scan_ofd -> {
                val intent = Intent(this, OfdScanActivity::class.java)
                    .putExtra(PARAMS_MANIFEST, manifest)
                startActivity(intent)
                true
            }
            R.id.menu_sent -> {
                val intent = Intent(this, OfdSentActivity::class.java)
                    .putExtra(Const.PARAMS_MANIFEST_ID, manifest!!.id)
                startActivity(intent)
                true
            }
            R.id.menu_retention -> {
                val intent = Intent(this, OfdCantSentActivity::class.java)
                    .putExtra(Const.PARAMS_MANIFEST_ID, manifest!!.id)
                startActivity(intent)
                true
            }
            /*R.id.menu_reorder -> {
                startActivity(Intent(this, OfdDetailItemsDraggableActivity::class.java))
                true
            }*/
            R.id.menu_notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.ofd_detail_menu, menu)
        return true
    }

    private fun setupDrawer(drawer: DrawerLayout, nav: NavigationView) {

        if (nav.headerCount > 0) {

            // stopship : please change to view model or something
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

            if (it.itemId == R.id.nav_menu_delivery) return@setNavigationItemSelectedListener false
            return@setNavigationItemSelectedListener handleDrawerNavigation(this, it.itemId)
        }
    }

    private fun observeData() {
        viewModel.dataHeader.observe(this, Observer { it ->
            if (it == null) return@Observer
            setHeader(it)
        })
    }

    fun updatePageTitle(pagePosition: Int, numItems: Int) {
        val tab = tlDisplay.getTabAt(pagePosition)
        tab?.text = myCalcTabTitle(pagePosition, numItems)
    }

    private fun myCalcTabTitle(pagePosition: Int, numItems: Int): String {
        var itemCount = ""
        if (numItems > 0) {
            itemCount = " ($numItems)"
        }
        return when (pagePosition) {
            0 -> getString(R.string.in_progress).toUpperCase() + itemCount
            1 -> getString(R.string.completed).toUpperCase() + itemCount
            else -> ""
        }
    }

    private fun getIntentHeader() {
        manifest = intent.getSerializableExtra(PARAMS_MANIFEST) as? Manifest
    }

    private fun setHeader(item: Manifest) {
        try {
            tvRemain.text = (item.bookingsTotal!!.toInt() -
                    item.bookingsDone!!.toInt()).toString()
            tvOfdRemain.text = (item.noOfItemsTotal!!.toInt() -
                    (item.noOfItemsDelivered!!.toInt() + item.noOfItemsRetention!!.toInt())).toString()
        } catch (e: Exception) {
            tvRemain.text = 0.toString()
            tvOfdRemain.text = 0.toString()
        }

        tvPicked.text = item.bookingsDone
        tvTotal.text = item.bookingsTotal

        tvDeliveryDelivered.text = item.noOfItemsDelivered
        tvDeliveryRetention.text = item.noOfItemsRetention
        tvDeliveryTotal.text = item.noOfItemsTotal
    }

    fun getOfdManifestBarcode(): String {
        return manifest?.barcode ?: ""
    }
}


class OfdDetailFragmentPagerAdapter(private val context: Context, fm: FragmentManager, private val manifestID: String) :
    FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        // stopship : add this
        return when (position) {
            0 -> OfdDetailItemsFragment.newInstance(manifestID, PARAMS_MANIFEST_IN_PROGRESS)
            1 -> OfdDetailItemsFragment.newInstance(manifestID, PARAMS_MANIFEST_COMPLETED)
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.in_progress).toUpperCase()
            1 -> context.getString(R.string.completed).toUpperCase()
            else -> null
        }
    }

}