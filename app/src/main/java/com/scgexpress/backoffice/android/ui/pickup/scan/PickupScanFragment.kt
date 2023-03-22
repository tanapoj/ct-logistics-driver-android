package com.scgexpress.backoffice.android.ui.pickup.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.hideKeyboard
import com.scgexpress.backoffice.android.common.listener.DrawableClickListener
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_scan.*
import kotlinx.android.synthetic.main.fragment_topic.*
import javax.inject.Inject

class PickupScanFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): PickupScanFragment {
            return PickupScanFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private val viewModel: PickupScanViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(PickupScanViewModel::class.java)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupScanActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupScanActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pickup_scan, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initButton()
        loadData()
        observeData()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { it ->
            it.contents?.let {
                etScanCode.setText(it)
                viewModel.lookupCustomerCode(it)
            }
        }
    }

    private fun initButton() {

        etScanCode.setOnTouchListener(object : DrawableClickListener.RightDrawableClickListener(etScanCode) {
            override fun onDrawableClick(): Boolean {
                IntentIntegrator(activity).run {
                    initiateScan()
                }
                return true
            }
        })

        etScanCode.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (etScanCode.text?.isNotBlank() == true) {
                    etScanCode.hideKeyboard()
                }
                return@OnKeyListener true
            }
            false
        })

        btnConfirm.setOnClickListener {
            viewModel.lookupCustomerCode(etScanCode.text.toString())
        }
    }

    private fun loadData() {
    }

    private fun observeData() {
        viewModel.alertMessage.observe(this, Observer {
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let { msg ->

            }
        })
    }

    private fun navigateToSelectBookingActivity(){

    }

    private fun navigateToMainActivity(){

    }
}
