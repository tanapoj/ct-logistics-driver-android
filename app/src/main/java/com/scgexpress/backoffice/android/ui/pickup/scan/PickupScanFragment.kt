package com.scgexpress.backoffice.android.ui.pickup.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_SEARCH_SHIPPER_CODE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID_LIST
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_NOT_EXIST
import com.scgexpress.backoffice.android.common.hideKeyboard
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_scan.*
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

//        etScanCode.setOnTouchListener(object : DrawableClickListener.RightDrawableClickListener(etScanCode) {
//            override fun onDrawableClick(): Boolean {
//                IntentIntegrator(activity).run {
//                    initiateScan()
//                }
//                return true
//            }
//        })

        imgBtnScan.setOnClickListener {
            IntentIntegrator(activity).run {
                initiateScan()
            }
        }

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
        viewModel.alertMessage.observe(this, Observer { e ->
            if (e == null) return@Observer
            e.getContentIfNotHandled()?.let { msg ->
                AlertDialog.Builder(context!!).apply {
                    setTitle("มีข้อผิดพลาด")
                    setMessage(msg)
                    setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.run { create() }.also { it.show() }
            }
        })

        viewModel.matchTasks.observe(this, Observer { tasks ->
            val input = etScanCode.text.toString()
            //null = cannot find match task ans customer not exist in db
            //tasks.empty = no match tasks but has customer code in db
            //task.notEmpty = has match tasks
            if (input.isNotBlank() && tasks == null) {
                AlertDialog.Builder(context!!).apply {
                    setTitle("มีข้อผิดพลาด")
                    setMessage("ไม่มีข้อมูลที่คุณค้นหา")
                    setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.run { create() }.also { it.show() }
            }
            else if (tasks != null) {
                val shipperCode = tasks.firstOrNull()?.customerCode ?: input
                val ids = tasks.map { it.id }.toTypedArray()
                Intent(activity, PickupBookingListActivity::class.java).apply {
                    putExtra(PARAMS_PICKUP_TASK_NOT_EXIST, ids.isEmpty())
                    putExtra(PARAMS_PICKUP_TASK_ID_LIST, ids)
                    putExtra(PARAMS_PICKUP_SEARCH_SHIPPER_CODE, shipperCode)
                }.also {
                    startActivity(it)
                }
            }
        })
    }

    private fun navigateToSelectBookingActivity() {

    }

    private fun navigateToMainActivity() {

    }
}
