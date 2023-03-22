package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking


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
import com.scgexpress.backoffice.android.common.Utils
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_booking_items.*
import javax.inject.Inject

class BookingItemsFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: BookingItemsViewModel
        fun newInstance(vm: BookingItemsViewModel): BookingItemsFragment {
            viewModel = vm
            return BookingItemsFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private val adapter: BookingItemsAdapter by lazy {
        BookingItemsAdapter()
    }

    private lateinit var mContext: Context
    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is BookingItemsActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + BookingItemsActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_booking_items, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        initButton()
        observeData()
        loadData()
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun loadData() {
        viewModel.loadData()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer
            tvRegistered.text = (it.size - 1).toString()
            adapter.data = it
        })

        viewModel.deliveryFee.observe(this, Observer {
            if (it == null) return@Observer
            tvCount.text = Utils.setCurrencyFormatWithUnit(it)
        })

        viewModel.serviceCharge.observe(this, Observer {
            if (it == null) return@Observer
            tvServiceCharge.text = Utils.setCurrencyFormatWithUnit(it)
        })

        viewModel.codFee.observe(this, Observer {
            if (it == null) return@Observer
            tvCodFee.text = Utils.setCurrencyFormatWithUnit(it)
        })

        viewModel.total.observe(this, Observer {
            if (it == null) return@Observer
            tvTotal.text = Utils.setCurrencyFormatWithUnit(it)
        })
    }

    private fun initButton() {
    }
}
