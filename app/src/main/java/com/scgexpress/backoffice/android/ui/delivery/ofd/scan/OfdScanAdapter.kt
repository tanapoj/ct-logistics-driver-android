package com.scgexpress.backoffice.android.ui.delivery.ofd.scan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.model.Delivery
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelResponse
import com.scgexpress.backoffice.android.model.DeliveryOfdScanTitle
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import kotlinx.android.synthetic.main.list_entry_ofd_scan_item.view.*
import timber.log.Timber

class OfdScanAdapter(
        private val viewModel: OfdScanViewModel
) : RecyclerView.Adapter<OfdScanAdapter.BaseViewHolder>() {

    companion object {
        const val VIEWTYPE_TITLE = 1
        const val VIEWTYPE_ITEM = 2
    }

    private val itemTitle = DeliveryTask("")

    var items: List<DeliveryTask> = arrayListOf()
        set(value) {
            val new = listOf(itemTitle) + value
            Timber.d("DiffUtil: old = ${field.size} $field")
            Timber.d("DiffUtil: new = ${new.size} $new")
            val result = DiffUtil.calculateDiff(ScanOfdDiffCallback(field, new))
            result.dispatchUpdatesTo(this)
            field = new
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_TITLE -> {
                val view = inflater.inflate(R.layout.list_entry_ofd_scan_title, parent, false)
                TitleViewHolder(view)
            }
            VIEWTYPE_ITEM -> {
                val view = inflater.inflate(R.layout.list_entry_ofd_scan_item, parent, false)
                ItemViewHolder(view)
            }
            else -> BaseViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = if (items.size > 1) items.size else 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is TitleViewHolder -> {
            }
            is ItemViewHolder -> {
                val item = items[position]
                holder.bind(item, viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEWTYPE_TITLE
            else -> VIEWTYPE_ITEM
        }

    }


    open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class TitleViewHolder(view: View) : BaseViewHolder(view)

    class ItemViewHolder(view: View) : BaseViewHolder(view) {

        fun bind(item: DeliveryTask, viewModel: OfdScanViewModel) = with(itemView) {
            tvCode.text = item.trackingCode.toTrackingId()
            tvSenderCode.text = item.senderCode
            tvSenderName.text = item.senderName
            tvCod.text = Utils.setCurrencyFormat(item.codAmount ?: .0)
            tvOrderDate.text = item.deliveryAt

            itemView.setOnLongClickListener {
                viewModel.removeTrackingCode(item.trackingCode)
                true
            }
        }
    }

    class ScanOfdDiffCallback(
            private val o: List<DeliveryTask>,
            private val n: List<DeliveryTask>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = o.size
        override fun getNewListSize(): Int = n.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = o[oldItemPosition].trackingCode == n[newItemPosition].trackingCode
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = o[oldItemPosition] == n[newItemPosition]
    }
}