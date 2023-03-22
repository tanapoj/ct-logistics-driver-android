package com.scgexpress.backoffice.android.ui.pickup.receipt

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_SELECT_TAB
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_receipt.*
import javax.inject.Inject

class PickupReceiptFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: PickupReceiptViewModel
        fun newInstance(vm: PickupReceiptViewModel): PickupReceiptFragment {
            this.viewModel = vm
            return PickupReceiptFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        check(context is PickupReceiptActivity) {
            "This fragment must be use in conjunction with " + PickupReceiptActivity::class.java.simpleName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_pickup_receipt, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.requestItem()
        initButton()
        observeData()
    }

    private fun observeData() {
        viewModel.alertMessage.observe(this, Observer { it ->
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let {
                // Only proceed if the event has never been handled
                activity!!.showAlertMessage(it)
            }
        })

        viewModel.nextBooking.observe(this, Observer { it ->
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let {
                // Only proceed if the event has never been handled
                nextBookingDialog(it)
            }
        })

        viewModel.finishWithMessage.observe(this, Observer { msg ->
            if (msg == null) return@Observer
            msg.getContentIfNotHandled()?.let { it ->
                finishWithMessage(it)
            }
        })

        viewModel.finish.observe(this, Observer { it ->
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let { it ->
                if (it) {
                    //activity!!.setResult(RESULT_OK)
                    Intent(activity, PickupTaskActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(PARAMS_PICKUP_TASK_SELECT_TAB, PARAMS_PICKUP_COMPLETED)
                    }.also {
                        startActivity(it)
                    }
                    activity!!.finish()
                }
            }
        })

        viewModel.isEnableButtonSend.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    btnSend.isEnabled = it
                }
            }
        })

        viewModel.getLocationHelper(context!!).observe(this, Observer<Location> { location ->
            if (location == null) return@Observer
            viewModel.latitude = location.latitude
            viewModel.longitude = location.longitude
        })

        viewModel.phone.observe(this, Observer { phone ->
            etBranch.setText(phone)
        })

        viewModel.email.observe(this, Observer { email ->
            etDriver.setText(email)
        })
    }

    private fun nextBookingDialog(task: PickupTask) {
        val msg = "Continue scan pick up next booking no. ${task.bookingCode}"
        AlertDialog.Builder(context!!).apply {
            setMessage(msg)
            setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                viewModel.finish(true)
                Intent(activity, PickupBookingListActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK, true)
                }.also {
                    startActivity(it)
                }
                dialog.dismiss()
                activity!!.finish()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                viewModel.finish(true)
                dialog.dismiss()
            }
        }.run { create() }.also { it.show() }
    }

    private fun finish(){
        Intent(activity, PickupTaskActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(PARAMS_PICKUP_TASK_SELECT_TAB, PARAMS_PICKUP_COMPLETED)
        }.also {
            startActivity(it)
        }
        activity!!.finish()
    }

    private fun finishWithMessage(msg: String) {
        AlertDialog.Builder(context!!).apply {
            setMessage(msg)
            setPositiveButton("Ok") { dialog, _ ->
                finish()
            }
            setCancelable(false)
        }.run { create() }.also { it.show() }
    }

    private fun initButton() {
        btnSend.setOnClickListener {
            pickup()
        }
    }

    private fun pickup() {
        val phoneNumber = etBranch.text.toString()
        val email = etDriver.text.toString()

        viewModel.sendReceipt(phoneNumber, email)
    }
}
