package com.scgexpress.backoffice.android.ui.delivery.ofd.sent

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_TRACKING_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.listener.DrawableClickListener
import com.scgexpress.backoffice.android.common.showWarningDialog
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_sent.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class OfdSentFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: OfdSentViewModel

        fun newInstance(vm: OfdSentViewModel, trackingID: String): OfdSentFragment {
            viewModel = vm
            return OfdSentFragment().also {
                val args = Bundle()
                args.putString(Const.PARAMS_TRACKING_ID, trackingID)
                it.arguments = args
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private val adapter: OfdSentAdapter by lazy {
        OfdSentAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OfdSentActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + OfdSentActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ofd_sent, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initButton()
        observeData()

        if (arguments!!.getString(PARAMS_TRACKING_ID)!! != "") {
            checkExistTracking(arguments!!.getString(PARAMS_TRACKING_ID)!!)
        }
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
            txtRegistered.text = (it.size - 1).toString()
            adapter.data = it
            edtScanTracking.text.clear()
        })

        viewModel.codAmount.observe(this, Observer {
            if (it == null) return@Observer
            txtCodAmount.text = Utils.setCurrencyFormat(it)
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

        viewModel.scanDataList.observe(this, Observer {
            if (it == null) return@Observer
            activity!!.onBackPressed()
            startActivity(
                Intent(context, SignatureActivity::class.java)
                    .putExtra(Const.PARAMS_MANIFEST_ID, viewModel.manifestID.value)
                    .putExtra(Const.PARAMS_MANIFEST_SENT_LIST, it)
            )
        })
        viewModel.trackingId.observe(this, Observer {
            edtScanTracking.setText(it)
        })

        viewModel.codDialog.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    viewModel.showDialogCod(rootView.context, it)
                }
            }
        })

        viewModel.getLocationHelper(context!!).observe(this, Observer<Location> { location ->
            if (location == null) return@Observer
            viewModel.latitude = location.latitude
            viewModel.longitude = location.longitude
        })
    }

    private fun initButton() {
        edtScanTracking.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                if (edtScanTracking.text.isNotEmpty()) {
                    checkExistTracking(edtScanTracking.text.toString())
                } else
                    viewModel.showWarning(getString(R.string.sentence_please_scan_your_tracking))
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

        btnConfirm.setOnClickListener {
            if (adapter.data.size > 1) {
                viewModel.confirmOfdSent()
            } else
                viewModel.showWarning(getString(R.string.sentence_please_scan_your_tracking))
        }
    }

    private fun checkExistTracking(trackingId: String) {
        if (viewModel.checkExistTracking(trackingId)) {
            viewModel.showWarning(getString(R.string.sentence_this_tracking_has_been_scanned))
            edtScanTracking.setText("")
            edtScanTracking.requestFocus()
        } else {
            viewModel.scanOfd(trackingId)
        }
    }
}
