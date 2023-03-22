package com.scgexpress.backoffice.android.ui.delivery.ofd.cantsent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.Delivery
import com.scgexpress.backoffice.android.model.DeliveryOfdScanTitle
import kotlinx.android.synthetic.main.list_entry_ofd_cant_sent_item.view.*

class OfdCantSentAdapter(private val viewModel: OfdCantSentViewModel) :
    RecyclerView.Adapter<OfdCantSentAdapter.ScanOfdViewHolder>() {

    companion object {
        const val VIEWTYPE_TITLE = 1
        const val VIEWTYPE_ITEM = 2
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanOfdViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TITLE) {
            val view = inflater.inflate(R.layout.list_entry_ofd_cant_sent_title, parent, false)
            return ScanOfdTitleViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM) {
            val view = inflater.inflate(R.layout.list_entry_ofd_cant_sent_item, parent, false)
            return ScanOfdItemViewHolder(view)
        }
        return ScanOfdViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ScanOfdViewHolder, position: Int) {
        if (holder is ScanOfdItemViewHolder) {
            val item = data[holder.adapterPosition] as Delivery
            holder.bind(item, viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is DeliveryOfdScanTitle -> VIEWTYPE_TITLE
            is Delivery -> VIEWTYPE_ITEM
            else -> super.getItemViewType(position)
        }

    }


    open class ScanOfdViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ScanOfdTitleViewHolder(view: View) : ScanOfdViewHolder(view) {

        fun bind() = with(itemView) {
        }
    }

    class ScanOfdItemViewHolder(view: View) : ScanOfdViewHolder(view) {

        fun bind(item: Delivery, viewModel: OfdCantSentViewModel) = with(itemView) {
            tvCode.text = item.trackingNumber
            tvSender.text = item.senderCode + "\n" + item.senderName
            try {
                tvCod.text = Utils.setCurrencyFormat(item.codAmount!!)
            } catch (ex: Exception) {
            }
            tvReason.text = viewModel.getRetentionName(item.statusID!!)
            tvNote.text = item.note

            itemView.setOnClickListener {
                viewModel.showDialogConfirmDeleteTracking(itemView.context, item)
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
                        oldObject.manifestID == newObject.manifestID
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