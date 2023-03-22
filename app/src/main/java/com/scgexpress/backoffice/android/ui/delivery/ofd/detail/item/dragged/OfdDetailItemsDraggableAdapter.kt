package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.common.toDateFormat
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.constant.ParcelStatus
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.MasterParcelModel
import com.scgexpress.backoffice.android.model.TrackingInfo
import kotlinx.android.synthetic.main.list_entry_ofd_tracking_item_draggable.view.*
import timber.log.Timber

class OfdDetailItemsDraggableAdapter(private val viewModel: OfdDetailItemsDraggableViewModel) :
    RecyclerView.Adapter<OfdDetailItemsDraggableAdapter.OfdItemViewHolder>(),
    DraggableItemAdapter<OfdDetailItemsDraggableAdapter.OfdItemViewHolder> {
    companion object {
        const val VIEWTYPE_TRACKING = 1
        const val VIEWTYPE_BOOKING = 2
    }

    var data: MutableList<Any>

    init {
        setHasStableIds(true) // this is required for D&D feature.

        data = arrayListOf()
    }

    override fun getItemId(position: Int): Long {
        // need to return stable (= not change even after reordered) value
        return if (data[position] is TrackingInfo) {
            val item = data[position] as TrackingInfo
            if (item.id != null)
                item.id.toLong()
            else 0
        } else if (data[position] is BookingInfo) {
            val item = data[position] as BookingInfo
            if (item.assignID != null)
                item.assignID.toLong()
            else 0
        } else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfdItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TRACKING) {
            val view = inflater.inflate(R.layout.list_entry_ofd_tracking_item_draggable, parent, false)
            return OfdTrackingItemViewHolder(view)
        } else if (viewType == VIEWTYPE_BOOKING) {
            val view = inflater.inflate(R.layout.list_entry_ofd_booking_item_draggable, parent, false)
            return OfdBookingItemViewHolder(view)
        }
        return OfdItemViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: OfdItemViewHolder, position: Int) {
        if (holder is OfdTrackingItemViewHolder) {
            val item = data[holder.adapterPosition] as TrackingInfo
            holder.bind(item, viewModel)

        } else if (holder is OfdBookingItemViewHolder) {
            val bookingItem = data[holder.adapterPosition] as BookingInfo
            holder.bind(bookingItem, viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is TrackingInfo -> VIEWTYPE_TRACKING
            is BookingInfo -> VIEWTYPE_BOOKING
            else -> super.getItemViewType(position)
        }

    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        val movedItem = data.removeAt(fromPosition)
        data.add(toPosition, movedItem)
    }

    override fun onCheckCanStartDrag(holder: OfdItemViewHolder, position: Int, x: Int, y: Int): Boolean {
        return true
    }

    override fun onGetItemDraggableRange(holder: OfdItemViewHolder, position: Int): ItemDraggableRange? {
        return null
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragStarted(position: Int) {
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        try {
            viewModel.updateTrackingPosition(data.toList())
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    open class OfdItemViewHolder(view: View) : AbstractDraggableItemViewHolder(view)

    class OfdTrackingItemViewHolder(view: View) : OfdItemViewHolder(view) {

        fun bind(item: TrackingInfo, viewModel: Any?) = with(itemView) {
            val trackingID: String = if (item.trackingNumber != null) {
                item.trackingNumber!!
            } else ""

            tvTitle.text = trackingID.toTrackingId()

            if (item.idStatus == "") {
                tvStatus.text = context.getString(R.string.unknown)
            } else {
                for (status: MasterParcelModel in ParcelStatus.list) {
                    if (status.id == item.idStatus)
                        tvStatus.text = status.name
                }
            }
            if (item.dateCreated != null) {
                tvDate.text = item.dateCreated.toDateFormat()
            } else tvDate.text = ""

            if (item.recipientImage != null) {
                if (item.recipientImage.isNotEmpty()) {
                    imgPhotoCollection.visibility = View.VISIBLE
                } else imgPhotoCollection.visibility = View.GONE
            } else imgPhotoCollection.visibility = View.GONE

            tvRecipient.text = item.recipientName

            val address = item.recipientAddress + ", " + item.recipientDistrict +
                    ", " + item.recipientProvince + " " + item.recipientZipcode
            tvAddress.text = address

            val tel = item.recipientTel
            tvTelephone.text = tel

            val remark = "Remark: " + item.userNote
            tvRemark.text = remark

            if (item.codAmount != null && trackingID.startsWith("11")) {
                if (item.codAmount != "") {
                    codContainer.visibility = View.VISIBLE
                    tvCod.text = item.codAmount.toDouble().toCurrencyFormat()
                } else {
                    codContainer.visibility = View.GONE
                    tvCod.text = ""
                }
            }
        }
    }

    class OfdBookingItemViewHolder(view: View) : OfdItemViewHolder(view) {

        fun bind(item: BookingInfo, viewModel: Any?) = with(itemView) {
            tvTitle.text = item.bookingID
            if (item.assignStatus == "") {
                tvStatus.text = context.getString(R.string.unknown)
            } else {
                tvStatus.text = item.assignStatus
            }
            tvDate.text = item.dateCreated
            tvAddress.text = item.description
            val tel = "Tel: " + item.senderTel
            tvTelephone.text = tel
        }
    }
}