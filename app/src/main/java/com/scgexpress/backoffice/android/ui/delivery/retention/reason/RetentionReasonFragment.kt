package com.scgexpress.backoffice.android.ui.delivery.retention.reason

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_delivery_retention_reason.*
import javax.inject.Inject

class RetentionReasonFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModelRetentionReason: RetentionReasonViewModel

        fun newInstance(vm: RetentionReasonViewModel): RetentionReasonFragment {
            viewModelRetentionReason = vm
            return RetentionReasonFragment()
                .also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: Context
    private lateinit var rootView: View

    private val adapter by lazy {
        RetentionReasonAdapter(viewModelRetentionReason)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is RetentionReasonActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + RetentionReasonActivity::class.java.simpleName)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeData()
        initRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_delivery_retention_reason, container, false)
        return rootView
    }

    private fun initRecyclerView() {
        rvScanReason.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvScanReason.adapter = adapter
    }

    private fun observeData() {
        viewModelRetentionReason.loadMainReason()
        viewModelRetentionReason.mainReason.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            adapter.data = it
            adapter.notifyDataSetChanged()
        })
    }

}