package com.scgexpress.backoffice.android.ui.pickup.task.item

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_TAB_TYPE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.REQUEST_PICKUP_TASK_REFRESH
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.ui.pickup.detail.PickupDetailsActivity
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskAdapter
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_task_items.*
import javax.inject.Inject


class PickupTaskInProgressFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: PickupTaskViewModel
        fun newInstance(type: String, vm: PickupTaskViewModel): PickupTaskInProgressFragment {
            viewModel = vm
            return PickupTaskInProgressFragment().also {
                val args = Bundle()
                args.putString(PARAMS_MANIFEST_TAB_TYPE, type)
                it.arguments = args
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var pickupPreference: PickupPreference

    private lateinit var mContext: PickupTaskActivity
    private lateinit var rootView: View

    private val adapter: PickupTaskAdapter by lazy {
        PickupTaskAdapter(viewModel)
    }

    private val refreshObserver =
        Observer<Boolean> {
            srlItems.isRefreshing = it!!
        }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupTaskActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupTaskActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =
            inflater.inflate(R.layout.fragment_pickup_task_items, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()
        initRecyclerView()
        initRefreshLayout()
        observeData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                /*val intent = Intent(mContext, PickupSearchActivity::class.java)
                    .putExtra(PARAMS_MANIFEST_ITEM_LIST, viewModelRetentionReason.getData())
                startActivity(intent)*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadData() {
        viewModel.requestItem()
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun observeData() {
        viewModel.inProgressPickupTask.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_PICKUP_IN_PROGRESS) {
                adapter.data = it
                adapter.notifyDataSetChanged()
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(context!!, 1, it.size)
            }
        })

        viewModel.itemClick.observe(this, Observer {
            if (it == null) return@Observer
            if (it.status != PARAMS_PICKUP_TASK_IN_PROGRESS) return@Observer
            val intent = Intent(context, PickupDetailsActivity::class.java)
                .putExtra(PARAMS_PICKUP_TASK_ID, it.id)
            startActivityForResult(intent, REQUEST_PICKUP_TASK_REFRESH)
            viewModel.clearItemClick()
        })

        viewModel.actionPickup.observe(this, Observer { task ->
            Intent(context, PickupMainActivity::class.java).apply {
                pickupPreference.currentScanningTaskIdList = null
                putExtra(PARAMS_PICKUP_TASK_ID, task.id)
            }.also {
                startActivity(it)
            }
        })

        viewModel.phoneCall.observe(this, Observer {
            if (it == null) return@Observer
            Utils.phoneCall(this.activity!!, it)
        })

        viewModel.location.observe(this, Observer {
            if (it == null) return@Observer
            val gmmIntentUri =
                Uri.parse("google.navigation:q=" + it.latitude + "," + it.longitude + "&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        })

        viewModel.alertMessage.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    activity!!.showAlertMessage(it)
                }
            }
        })
    }

    private fun initRefreshLayout() {
        srlItems.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorAccent
            )
        )
        srlItems.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing
            .observe(this, refreshObserver)
    }
}