package com.scgexpress.backoffice.android.ui.pickup.task.search


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.pickup.detail.PickupDetailsActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskAdapter
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_item_search.*
import javax.inject.Inject

class PickupSearchFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: PickupTaskViewModel

        fun newInstance(vm: PickupTaskViewModel):
                PickupSearchFragment = PickupSearchFragment().apply {
            viewModel = vm
            this.arguments = Bundle()
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: PickupSearchActivity
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

        if (context is PickupSearchActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupSearchActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_ofd_item_search, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initRefreshLayout()
        observeData()
        bookingClickedListener()
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun observeData() {
        viewModel.dataSearchResult.observe(this, Observer { it ->
            if (it == null) return@Observer
            adapter.data = it
            rvItems.smoothScrollToPosition(0)
        })

        viewModel.itemClick.observe(this, Observer {
            if (it == null) return@Observer
            val intent = Intent(context, PickupDetailsActivity::class.java)
                .putExtra(PARAMS_PICKUP_TASK_ID, it.id)
            startActivity(intent)
            activity!!.onBackPressed()
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

    private fun bookingClickedListener() {
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
                fragment.show(childFragmentManager, Const.PARAMS_DIALOG_RE_STATUS)
            }

        })
    }

    private fun initRefreshLayout() {
        /* srlItems.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
         srlItems.setOnRefreshListener { viewModelRetentionReason.refreshSearch() }
         viewModelRetentionReason.refreshing
             .observe(this, refreshObserver)*/
    }
}
