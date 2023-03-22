package com.scgexpress.backoffice.android.ui.delivery.task

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_ITEM_LIST
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.OfdSentActivity
import com.scgexpress.backoffice.android.ui.delivery.retention.reason.RetentionReasonActivity
import com.scgexpress.backoffice.android.ui.delivery.task.item.DeliveryTaskCompletedFragment
import com.scgexpress.backoffice.android.ui.delivery.task.item.DeliveryTaskInProgressFragment
import com.scgexpress.backoffice.android.ui.delivery.task.search.DeliverySearchActivity
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_pickup_task.*
import kotlinx.android.synthetic.main.custom_pickup_task_tab_layout.view.*
import java.util.*

class DeliveryTaskActivity : BaseActivity() {

    private lateinit var viewModel: DeliveryTaskViewModel

    private var notificationIndicator = false

    private val pagerAdapter: DeliveryTaskItemsFragmentPagerAdapter by lazy {
        DeliveryTaskItemsFragmentPagerAdapter(this, viewModel, supportFragmentManager)
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var tabNames: List<Pair<String, Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delivery_task)
        setSupportActionBar(toolbar)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DeliveryTaskViewModel::class.java)

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

        fun AlertDialog.setDivider(): AlertDialog {
            window?.apply {
                listView.apply {
                    divider = ColorDrawable(Color.GRAY)
                    dividerHeight = 1
                }
            }
            return this
        }

        fun AlertDialog.display(): AlertDialog {
            show()
            return this
        }

        fun AlertDialog.atBottomRight(): AlertDialog {
            window?.apply {
                WindowManager.LayoutParams().apply {
                    copyFrom(attributes)
                    gravity = Gravity.BOTTOM or Gravity.END
                }.also {
                    attributes = it
                }
                setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            return this
        }

        fab.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setItems(arrayOf("Scan Delivery", "Scan Retention", "Scan OFD")) { _, idx ->
                    when (idx) {
                        0 -> Intent(this@DeliveryTaskActivity, OfdSentActivity::class.java)
                        1 -> Intent(this@DeliveryTaskActivity, RetentionReasonActivity::class.java)
                        2 -> Intent(this@DeliveryTaskActivity, OfdScanActivity::class.java)
                        else -> throw IllegalStateException()
                    }.also {
                        startActivity(it)
                    }
                }
            }.run {
                create()
            }.also {
                it.setDivider().display().atBottomRight()
            }

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
                val intent = Intent(this, DeliverySearchActivity::class.java)
                    .putExtra(PARAMS_DELIVERY_ITEM_LIST, viewModel.getData())
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
            0 -> getString(R.string.in_progress).toUpperCase(Locale.ENGLISH)
            1 -> getString(R.string.completed).toUpperCase(Locale.ENGLISH)
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
        })
    }
}


class DeliveryTaskItemsFragmentPagerAdapter(
    private val context: Context, private val viewModel: DeliveryTaskViewModel, fm: FragmentManager
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        // stopship : add this
        return when (position) {
            0 -> DeliveryTaskInProgressFragment.newInstance(
                PARAMS_DELIVERY_TASK_IN_PROGRESS,
                viewModel
            )
            1 -> DeliveryTaskCompletedFragment.newInstance(
                PARAMS_DELIVERY_TASK_COMPLETED,
                viewModel
            )
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.in_progress).toUpperCase(Locale.ENGLISH)
            1 -> context.getString(R.string.completed).toUpperCase(Locale.ENGLISH)
            else -> null
        }
    }

}