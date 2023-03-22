package com.scgexpress.backoffice.android.ui.pickup.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.hideKeyboard
import com.scgexpress.backoffice.android.common.listener.DrawableClickListener
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_scan.*
import javax.inject.Inject

class PickupMainFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): PickupMainFragment {
            return PickupMainFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private val viewModel: PickupMainViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(PickupMainViewModel::class.java)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupMainActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupMainActivity::class.java.simpleName)
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
//                    viewModel.getData(etScanCode.text.toString())
                }
                return@OnKeyListener true
            }
            false
        })
    }

    private fun loadData() {
        viewModel.getTopics()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer

        })
    }
}
