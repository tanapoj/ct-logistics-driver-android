package com.scgexpress.backoffice.android.ui.pickup.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toDateFormat
import com.scgexpress.backoffice.android.common.toDateTimeFormat
import com.scgexpress.backoffice.android.common.toPhoneNumber
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import kotlinx.android.synthetic.main.list_entry_pickup_task_completed.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_task_new_booking.view.*

class PickupTaskAdapter(val viewModel: PickupTaskViewModel) :
    RecyclerView.Adapter<PickupTaskAdapter.PickupTaskViewHolder>() {
    companion object {
        const val VIEWTYPE_NEW_BOOKING = 1
        const val VIEWTYPE_IN_PROGRESS = 2
        const val VIEWTYPE_COMPLETED = 3
        const val VIEWTYPE_BOOKING = 4
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupTaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_NEW_BOOKING -> {
                val view =
                    inflater.inflate(R.layout.list_entry_pickup_task_new_booking, parent, false)
                PickupTaskNewViewHolder(view, viewModel)
            }
            VIEWTYPE_IN_PROGRESS -> {
                val view =
                    inflater.inflate(R.layout.list_entry_pickup_task_in_progress, parent, false)
                PickupTaskInProgressViewHolder(view, viewModel)
            }
            VIEWTYPE_COMPLETED -> {
                val view =
                    inflater.inflate(R.layout.list_entry_pickup_task_completed, parent, false)
                PickupTaskCompletedViewHolder(view, viewModel)
            }
            else -> PickupTaskViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PickupTaskViewHolder, position: Int) {
        val item = data[holder.adapterPosition] as PickupTask
        when (holder) {
            is PickupTaskNewViewHolder -> holder.bind(item, holder.adapterPosition)
            is PickupTaskInProgressViewHolder -> holder.bind(item, holder.adapterPosition)
            is PickupTaskCompletedViewHolder -> holder.bind(item, holder.adapterPosition)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is PickupTask -> getPickupTaskViewType(position, data[position] as PickupTask)
            is BookingInfo -> VIEWTYPE_BOOKING
            else -> super.getItemViewType(position)
        }
    }

    private fun getPickupTaskViewType(position: Int, data: PickupTask): Int {
        return when (data.status) {
            "NEW_BOOKING" -> VIEWTYPE_NEW_BOOKING
            "IN_PROGRESS" -> VIEWTYPE_IN_PROGRESS
            "COMPLETED" -> VIEWTYPE_COMPLETED
            else -> super.getItemViewType(position)
        }
    }

    open class PickupTaskViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class PickupTaskNewViewHolder(view: View, private val viewModel: PickupTaskViewModel) :
        PickupTaskViewHolder(view) {

        fun bind(item: PickupTask, position: Int) = with(itemView) {

            val customer = listOfNotNull(item.customerCode, item.customerName).joinToString(" - ")
            tvCustomerName.text = customer

            if (item.createdAt != "") {
                tvDate.text = item.createdAt!!.toDateFormat()
            } else tvDate.text = ""

            tvBookingCode.text = item.bookingCode

            tvCount.text = item.totalCount.toString()

            val senderName = listOfNotNull("Sender :", item.senderName).joinToString(" ")
            tvSenderName.text = senderName

            val address = "Address : " + item.location!!.address
            tvAddress.text = address

            val tel = listOfNotNull("Tel :", item.tel.toPhoneNumber()).joinToString(" ")
            tvTelephone.text = tel

            if (item.pickupedAt != "") {
                val pickupDate = "Pickup date and time : " + item.pickupedAt!!
                tvPickupDate.text = pickupDate
            } else tvPickupDate.text = ""

            val serviceType = "Service type : " + "Normal " + item.serviceTypeCount!!.normal + "/" +
                    "Chilled " + item.serviceTypeCount!!.chilled + "/" +
                    "Frozen " + item.serviceTypeCount!!.frozen
            tvServiceType.text = serviceType

            val remark = listOfNotNull("Remark:", item.remark).joinToString(" ")
            tvRemark.text = remark

            btnTel.setOnClickListener { if (item.tel != "") viewModel.phoneCall(item.tel!!) }
            btnAddress.setOnClickListener {
                viewModel.showAddress(item.location)
            }
            btnAccept.text = btnAccept.context.getString(R.string.accept).toUpperCase()
            btnAccept.setOnClickListener { viewModel.bookingAccept(item) }
            btnReject.text = btnReject.context.getString(R.string.reject).toUpperCase()
            btnReject.setOnClickListener { viewModel.bookingReject(item) }
        }
    }

    class PickupTaskInProgressViewHolder(view: View, private val viewModel: PickupTaskViewModel) :
        PickupTaskViewHolder(view) {

        fun bind(item: PickupTask, position: Int) = with(itemView) {

            val customer = listOfNotNull(item.customerCode, item.customerName).joinToString(" - ")
            tvCustomerNameIP.text = customer

            if (item.createdAt != "") {
                tvDateIP.text = item.createdAt.toDateTimeFormat()
            } else tvDateIP.text = ""

            tvBookingCodeIP.text = item.bookingCode

            val count = item.pickupedCount.toString() + "/" + item.totalCount.toString()
            tvCountIP.text = count

            val senderName = listOfNotNull("Sender :", item.senderName).joinToString(" ")
            tvSenderNameIP.text = senderName

            val address = "Address : " + item.location!!.address
            tvAddressIP.text = address

            val tel = listOfNotNull("Tel :", item.tel.toPhoneNumber()).joinToString(" ")
            tvTelephoneIP.text = tel

            val serviceType = "Service type : " + "Normal " + item.serviceTypeCount!!.normal + "/" +
                    "Chilled " + item.serviceTypeCount!!.chilled + "/" +
                    "Frozen " + item.serviceTypeCount!!.frozen
            tvServiceTypeIP.text = serviceType

            containerIP.setOnClickListener { viewModel.itemClick(item) }
            btnTelIP.setOnClickListener { if (item.tel != "") viewModel.phoneCall(item.tel!!) }
            btnLocationIP.setOnClickListener {
                if (item.location!!.latitude != "" && item.location!!.longitude != "")
                    viewModel.showAddress(item.location!!)
            }
            btnCameraIP.setOnClickListener { viewModel.onActionPickup(item) }
        }
    }

    class PickupTaskCompletedViewHolder(view: View, private val viewModel: PickupTaskViewModel) :
        PickupTaskViewHolder(view) {

        fun bind(item: PickupTask, position: Int) = with(itemView) {

            val customer = listOfNotNull(item.customerCode, item.customerName).joinToString(" - ")
            tvCustomerNameCP.text = customer

            if (item.createdAt != "") {
                tvDateCP.text = item.createdAt!!.toDateFormat()
            } else tvDateCP.text = ""

            tvBookingCodeCP.text = item.bookingCode

            val count = item.pickupedCount.toString() + "/" + item.totalCount.toString()
            tvCountCP.text = count

            val senderName = listOfNotNull("Sender :", item.senderName).joinToString(" ")
            tvSenderNameCP.text = senderName

            val address = "Address : " + item.location!!.address
            tvAddressCP.text = address

            val tel = listOfNotNull("Tel :", item.tel.toPhoneNumber()).joinToString(" ")
            tvTelephoneCP.text = tel

            val serviceType = "Service type : " + "Normal " + item.serviceTypeCount!!.normal + "/" +
                    "Chilled " + item.serviceTypeCount!!.chilled + "/" +
                    "Frozen " + item.serviceTypeCount!!.frozen
            tvServiceTypeCP.text = serviceType

            containerCP.setOnClickListener { viewModel.itemClick(item) }
        }
    }

    class DiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is PickupTask && newObject is PickupTask) {
                oldObject.id == newObject.id &&
                        oldObject.id == newObject.id &&
                        oldObject.bookingCode == newObject.bookingCode
            } else if (oldObject is BookingInfo && newObject is BookingInfo) {
                oldObject.assignID == newObject.assignID &&
                        oldObject.bookingID == newObject.bookingID &&
                        oldObject.personalID == newObject.personalID
            } else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}