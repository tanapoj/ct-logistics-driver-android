package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.common.toDateFormat
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.constant.ParcelStatus
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.MasterParcelModel
import com.scgexpress.backoffice.android.model.TrackingInfo
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailViewModel
import kotlinx.android.synthetic.main.list_entry_ofd_tracking_item.view.*

class OfdDetailItemsAdapter(val viewModel: OfdDetailViewModel) :
    RecyclerView.Adapter<OfdDetailItemsAdapter.OfdItemViewHolder>() {
    companion object {
        const val VIEWTYPE_TRACKING = 1
        const val VIEWTYPE_BOOKING = 2
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfdItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TRACKING) {
            val view = inflater.inflate(R.layout.list_entry_ofd_tracking_item, parent, false)
            return OfdTrackingItemViewHolder(view, viewModel)
        } else if (viewType == VIEWTYPE_BOOKING) {
            val view = inflater.inflate(R.layout.list_entry_ofd_booking_item, parent, false)
            return OfdBookingItemViewHolder(view, viewModel)
        }
        return OfdItemViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: OfdItemViewHolder, position: Int) {
        if (holder is OfdTrackingItemViewHolder) {
            val item = data[holder.adapterPosition] as TrackingInfo
            holder.bind(item, holder.adapterPosition)

        } else if (holder is OfdBookingItemViewHolder) {
            val bookingItem = data[holder.adapterPosition] as BookingInfo
            holder.bind(bookingItem, holder.adapterPosition)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is TrackingInfo -> VIEWTYPE_TRACKING
            is BookingInfo -> VIEWTYPE_BOOKING
            else -> super.getItemViewType(position)
        }

    }

    open class OfdItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class OfdTrackingItemViewHolder(view: View, private val viewModel: OfdDetailViewModel) : OfdItemViewHolder(view) {

        fun bind(item: TrackingInfo, position: Int) = with(itemView) {
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

            if (item.recipientParcelImage != null) {
                if (item.recipientParcelImage.isNotEmpty()) {
                    imgPhotoCollection.visibility = View.VISIBLE
                } else imgPhotoCollection.visibility = View.GONE
            } else imgPhotoCollection.visibility = View.GONE

            val recipientName = listOfNotNull("ชื่อ-นามสกุล :", item.recipientName).joinToString(" ")
            tvRecipient.text = recipientName

            val address = "ที่อยู่ : " + listOfNotNull(
                item.recipientAddress, item.recipientDistrict,
                item.recipientProvince, item.recipientZipcode
            ).joinToString(", ")
            tvAddress.text = address

            val tel = listOfNotNull("โทร :", item.recipientTel).joinToString(" ")
            tvTelephone.text = tel

            val remark = listOfNotNull("Remark:", item.userNote).joinToString(" ")
            tvRemark.text = remark

            if (item.codAmount != null && trackingID.startsWith("11")) {
                if (item.codAmount != "") {
                    codContainer.visibility = View.VISIBLE
                    tvCod.text = item.codAmount!!.toDouble().toCurrencyFormat()
                } else {
                    codContainer.visibility = View.GONE
                    tvCod.text = ""
                }
            }

            itemView.setOnClickListener { viewModel.itemTrackingClick(item) }
            itemView.setOnLongClickListener {
                if (item.idStatus == "7")
                    viewModel.itemTrackingLongClick(position)
                true
            }
            btnTel.setOnClickListener { if (item.recipientTel != null) viewModel.phoneCall(item.recipientTel) }
            btnLocation.setOnClickListener {}
            btnCamera.setOnClickListener { viewModel.photo(item.trackingNumber!!) }
        }
    }

    class OfdBookingItemViewHolder(view: View, private val viewModel: OfdDetailViewModel) : OfdItemViewHolder(view) {

        fun bind(item: BookingInfo, position: Int) = with(itemView) {
            tvTitle.text = item.bookingID
            if (item.assignStatus == "") {
                tvStatus.text = context.getString(R.string.unknown)
            } else {
                tvStatus.text = item.assignStatus
            }
            tvDate.text = item.dateCreated
            val description = "ชื่อ-นามสกุล : " + item.senderName + "\n" +
                    "ที่อยู่ : " + item.contactAddress + "\n" +
                    "โทร : " + item.senderTel + "\n\n" +
                    "วันเวลาเข้ารับสินค้า : " + item.pickupRequestDate + " " + item.pickupRequestTime + "\n" +
                    "รูปแบบบริการ : " + "normal " + item.normalAmount + ", " + "chilled " + item.chilledAmount + ", " + "frozen " + item.frozenAmount + "\n" +
                    "Remark : " + item.remark + "\n"
            tvAddress.text = description
            //val tel = "Tel: " + item.senderTel
            //tvTelephone.text = tel

            itemView.setOnClickListener { viewModel.itemBookingClick(item) }
            itemView.setOnLongClickListener {
                if (item.assignStatus != "done")
                    viewModel.itemTrackingLongClick(position)
                true
            }
            btnTel.setOnClickListener { if (item.senderTel != null) viewModel.phoneCall(item.senderTel) }
            btnLocation.setOnClickListener {}
        }
    }

    class DiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is TrackingInfo && newObject is TrackingInfo) {
                oldObject.id == newObject.id &&
                        oldObject.trackingNumber == newObject.trackingNumber &&
                        oldObject.runningNumber == newObject.runningNumber
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