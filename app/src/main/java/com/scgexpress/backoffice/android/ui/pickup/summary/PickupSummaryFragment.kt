package com.scgexpress.backoffice.android.ui.pickup.summary

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_GROUP_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_CONTINUE_NEXT_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_CUSTOMER_CODE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_PAYMENT
import com.scgexpress.backoffice.android.common.Const.PARAMS_TAG_DIALOG_PHOTO_SELECT
import com.scgexpress.backoffice.android.common.Const.REQUEST_CODE_PICKUP
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.ui.dialog.PhotoSelectDialogFragment
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListActivity
import com.scgexpress.backoffice.android.ui.pickup.receipt.PickupReceiptActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_summary.*
import javax.inject.Inject

class PickupSummaryFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private const val UNDEFINED = "undefined"
        private lateinit var viewModel: PickupSummaryViewModel
        fun newInstance(vm: PickupSummaryViewModel): PickupSummaryFragment {
            viewModel = vm
            return PickupSummaryFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private var payment: Int = 0

    private val adapter: PickupSummaryAdapter by lazy {
        PickupSummaryAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupSummaryActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupSummaryActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pickup_summary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initButton()
        loadData()
        observeData()
    }

    private fun initButton() {
        btnContinue.setOnClickListener {
            activity?.finish()
        }

        btnDone.setOnClickListener {
            /*if (viewModelRetentionReason.businessCustomer.value!!) donePickupSummary(UNDEFINED)
            else showDialogPayment()*/
            viewModel.showDialogPayment()
        }

        btnDone.setOnLongClickListener {
            Intent(activity, PickupBookingListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(PARAMS_PICKUP_CONTINUE_NEXT_TASK, true)
            }.also {
                startActivity(it)
            }
            true
        }
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun loadData() {
        viewModel.insertData()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer
            btnDone.isEnabled = it.size != 1
            adapter.data = it
        })

        viewModel.statusCount.observe(this, Observer {
            val (fromBooking, newTracking, total) = it ?: return@Observer
            val scanned = fromBooking + newTracking

            context?.resources.apply {
                if (total > 0) {
                    tvRegistered.text =
                        getString(R.string.pickup_title_amount_to_total, scanned, total)
                } else {
                    tvRegistered.text =
                        getString(R.string.pickup_title_amount_to_total_none, scanned)
                }
            }
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

        viewModel.cartonFee.observe(this, Observer {
            if (it == null) return@Observer
            tvCartonFee.text = Utils.setCurrencyFormatWithUnit(it)
        })

        viewModel.total.observe(this, Observer {
            if (it == null) return@Observer
            tvTotal.text = Utils.setCurrencyFormatWithUnit(it)
        })

        viewModel.done.observe(this, Observer {
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let { it ->
                btnDone.isEnabled = it
            }
        })

        viewModel.businessCustomer.observe(this, Observer {
            if (it != null) {
                if (it) displayBusinessCustomer()
            }
        })

        viewModel.paymentMethods.observe(this, Observer {
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let { it ->
                showDialogPayment(it)
            }
        })

        viewModel.viewPhoto.observe(this, Observer { it ->
            if (it == null) return@Observer
            showPictureDialog(it)
        })
    }

    private fun displayBusinessCustomer() {
        containerFee.visibility = View.GONE
    }


    private fun showDialogPayment(paymentMethods: List<Pair<Int, String>>) {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(getString(R.string.payment_method))
        mBuilder.setSingleChoiceItems(paymentMethods.map { it.second }.toTypedArray(), -1) { _, i ->
            //txtView.text = listItems[i]
            payment = paymentMethods[i].first
        }
        mBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            // Do something when click the neutral button
            if (payment > 0) {
                dialog.dismiss()
                donePickupSummary("$payment")
            }
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun donePickupSummary(payment: String) {
        viewModel.savePayment(payment)

        btnDone.isEnabled = false
        btnContinue.isEnabled = false
        adapter.showDelete = false

        activity!!.startActivityForResult(
            Intent(context, PickupReceiptActivity::class.java)
                .putExtra(Const.PARAMS_PICKUP_TASK_ID, viewModel.taskId)
                .putExtra(PARAMS_PICKUP_CUSTOMER_CODE, viewModel.customerCode)
                .putExtra(PARAMS_PICKUP_PAYMENT, payment)
                .putExtra(PARAMS_GROUP_ID, viewModel.groupID), REQUEST_CODE_PICKUP
        )
    }

    private fun showPictureDialog(submitTracking: PickupScanningTrackingEntity) {
        val senderStored =
            PhotoStored(submitTracking.senderImgUrl, submitTracking.senderImgPath)
        val receiverStored =
            PhotoStored(submitTracking.receiverImgUrl, submitTracking.receiverImgPath)
        val fragment = PhotoSelectDialogFragment.newInstance(senderStored, receiverStored)
        fragment.show(childFragmentManager, PARAMS_TAG_DIALOG_PHOTO_SELECT)
    }
}