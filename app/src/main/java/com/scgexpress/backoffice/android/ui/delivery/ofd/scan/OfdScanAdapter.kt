package com.scgexpress.backoffice.android.ui.delivery.ofd.scan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.Delivery
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelResponse
import com.scgexpress.backoffice.android.model.DeliveryOfdScanTitle
import kotlinx.android.synthetic.main.list_entry_ofd_scan_item.view.*

class OfdScanAdapter(private val viewModel: OfdScanViewModel) :
    RecyclerView.Adapter<OfdScanAdapter.ScanOfdViewHolder>() {

    companion object {
        const val VIEWTYPE_TITLE = 1
        const val VIEWTYPE_ITEM_LOCAL = 2
        const val VIEWTYPE_ITEM_NETWORK = 3
    }

    var showDelete: Boolean = true
        set(value) {
            val isChange = value != field
            field = value
            if (isChange) notifyDataSetChanged()
        }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(ScanOfdDiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    var manifestID: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanOfdViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TITLE) {
            val view = inflater.inflate(R.layout.list_entry_ofd_scan_title, parent, false)
            return ScanOfdTitleViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM_LOCAL) {
            val view = inflater.inflate(R.layout.list_entry_ofd_scan_item, parent, false)
            return ScanOfdItemLocalViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM_NETWORK) {
            val view = inflater.inflate(R.layout.list_entry_ofd_scan_item, parent, false)
            return ScanOfdItemNetworkViewHolder(view)
        }
        return ScanOfdViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ScanOfdViewHolder, position: Int) {
        when (holder) {
            is ScanOfdTitleViewHolder -> holder.bind(showDelete)
            is ScanOfdItemLocalViewHolder -> {
                val item = data[holder.adapterPosition] as Delivery
                holder.bind(manifestID, item, showDelete, viewModel)
            }
            is ScanOfdItemNetworkViewHolder -> {
                val item = data[holder.adapterPosition] as DeliveryOfdParcelResponse
                holder.bind(manifestID, item, showDelete, viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is DeliveryOfdScanTitle -> VIEWTYPE_TITLE
            is Delivery -> VIEWTYPE_ITEM_LOCAL
            is DeliveryOfdParcelResponse -> VIEWTYPE_ITEM_NETWORK
            else -> super.getItemViewType(position)
        }

    }


    open class ScanOfdViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ScanOfdTitleViewHolder(view: View) : ScanOfdViewHolder(view) {

        fun bind(showDelete: Boolean) = with(itemView) {
            /*tvDeleteTitle.visibility = if (showDelete) View.VISIBLE else View.GONE
            vDividerTitle.visibility = if (showDelete) View.VISIBLE else View.GONE*/
        }
    }

    class ScanOfdItemLocalViewHolder(view: View) : ScanOfdViewHolder(view) {

        fun bind(
            manifestID: String, item: Delivery, showDelete: Boolean,
            viewModel: OfdScanViewModel
        ) = with(itemView) {
            tvCode.text = item.trackingNumber
            tvSender.text = item.senderCode
            try {
                tvCod.text = Utils.setCurrencyFormat(item.codAmount!!)
            } catch (ex: Exception) {
            }
            tvOrderDate.text = item.orderDate

            itemView.setOnClickListener {
                viewModel.showDialogConfirmDeleteTracking(itemView.context, manifestID, item)
            }
        }
    }

    class ScanOfdItemNetworkViewHolder(view: View) : ScanOfdViewHolder(view) {

        fun bind(
            manifestID: String, item: DeliveryOfdParcelResponse, showDelete: Boolean,
            viewModel: OfdScanViewModel
        ) = with(itemView) {
            tvCode.text = item.trackingId
            tvSender.text = item.parcelsInfo.senderCode
            try {
                tvCod.text = Utils.setCurrencyFormat(item.parcelsInfo.codAmount)
            } catch (ex: Exception) {
            }
            tvOrderDate.text = item.parcelsInfo.userOrderDate

            itemView.setOnClickListener {
                viewModel.showDialogConfirmDeleteTracking(itemView.context, manifestID, item)
            }
        }
    }

    class ScanOfdDiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is Delivery && newObject is Delivery) {
                oldObject.trackingNumber == newObject.trackingNumber &&
                        oldObject.statusID == newObject.statusID &&
                        oldObject.timestamp == newObject.timestamp
            } else if (oldObject is DeliveryOfdParcelResponse && newObject is DeliveryOfdParcelResponse) {
                oldObject.trackingId == newObject.trackingId &&
                        oldObject.parcelsInfo == newObject.parcelsInfo
            } else if (oldObject is DeliveryOfdScanTitle && newObject is DeliveryOfdScanTitle) {
                oldObject.id == newObject.id
            } else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}