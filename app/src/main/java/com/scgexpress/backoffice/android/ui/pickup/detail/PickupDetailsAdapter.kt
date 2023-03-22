package com.scgexpress.backoffice.android.ui.pickup.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toPhoneNumber
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.pickup.Receipt
import com.scgexpress.backoffice.android.model.tracking.Tracking
import kotlinx.android.synthetic.main.list_entry_pickup_details_receipt.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_details_tracking.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_details_tracking_title.view.*

class PickupDetailsAdapter(val viewModel: PickupDetailsViewModel) :
    RecyclerView.Adapter<PickupDetailsAdapter.PickupDetailsViewHolder>() {
    companion object {
        const val VIEWTYPE_TRACKING_TITLE = 1
        const val VIEWTYPE_TRACKING = 2
        const val VIEWTYPE_RECEIPT = 3
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_TRACKING_TITLE -> {
                val view = inflater.inflate(
                    R.layout.list_entry_pickup_details_tracking_title,
                    parent,
                    false
                )
                PickupDetailsTrackingTitleViewHolder(view)
            }
            VIEWTYPE_TRACKING -> {
                val view =
                    inflater.inflate(R.layout.list_entry_pickup_details_tracking, parent, false)
                PickupDetailsTrackingViewHolder(view, viewModel)
            }
            VIEWTYPE_RECEIPT -> {
                val view =
                    inflater.inflate(R.layout.list_entry_pickup_details_receipt, parent, false)
                PickupDetailsReceiptViewHolder(view, viewModel)
            }
            else -> PickupDetailsViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PickupDetailsViewHolder, position: Int) {
        when (holder) {
            is PickupDetailsTrackingTitleViewHolder -> {
                val item = data[holder.adapterPosition] as Title
                holder.bind(item)
            }
            is PickupDetailsTrackingViewHolder -> {
                val item = data[holder.adapterPosition] as Tracking
                holder.bind(item)
            }
            is PickupDetailsReceiptViewHolder -> {
                val item = data[holder.adapterPosition] as Receipt
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Title -> VIEWTYPE_TRACKING_TITLE
            is Tracking -> VIEWTYPE_TRACKING
            is Receipt -> VIEWTYPE_RECEIPT
            else -> super.getItemViewType(position)
        }
    }

    open class PickupDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class PickupDetailsTrackingTitleViewHolder(view: View) :
        PickupDetailsViewHolder(view) {

        fun bind(item: Title) = with(itemView) {
            txtTitle.text = item.title
        }
    }

    class PickupDetailsTrackingViewHolder(
        view: View,
        private val viewModel: PickupDetailsViewModel
    ) :
        PickupDetailsViewHolder(view) {

        fun bind(item: Tracking) = with(itemView) {
            txtTrackingID.text = item.tracking.toTrackingId()
            txtOrderID.text = item.orderId
            if (item.pinbox!!.isNotBlank()) {
                txtPin.text = item.pinbox
                txtPin.visibility = View.VISIBLE
            } else {
                txtPin.visibility = View.GONE
            }
        }
    }

    class PickupDetailsReceiptViewHolder(
        view: View,
        private val viewModel: PickupDetailsViewModel
    ) :
        PickupDetailsViewHolder(view) {

        private val adapter: PickupDetailsTrackingAdapter by lazy {
            PickupDetailsTrackingAdapter(viewModel)
        }

        fun bind(item: Receipt) = with(itemView) {
            val receipt = listOfNotNull("Receipt code", item.receiptCode).joinToString(" : ")
            txtReceiptCode.text = receipt
            txtPhone.setText(viewModel.getCustomerTel().toPhoneNumber())
            txtEmail.setText(viewModel.getCustomerEmail())

            rvTracking.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rvTracking.adapter = adapter

            val d: ArrayList<Any> = arrayListOf(Title(""))
            d.addAll(viewModel.setServiceAndSizeName(item.submitTrackings))
            adapter.data = d

            btnResend.setOnClickListener {
                val phone = txtPhone.text.toString()
                val email = txtEmail.text.toString()
                if (phone.isNotBlank() || email.isNotBlank())
                    viewModel.resendReceipt(item.receiptId, phone, email)
                else
                    viewModel.showWarning("Please insert phone number or email")
            }
        }
    }

    class DiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is Title && newObject is Title) {
                oldObject.title == newObject.title
            } else if (oldObject is Tracking && newObject is Tracking) {
                oldObject.tracking == newObject.tracking &&
                        oldObject.orderId == newObject.orderId &&
                        oldObject.pinbox == newObject.pinbox
            } else if (oldObject is Receipt && newObject is Receipt) {
                oldObject.receiptId == newObject.receiptId &&
                        oldObject.receiptCode == newObject.receiptCode
            } else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}