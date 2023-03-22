package com.scgexpress.backoffice.android.ui.pickup.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_TAG_DIALOG_PHOTO_SELECT
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.ui.dialog.PhotoSelectDialogFragment
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_details.*
import javax.inject.Inject


class PickupDetailsFragment : Fragment(), HasSupportFragmentInjector {


    companion object {
        private lateinit var viewModel: PickupDetailsViewModel
        fun newInstance(vm: PickupDetailsViewModel): PickupDetailsFragment {
            viewModel = vm
            return PickupDetailsFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var pickupPreference: PickupPreference

    private lateinit var mContext: Context
    private lateinit var rootView: View
    private lateinit var data: PickupTask
    private var customer: TblOrganization = TblOrganization()

    private val adapterTracking: PickupDetailsAdapter by lazy {
        PickupDetailsAdapter(viewModel)
    }

    private val adapterReceipt: PickupDetailsAdapter by lazy {
        PickupDetailsAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupDetailsActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupDetailsActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pickup_details, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
        initRecyclerView()
        observeData()
    }

    private fun initRecyclerView() {
        rvTracking.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvTracking.adapter = adapterTracking

        rvReceipt.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvReceipt.adapter = adapterReceipt
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer
            this.data = it
            mapData(it)
        })

        viewModel.trackings.observe(this, Observer {
            if (it == null) return@Observer
            adapterTracking.data = it
        })

        viewModel.receipts.observe(this, Observer {
            if (it == null) return@Observer
            adapterReceipt.data = it
        })

        viewModel.customer.observe(this, Observer {
            if (it == null) return@Observer
            customer = it
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

        viewModel.finish.observe(this, Observer {
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    activity!!.setResult(Activity.RESULT_OK)
                    activity!!.finish()
                }
            }
        })

        viewModel.viewPhoto.observe(this, Observer {
            if (it == null) return@Observer
            showPictureDialog(it)
        })
    }

    private fun mapData(data: PickupTask) {
        val customer = listOfNotNull(data.customerCode, data.customerName).joinToString(" - ")
        txtCustomerID.text = customer
        txtBookingID.text = data.bookingCode
        if (data.createdAt != "") {
            txtOrderDate.text = data.createdAt.toDateTimeFormat()
        } else txtOrderDate.text = ""

        val sender = listOfNotNull("Sender", data.senderName).joinToString(" : ")
        txtSender.text = sender
        txtStatus.text = data.status
        txtPhone.text = data.tel.toPhoneNumber()
        txtEmail.text = data.location.address

        if (data.pickupedAt != "") {
            txtDatePickup.text = data.pickupedAt
        } else txtDatePickup.text = ""
        if (data.updatedAt != "") {
            val updated = "( Update " + data.updatedAt.toDateTimeFormat() + " )"
            txtUpdatedDate.text = updated
        } else txtUpdatedDate.text = ""
        val serviceType =
            "normal ${data.serviceTypeCount.normal} / chilled ${data.serviceTypeCount.chilled} / frozen ${data.serviceTypeCount.frozen}"
        txtServiceType.text = serviceType
        txtRemark.text = data.remark

        val counted = "(" + data.pickupedCount + "/" + data.totalCount + " pieces)"
        txtCounted.text = counted

        if (data.status == PARAMS_PICKUP_TASK_COMPLETED) {
            btnCancelBooking.visibility = View.GONE
            containerButton.visibility = View.GONE
        } else {
            btnCancelBooking.visibility = View.VISIBLE
            containerButton.visibility = View.VISIBLE
        }

        if (data.trackingInfo!!.receipts.isEmpty()) {
            rvReceipt.visibility = View.GONE
        } else {
            rvReceipt.visibility = View.VISIBLE
        }

        if (data.trackingInfo!!.trackings.isEmpty()) {
            containerButton.visibility = View.GONE
        } else {
            containerButton.visibility = View.VISIBLE
        }
    }

    private fun initButton() {
        btnPhone.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            Utils.phoneCall(this.activity!!, data.tel)
        }
        btnAddress.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            val gmmIntentUri = Uri.parse(
                "google.navigation:q=" +
                        data.location.latitude + "," + data.location.longitude + "&mode=d"
            )
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        btnCancelBooking.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            val fragment = RejectStatusDialogFragment.newInstance()
            fragment.show(childFragmentManager, Const.PARAMS_DIALOG_RE_STATUS)
        }
        btnPickup.setOnClickListener {
            val id = data.id
            if (!viewModel.checkLastClickTime()) return@setOnClickListener

            Intent(context, PickupMainActivity::class.java).apply {
                pickupPreference.currentScanningTaskIdList = null
                putExtra(PARAMS_PICKUP_TASK_ID, id)
            }.also {
                startActivity(it)
            }
        }
    }

    private fun showPictureDialog(submitTracking: SubmitTracking) {
        val senderStored =
            PhotoStored(submitTracking.senderImgUrl!!, "")
        val receiverStored =
            PhotoStored(submitTracking.receiverImgUrl!!, "")
        val fragment = PhotoSelectDialogFragment.newInstance(senderStored, receiverStored)
        fragment.show(childFragmentManager, PARAMS_TAG_DIALOG_PHOTO_SELECT)
    }
}
