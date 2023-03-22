package com.scgexpress.backoffice.android.ui.delivery.booking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.Manifest
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivity
import com.scgexpress.backoffice.android.ui.delivery.DeliveryViewModel
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.new_booking_fragment.*
import timber.log.Timber
import javax.inject.Inject

class NewBookingsFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: DeliveryViewModel

        fun newInstance(vm: DeliveryViewModel): NewBookingsFragment {
            viewModel = vm
            return NewBookingsFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: Context
    private lateinit var rootView: View

    private var manifestIDList: ArrayList<String> = arrayListOf()
    private var manifestBarcodeList: ArrayList<String> = arrayListOf()

    private var manifestID: String = ""

    private val adapter by lazy {
        NewBookingsAdapter(viewModel)
    }

    private val refreshObserver =
        Observer<Boolean> {
            srlBookings.isRefreshing = it!!
        }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is DeliveryMainActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + DeliveryMainActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.new_booking_fragment, container, false)
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
        rvBookings.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvBookings.adapter = adapter
    }

    private fun initRefreshLayout() {
        srlBookings.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        srlBookings.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing
            .observe(this, refreshObserver)
    }

    private fun observeData() {
        viewModel.ofdManifest.observe(this, Observer {
            if (it == null) return@Observer
            manifestIDList = arrayListOf()
            manifestBarcodeList = arrayListOf()
            for (manifest: Manifest in it) {
                manifestIDList.add(manifest.id!!)
                manifestBarcodeList.add(manifest.barcode!!)
            }
            if (viewModel.bookingAccept.value != null) {
                if (manifestIDList.size == 1 && viewModel.bookingAccept.value!!.bookingID != "") {
                    viewModel.acceptBooking(manifestIDList[0], viewModel.bookingAccept.value!!)
                }
            }
        })

        viewModel.newBookings.observe(this, Observer {
            if (it == null) return@Observer
            val oldDataSize = adapter.data.size
            adapter.data = it
            try {
                if (oldDataSize < adapter.data.size) {
                    rvBookings.smoothScrollToPosition(0)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }

        })

        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.warning.observe(this, Observer { it ->
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
            if (manifestBarcodeList.size == 1) {
                viewModel.acceptBooking(manifestIDList[0], it)
                return@Observer
            }
            if (it.bookingID != "")
                showDialogOfd(it)
        })
        viewModel.bookingReject.observe(this, Observer {
            if (it == null) return@Observer
            val fragment = RejectStatusDialogFragment.newInstance()
            fragment.show(childFragmentManager, Const.PARAMS_DIALOG_RE_STATUS)
        })
    }

    private fun showDialogOfd(item: BookingInfo) {
        if (manifestBarcodeList.size > 0) {
            val mBuilder = AlertDialog.Builder(mContext)
            mBuilder.setTitle("Assign to...")
            mBuilder.setSingleChoiceItems(
                ArrayAdapter<String>(
                    mContext, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    manifestBarcodeList // List
                ), -1
            ) { _, i ->
                manifestID = manifestIDList[i]
            }
            mBuilder.setPositiveButton("OK") { _, _ ->
                if (manifestID != "") {
                    viewModel.acceptBooking(manifestID, item)
                }
            }
            mBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val mDialog = mBuilder.create()
            mDialog.show()
        } else {
            viewModel.showDialogOfdCreated(mContext)
        }
    }
}