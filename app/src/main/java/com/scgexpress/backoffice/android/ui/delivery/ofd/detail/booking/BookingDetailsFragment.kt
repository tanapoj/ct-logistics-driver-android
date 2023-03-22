package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_BOOKING_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.showWarningDialog
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_booking_details.*
import javax.inject.Inject


class BookingDetailsFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: BookingDetailsViewModel
        fun newInstance(vm: BookingDetailsViewModel): BookingDetailsFragment {
            viewModel = vm
            return BookingDetailsFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: Context
    private lateinit var rootView: View
    private lateinit var data: BookingInfo
    private var customer: TblOrganization = TblOrganization()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is BookingDetailsActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + BookingDetailsActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_booking_details, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
        observeData()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer { it ->
            if (it == null) return@Observer
            this.data = it
            mapData(it)
        })

        viewModel.customer.observe(this, Observer { it ->
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
                    activity!!.showWarningDialog(it)
                }
            }
        })

        viewModel.finish.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    activity!!.onBackPressed()
                }
            }
        })
    }

    private fun mapData(data: BookingInfo) {
        edtBookingId.setText(data.bookingID)
        txtOrderDate.text = data.dateCreated

        txtStatus.text = data.assignStatus
        txtSender.text = data.senderName
        txtSenderCode.text = data.senderCode
        txtPhone.text = data.senderTel
        if (data.senderAddress.isEmpty())
            txtAddress.text = data.contactAddress
        else
            txtAddress.text = data.senderAddress
        val datePickup = "${data.pickupRequestDate} ${data.pickupRequestTime}"
        txtDatePickup.text = datePickup
        val serviceType = "normal: ${data.normalAmount}, chilled: ${data.chilledAmount}, frozen: ${data.frozenAmount}"
        txtServiceType.text = serviceType
        txtRemark.text = data.remark

        if (data.assignStatus == "done") {
            btnCancelBooking.visibility = View.GONE
            containerButton.visibility = View.GONE
            btnItems.visibility = View.VISIBLE
        } else {
            btnCancelBooking.visibility = View.VISIBLE
            containerButton.visibility = View.VISIBLE
            btnItems.visibility = View.GONE
        }

        if (data.senderCode!!.isNotEmpty()) {
            viewModel.getCustomer(data.senderCode)
        }
    }

    private fun initButton() {
        btnPhone.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            Utils.phoneCall(this.activity!!, data.senderTel!!)
        }
        btnCancelBooking.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            val fragment = RejectStatusDialogFragment.newInstance()
            fragment.show(childFragmentManager, Const.PARAMS_DIALOG_RE_STATUS)
        }
        btnPickup.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            if (customer.customerCode != "") {
//                activity!!.startActivityForResult(
//                    Intent(context, PickupMainActivity::class.java)
//                        .putExtra(PARAMS_ORGANIZATION, customer)
//                        .putExtra(PARAMS_BOOKING_ID, data.bookingID)
//                        .putExtra(PARAMS_ASSIGNMENT_ID, data.assignID), REQUEST_CODE_PICKUP
//                )
            } else {
                viewModel.showWarning(getString(R.string.sentence_booking_details_error_invalid_customer_code))
            }
        }
        btnItems.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            startActivity(
                Intent(context, BookingItemsActivity::class.java)
                    .putExtra(PARAMS_BOOKING_ID, data.bookingID)
            )
        }
    }
}
