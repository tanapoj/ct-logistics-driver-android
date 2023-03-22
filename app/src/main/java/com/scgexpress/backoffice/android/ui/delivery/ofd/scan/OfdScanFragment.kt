package com.scgexpress.backoffice.android.ui.delivery.ofd.scan


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.listener.DrawableClickListener
import com.scgexpress.backoffice.android.common.showWarningDialog
import com.scgexpress.backoffice.android.model.Manifest
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_scan.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class OfdScanFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: OfdScanViewModel

        fun newInstance(vm: OfdScanViewModel): OfdScanFragment {
            viewModel = vm
            return OfdScanFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var rootView: View

    private val adapter: OfdScanAdapter by lazy {
        OfdScanAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OfdScanActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + OfdScanActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ofd_scan, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initButton()
        observeData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { it ->
            it.contents?.let {
                edtScanTracking.setText(it)
                checkExistTracking(it)
            }
        }
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter

        edtScanTracking.requestFocus()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer
            adapter.data = it
            edtScanTracking.text.clear()
        })

        viewModel.header.observe(this, Observer { it ->
            if (it != null) {
                setHeader(it)
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
                    activity!!.showWarningDialog(it)
                }
            }
        })

        viewModel.trackingId.observe(this, Observer {
            edtScanTracking.setText(it)
        })

        viewModel.getLocationHelper(context!!).observe(this, Observer<Location> { location ->
            if (location == null) return@Observer
            viewModel.latitude = location.latitude
            viewModel.longitude = location.longitude
        })
    }

    private fun setHeader(item: Manifest) {
        adapter.manifestID = item.id.toString()
        if (viewModel.dataNetwork.value == null)
            viewModel.prepareData()

        try {
            tvRemain.text = (item.bookingsTotal!!.toInt() -
                    item.bookingsDone!!.toInt()).toString()
            tvOfdRemain.text = (item.noOfItemsTotal!!.toInt() -
                    (item.noOfItemsDelivered!!.toInt() + item.noOfItemsRetention!!.toInt())).toString()
        } catch (e: Exception) {
            tvRemain.text = 0.toString()
            tvOfdRemain.text = 0.toString()
        }

        tvPicked.text = item.bookingsDone
        tvTotal.text = item.bookingsTotal

        tvDeliveryDelivered.text = item.noOfItemsDelivered
        tvDeliveryRetention.text = item.noOfItemsRetention
        tvDeliveryTotal.text = item.noOfItemsTotal
    }

    private fun initButton() {
        edtScanTracking.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                checkExistTracking(edtScanTracking.text.toString())
                return@OnKeyListener true
            }
            false
        })

        edtScanTracking.setOnTouchListener(object : DrawableClickListener.RightDrawableClickListener(edtScanTracking) {
            override fun onDrawableClick(): Boolean {
                IntentIntegrator(activity).run {
                    initiateScan()
                }
                return true
            }
        })

        btnDone.setOnClickListener {
            showDialogConfirmExit()
        }
    }


    private fun checkExistTracking(trackingId: String) {
        if (viewModel.checkExistTracking(trackingId)) {
            viewModel.showWarning(getString(R.string.sentence_this_tracking_has_been_scanned))
            edtScanTracking.setText("")
            edtScanTracking.requestFocus()
        } else {
            viewModel.confirmScanOfd(trackingId)
        }
    }

    private fun showDialogConfirmExit() {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setMessage(getString(R.string.sentence_scan_are_you_sure_you_want_to_confirm_scan_out))
        mBuilder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            activity!!.onBackPressed()
        }
        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }
}

