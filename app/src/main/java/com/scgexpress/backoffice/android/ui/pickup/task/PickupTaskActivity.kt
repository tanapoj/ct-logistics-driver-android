package com.scgexpress.backoffice.android.ui.pickup.task

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.android.material.tabs.TabLayout
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.base.handleDrawerNavigation
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_ITEM_LIST
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_NEW_BOOKING
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanActivity
import com.scgexpress.backoffice.android.ui.pickup.task.item.PickupTaskCompletedFragment
import com.scgexpress.backoffice.android.ui.pickup.task.item.PickupTaskInProgressFragment
import com.scgexpress.backoffice.android.ui.pickup.task.item.PickupTaskNewFragment
import com.scgexpress.backoffice.android.ui.pickup.task.search.PickupSearchActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_pickup_task.*
import kotlinx.android.synthetic.main.custom_pickup_task_tab_layout.view.*
import java.util.*

class PickupTaskActivity : BaseActivity(), RejectStatusDialogFragment.OnStatusSelectedListener {

    private lateinit var viewModel: PickupTaskViewModel

    private var notificationIndicator = false

    private val pagerAdapter: PickupTaskItemsFragmentPagerAdapter by lazy {
        PickupTaskItemsFragmentPagerAdapter(this, viewModel, supportFragmentManager)
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var tabNames: List<Pair<String, Int>>
    var touchableList: ArrayList<View>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pickup_task)
        setSupportActionBar(toolbar)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PickupTaskViewModel::class.java)

        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                    renderTabTitle(tabNames, tab.position)

                }

                override fun onTabReselected(tab: TabLayout.Tab) {}

                override fun onTabUnselected(tab: TabLayout.Tab) {}
            })
        }
        touchableList = tabLayout?.touchables
        touchableList?.forEach { it.isEnabled = false }

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
        }

        setupDrawer(dlMenu, navMain)
        observeData()
        initFab()
    }

    @SuppressLint("InflateParams")
    private fun renderTabTitle(tabNames: List<Pair<String, Int>>, currentTabIndex: Int = -1) {
        //val tabNames = listOf("First" to 1, "Second" to 2, "Third" to 3)
        for ((i, value) in tabNames.withIndex()) {
            //Log.i("aaa", "render i=$i currentTabIndex=$currentTabIndex ${currentTabIndex == i}")
            val (name, count) = value
            val customTab = when (currentTabIndex) {
                i -> LayoutInflater.from(this).inflate(
                    R.layout.custom_pickup_task_tab_layout_active,
                    null
                )
                else -> LayoutInflater.from(this).inflate(
                    R.layout.custom_pickup_task_tab_layout,
                    null
                )
            }
            customTab.txtCount.text = "$count"
            customTab.txtTitle.text = name
            tabLayout.getTabAt(i)?.let {
                it.customView = null
                it.customView = customTab
            }
        }
    }

    private fun initFab() {
        fab.setOnClickListener {
            startActivity(Intent(this, PickupScanActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!viewModel.checkLastClickTime()) return false
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_search -> {
                val intent = Intent(this, PickupSearchActivity::class.java)
                    .putExtra(PARAMS_PICKUP_ITEM_LIST, viewModel.getData())
                startActivity(intent)
                false
            }
            R.id.menu_notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.initNotificationIndicator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        if (notificationIndicator) {
            inflater.inflate(R.menu.pickup_task_menu_with_indicator, menu)
        } else {
            inflater.inflate(R.menu.pickup_task_menu, menu)
        }
        return true
    }

    override fun onStatusSelected(rejectStatus: BookingRejectStatusModel, note: String) {
        viewModel.rejectBooking(rejectStatus, note)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            viewModel.requestItem()
        }
    }

    private fun setupDrawer(drawer: DrawerLayout, nav: NavigationView) {

        if (nav.headerCount > 0) {
            // stopship : please change to view model or something
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

            if (it.itemId == R.id.nav_menu_delivery) return@setNavigationItemSelectedListener false
            return@setNavigationItemSelectedListener handleDrawerNavigation(this, it.itemId)
        }
    }

    @Deprecated("")
    fun updatePageTitle(context: Context, pagePosition: Int, numItems: Int) {
//        val tab = tabLayout.getTabAt(pagePosition)!!.setCustomView(R.layout.custom_pickup_task_tab_layout)
//        val tabCount = tabLayout!!.getTabAt(pagePosition)!!.customView!!.findViewById<TextView>(R.id.txtCount)
//        val tabTitle = tabLayout!!.getTabAt(pagePosition)!!.customView!!.findViewById<TextView>(R.id.txtTitle)
//        //tab.text = myTabTitle(pagePosition, numItems)
//        tabCount.text = numItems.toString()
//        tabTitle.text = getTabTitle(pagePosition)
    }

    private fun getTabTitle(pagePosition: Int): String {
        return when (pagePosition) {
            0 -> getString(R.string.new_booking).toUpperCase(Locale.ENGLISH)
            1 -> getString(R.string.in_progress).toUpperCase(Locale.ENGLISH)
            2 -> getString(R.string.completed).toUpperCase(Locale.ENGLISH)
            else -> ""
        }
    }

    private fun observeData() {
        viewModel.notificationIndicator.observe(this, Observer {
            notificationIndicator = it ?: false
            invalidateOptionsMenu()
        })

        viewModel.tabTaskCounter.observe(this, Observer {
            tabNames = it.mapIndexed { index, counter ->
                getTabTitle(index) to counter
            }
            renderTabTitle(tabNames, viewPager.currentItem)
            touchableList?.forEach { t -> t.isEnabled = true }
        })
    }
}


class PickupTaskItemsFragmentPagerAdapter(
    private val context: Context, private val viewModel: PickupTaskViewModel, fm: FragmentManager
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        // stopship : add this
        return when (position) {
            0 -> PickupTaskNewFragment.newInstance(PARAMS_PICKUP_NEW_BOOKING, viewModel)
            1 -> PickupTaskInProgressFragment.newInstance(PARAMS_PICKUP_IN_PROGRESS, viewModel)
            2 -> PickupTaskCompletedFragment.newInstance(PARAMS_PICKUP_COMPLETED, viewModel)
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.new_booking).toUpperCase(Locale.ENGLISH)
            1 -> context.getString(R.string.in_progress).toUpperCase(Locale.ENGLISH)
            2 -> context.getString(R.string.completed).toUpperCase(Locale.ENGLISH)
            else -> null
        }
    }

}