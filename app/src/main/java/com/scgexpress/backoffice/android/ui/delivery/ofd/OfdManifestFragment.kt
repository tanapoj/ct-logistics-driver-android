package com.scgexpress.backoffice.android.ui.delivery.ofd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivity
import com.scgexpress.backoffice.android.ui.delivery.DeliveryViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.scan.OfdScanActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.ofd_manifest_fragment.*
import javax.inject.Inject

class OfdManifestFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: DeliveryViewModel

        fun newInstance(vm: DeliveryViewModel): OfdManifestFragment {
            viewModel = vm
            return OfdManifestFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var rootView: View

    private val ofdAdapter by lazy {
        OfdAdapter(viewModel)
    }

    private val refreshObserver =
        Observer<Boolean> {
            srlOfd.isRefreshing = it!!
        }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is DeliveryMainActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + DeliveryMainActivity::class.java.simpleName)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.ofd_manifest_fragment, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initRefreshLayout()
        initButton()
        observeData()
    }

    private fun initRefreshLayout() {
        srlOfd.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        srlOfd.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing
            .observe(this, refreshObserver)
    }

    private fun initRecyclerView() {
        rvOfd.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvOfd.adapter = ofdAdapter
    }

    private fun initButton() {
    }

    private fun observeData() {
        viewModel.ofdManifest.observe(this, Observer { it ->
            if (it == null) return@Observer
            val oldDataSize = ofdAdapter.data.size
            ofdAdapter.data = it.sortedWith(compareBy({ it.dateCreated }, { it.id })).reversed()
            if (oldDataSize < ofdAdapter.data.size) {
                rvOfd.smoothScrollToPosition(0)
            }
        })
        ofdClickedListener()
    }

    private fun ofdClickedListener() {
        viewModel.ofdScan.observe(this, Observer {
            if (it == null) return@Observer
            val intent = Intent(context, OfdScanActivity::class.java)
                .putExtra(PARAMS_MANIFEST, it)
            startActivity(intent)
        })
    }
}