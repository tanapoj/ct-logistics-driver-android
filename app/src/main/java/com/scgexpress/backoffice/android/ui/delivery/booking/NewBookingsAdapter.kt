package com.scgexpress.backoffice.android.ui.delivery.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.ui.delivery.DeliveryViewModel
import kotlinx.android.synthetic.main.list_entry_new_bookings.view.*

class NewBookingsAdapter(val viewModel: DeliveryViewModel) :
    RecyclerView.Adapter<NewBookingsAdapter.NewBookingViewHolder>() {

    var data: List<BookingInfo> = listOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBookingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_entry_new_bookings, parent, false)
        return NewBookingViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: NewBookingViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.bind(item, viewModel)
    }

    class NewBookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: BookingInfo, viewModel: DeliveryViewModel) = with(itemView) {
            tvTitle.text = item.bookingID
            tvStatus.text = item.assignStatus
            tvDate.text = item.dateCreated

            val description = "ชื่อ-นามสกุล : " + item.senderName + "\n" +
                    "ที่อยู่ : " + item.senderAddress + "\n" +
                    "โทร : " + item.senderTel + "\n\n" +
                    "วันเวลาเข้ารับสินค้า : " + item.pickupRequestDate + " " + item.pickupRequestTime + "\n" +
                    "รูปแบบบริการ : " + "normal " + item.normalAmount + ", " + "chilled " + item.chilledAmount + ", " + "frozen " + item.frozenAmount + "\n" +
                    "Remark : " + item.remark
            tvAddress.text = description

            btnTel.setOnClickListener { viewModel.bookingAccept(item) }
            btnLocation.setOnClickListener { viewModel.bookingReject(item) }
        }
    }

    class DiffCallback(private val o: List<BookingInfo>, private val n: List<BookingInfo>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] === n[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}