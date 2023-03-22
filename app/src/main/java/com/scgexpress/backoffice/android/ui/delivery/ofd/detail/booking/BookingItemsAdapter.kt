package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.model.TrackingNoByBooking
import kotlinx.android.synthetic.main.list_entry_booking_summary_item.view.*

class BookingItemsAdapter : RecyclerView.Adapter<BookingItemsAdapter.BookingItemsViewHolder>() {

    companion object {
        const val VIEWTYPE_TITLE = 1
        const val VIEWTYPE_ITEM = 2
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(BookingItemsDiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TITLE) {
            val view = inflater.inflate(R.layout.list_entry_booking_summary_title, parent, false)
            return BookingItemsTitleViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM) {
            val view = inflater.inflate(R.layout.list_entry_booking_summary_item, parent, false)
            return BookingItemsItemViewHolder(view)
        }
        return BookingItemsViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BookingItemsViewHolder, position: Int) {
        if (holder is BookingItemsTitleViewHolder) {
            holder.bind()

        } else if (holder is BookingItemsItemViewHolder) {
            val item = data[holder.adapterPosition] as TrackingNoByBooking
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is BookingSummaryTitle -> VIEWTYPE_TITLE
            is TrackingNoByBooking -> VIEWTYPE_ITEM
            else -> super.getItemViewType(position)
        }

    }

    open class BookingItemsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class BookingItemsTitleViewHolder(view: View) : BookingItemsViewHolder(view) {
        fun bind() = with(itemView) {
        }
    }

    class BookingItemsItemViewHolder(view: View) : BookingItemsViewHolder(view) {
        fun bind(item: TrackingNoByBooking) = with(itemView) {
            val trackingID = item.trackingNumber.toTrackingId()
            if (trackingID == "") {
                tvTracking.text = item.trackingNumber
            } else {
                tvTracking.text = trackingID
            }

            tvIdParcel.text = item.idParcel
            tvIdStatus.text = item.idStatus
            tvBookingId.text = item.bookingId
            tvIdManifestSheet.text = item.idManifestSheet
        }
    }

    class BookingItemsDiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is TrackingNoByBooking && newObject is TrackingNoByBooking) {
                oldObject.id == newObject.id &&
                        oldObject.trackingNumber == newObject.trackingNumber &&
                        oldObject.idParcel == newObject.idParcel
            } else if (oldObject is BookingSummaryTitle && newObject is BookingSummaryTitle)
                oldObject.id == newObject.id
            else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}