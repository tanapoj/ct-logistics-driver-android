package com.scgexpress.backoffice.android.ui.delivery.task.search


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
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.ui.delivery.detail.TrackingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskAdapter
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_item_search.*
import javax.inject.Inject

class DeliverySearchFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: DeliveryTaskViewModel

        fun newInstance(vm: DeliveryTaskViewModel):
                DeliverySearchFragment = DeliverySearchFragment().apply {
            viewModel = vm
            this.arguments = Bundle()
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: DeliverySearchActivity
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

        if (context is DeliverySearchActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + DeliverySearchActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_ofd_item_search, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initRefreshLayout()
        observeData()
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
            val intent = Intent(context, TrackingDetailsActivity::class.java)
                .putExtra(PARAMS_PICKUP_TASK_ID, it.id)
            startActivity(intent)
        })

        viewModel.phoneCall.observe(this, Observer {
            if (it == null) return@Observer
            Utils.phoneCall(this.activity!!, it)
        })

        viewModel.location.observe(this, Observer {
            if (it == null) return@Observer
            val gmmIntentUri = Uri.parse("google.navigation:q=" + it.latitude + "," + it.longitude + "&mode=d")
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
        /* srlItems.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
         srlItems.setOnRefreshListener { viewModelRetentionReason.refreshSearch() }
         viewModelRetentionReason.refreshing
             .observe(this, refreshObserver)*/
    }
}
