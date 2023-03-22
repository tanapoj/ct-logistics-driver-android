package com.scgexpress.backoffice.android.ui.pickup.task.item

import android.content.Context
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
import com.scgexpress.backoffice.android.common.Const.PARAMS_DIALOG_RE_STATUS
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_TAB_TYPE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_NEW_BOOKING
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskAdapter
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_task_items.*
import javax.inject.Inject


class PickupTaskNewFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: PickupTaskViewModel
        fun newInstance(type: String, vm: PickupTaskViewModel): PickupTaskNewFragment {
            viewModel = vm
            return PickupTaskNewFragment().also {
                val args = Bundle()
                args.putString(PARAMS_MANIFEST_TAB_TYPE, type)
                it.arguments = args
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

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
        bookingClickedListener()
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
        viewModel.newPickupTask.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_PICKUP_NEW_BOOKING) {
                adapter.data = it
                adapter.notifyDataSetChanged()
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(context!!, 0, it.size)
            }
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

    private fun bookingClickedListener() {
        if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_PICKUP_NEW_BOOKING) {
            viewModel.bookingAccept.observe(this, Observer {
                if (it == null) return@Observer
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    viewModel.showConfirmAcceptationDialog(activity!!, it.id, it.bookingCode)
                }
            })
            viewModel.bookingReject.observe(this, Observer {
                if (it == null) return@Observer
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    val fragment = RejectStatusDialogFragment.newInstance()
                    fragment.show(childFragmentManager, PARAMS_DIALOG_RE_STATUS)
                }
            })
        }
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