package com.scgexpress.backoffice.android.ui.delivery.task.item

import android.content.Context
import android.content.Intent
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
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_TAB_TYPE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.ui.delivery.detail.TrackingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskActivity
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskAdapter
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_delivery_task_items.*
import kotlinx.android.synthetic.main.fragment_pickup_task_items.rvItems
import kotlinx.android.synthetic.main.fragment_pickup_task_items.srlItems
import javax.inject.Inject


class DeliveryTaskCompletedFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: DeliveryTaskViewModel
        fun newInstance(type: String, vm: DeliveryTaskViewModel): DeliveryTaskCompletedFragment {
            viewModel = vm
            return DeliveryTaskCompletedFragment().also {
                val args = Bundle()
                args.putString(PARAMS_MANIFEST_TAB_TYPE, type)
                it.arguments = args
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: DeliveryTaskActivity
    private lateinit var rootView: View

    private val adapter: DeliveryTaskAdapter by lazy {
        DeliveryTaskAdapter(viewModel)
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

        if (context is DeliveryTaskActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + DeliveryTaskActivity::class.java.simpleName)
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
            inflater.inflate(R.layout.fragment_delivery_task_items, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hideReverseBar()
        loadData()
        initRecyclerView()
        initRefreshLayout()
        observeData()
    }

    private fun hideReverseBar() {
        containerReverse.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                /*val intent = Intent(mContext, DeliverySearchActivity::class.java)
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
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.layoutManager = layoutManager
        rvItems.adapter = adapter
    }

    private fun observeData() {
        viewModel.completedDeliveryTask.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_DELIVERY_TASK_COMPLETED) {

                /*val d: ArrayList<Any> = arrayListOf(Title(""))
                d.addAll(viewModelRetentionReason.setServiceAndSizeName(item.submitTrackings))
                adapter.data = d*/

                adapter.data = it
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(context!!, 2, it.size)
            }
        })

        viewModel.itemClick.observe(this, Observer {
            if (it == null) return@Observer
            if (it.deliveryStatus != PARAMS_DELIVERY_TASK_COMPLETED) return@Observer
            val intent = Intent(context, TrackingDetailsActivity::class.java)
                .putExtra(PARAMS_PICKUP_TASK_ID, it.id)
            startActivity(intent)
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
        srlItems.setOnRefreshListener { viewModel.refresh(-1) }
        viewModel.refreshing
            .observe(this, refreshObserver)
    }
}