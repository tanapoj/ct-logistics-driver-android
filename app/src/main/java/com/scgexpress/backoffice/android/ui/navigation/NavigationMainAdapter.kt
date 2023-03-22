package com.scgexpress.backoffice.android.ui.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.common.toDateTimeFormat
import com.scgexpress.backoffice.android.common.toPhoneNumber
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvServiceIP as tvServiceIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvDateIP as tvDateIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvTrackingIP as tvTrackingIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvBackOrderIP as tvBackOrderIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvCodIP as tvCodIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvRecipientIP as tvRecipientIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvAddressIP as tvAddressIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvTelephoneIP as tvTelephoneIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.tvRemarkIP as tvRemarkIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.btnTelIP as btnTelIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.btnLocationIP as btnLocationIPDelivery
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.btnCameraIP as btnCameraIPDelivery
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvCustomerNameIP as tvCustomerNameIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvBookingCodeIP as tvBookingCodeIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvCountIP as tvCountIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvSenderNameIP as tvSenderNameIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvServiceTypeIP as tvServiceTypeIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvDateIP as tvDateIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvAddressIP as tvAddressIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.tvTelephoneIP as tvTelephoneIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.btnTelIP as btnTelIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.btnLocationIP as btnLocationIPPickup
import kotlinx.android.synthetic.main.list_entry_pickup_task_in_progress.view.btnCameraIP as btnCameraIPPickup

class NavigationMainAdapter(val viewModel: NavigationMainViewModel) :
    RecyclerView.Adapter<NavigationMainAdapter.NavigationMainViewHolder>() {

    companion object {
        const val VIEWTYPE_PICKUP = 1
        const val VIEWTYPE_DELIVERY = 2
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationMainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_PICKUP -> {
                val view =
                    inflater.inflate(R.layout.list_entry_pickup_task_in_progress, parent, false)
                NavigationMainPickupViewHolder(view, viewModel)
            }
            VIEWTYPE_DELIVERY -> {
                val view =
                    inflater.inflate(R.layout.list_entry_delivery_task_in_progress, parent, false)
                NavigationMainDeliveryViewHolder(view, viewModel)
            }
            else -> NavigationMainViewHolder(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is PickupTask -> VIEWTYPE_PICKUP
            is DeliveryTask -> VIEWTYPE_DELIVERY
            else -> throw IllegalStateException("Navigator List Allow PickupTask or DeliveryTask")
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: NavigationMainViewHolder, position: Int) {
        when (holder) {
            is NavigationMainPickupViewHolder -> {
                val item = data[holder.adapterPosition] as PickupTask
                holder.bind(item)
            }
            is NavigationMainDeliveryViewHolder -> {
                val item = data[holder.adapterPosition] as DeliveryTask
                holder.bind(item)
            }
        }
    }

    open class NavigationMainViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class NavigationMainPickupViewHolder(
        view: View,
        private val viewModel: NavigationMainViewModel
    ) :
        NavigationMainViewHolder(view) {

        fun bind(item: PickupTask) = with(itemView) {
            val customer = listOfNotNull(item.customerCode, item.customerName).joinToString(" - ")
            tvCustomerNameIPPickup.text = customer

            if (item.createdAt != "") {
                tvDateIPPickup.text = item.createdAt.toDateTimeFormat()
            } else tvDateIPPickup.text = ""

            tvBookingCodeIPPickup.text = item.bookingCode

            val count = item.pickupedCount.toString() + "/" + item.totalCount.toString()
            tvCountIPPickup.text = count

            val senderName = listOfNotNull("Sender :", item.senderName).joinToString(" ")
            tvSenderNameIPPickup.text = senderName

            val address = "Address : " + item.location.address
            tvAddressIPPickup.text = address

            val tel = listOfNotNull("Tel :", item.tel.toPhoneNumber()).joinToString(" ")
            tvTelephoneIPPickup.text = tel

            val serviceType = "Service type : " + "Normal " + item.serviceTypeCount.normal + "/" +
                    "Chilled " + item.serviceTypeCount.chilled + "/" +
                    "Frozen " + item.serviceTypeCount.frozen
            tvServiceTypeIPPickup.text = serviceType
            btnTelIPPickup.setOnClickListener { if (item.tel != "") viewModel.phoneCall(item.tel) }
            btnLocationIPPickup.setOnClickListener {
                if (item.location.latitude != "" && item.location.longitude != "")
                    viewModel.showAddress(item.location)
            }
            btnCameraIPPickup.setOnClickListener { viewModel.onActionPickup(item) }
        }
    }

    class NavigationMainDeliveryViewHolder(
        view: View,
        private val viewModel: NavigationMainViewModel
    ) :
        NavigationMainViewHolder(view) {

        fun bind(item: DeliveryTask) = with(itemView) {
            val customer = listOfNotNull(item.serviceName, item.sizeName).joinToString(" - ")
            tvServiceIPDelivery.text = customer

            if (item.createdAt != "") {
                tvDateIPDelivery.text = item.createdAt.toDateTimeFormat()
            } else tvDateIPDelivery.text = ""

            tvTrackingIPDelivery.text = item.trackingCode

            tvBackOrderIPDelivery.text = context.getString(R.string.back_order)

            tvCodIPDelivery.text = item.codAmount?.toCurrencyFormat()

            val recipientName = listOfNotNull("Recipient :", item.recipientName).joinToString(" ")
            tvRecipientIPDelivery.text = recipientName

            val address = "Address : " + item.recipientLocation.address
            tvAddressIPDelivery.text = address

            val tel = listOfNotNull("Tel :", item.senderTel.toPhoneNumber()).joinToString(" ")
            tvTelephoneIPDelivery.text = tel

            val remark =
                "Remark : ${item.remark} (Delivery time ${item.deliveryAt.toDateTimeFormat()})"
            tvRemarkIPDelivery.text = remark
            btnTelIPDelivery.setOnClickListener { if (item.senderTel != "") viewModel.phoneCall(item.senderTel) }
            btnLocationIPDelivery.setOnClickListener {
                if (item.recipientLocation.latitude != "" && item.recipientLocation.longitude != "")
                    viewModel.showAddress(item.recipientLocation)
            }
            btnCameraIPDelivery.setOnClickListener { viewModel.onActionDelivery(item) }
        }
    }

    class DiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]
            return if (oldObject is PickupTask && newObject is PickupTask && oldObject is DeliveryTask && newObject is DeliveryTask) {
                oldObject.id == newObject.id &&
                        oldObject.bookingCode == newObject.bookingCode
                oldObject.trackingCode == newObject.trackingCode
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