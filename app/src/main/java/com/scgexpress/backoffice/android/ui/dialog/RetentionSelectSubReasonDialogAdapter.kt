package com.scgexpress.backoffice.android.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.delivery.RetentionScanSubReason
import kotlinx.android.synthetic.main.list_retention_scan_items.view.*

class RetentionSelectSubReasonDialogAdapter(
    val viewModel: RetentionSelectSubReasonDialogViewModel
): RecyclerView.Adapter<RetentionSelectSubReasonDialogAdapter.RetentionSelectSubReasonDialogViewHolder>() {

    var data: List<RetentionScanSubReason> = listOf()

    private var selectPosition = -1
    private var onItemClickListener: AdapterView.OnItemClickListener? = null

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RetentionSelectSubReasonDialogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_retention_scan_items, parent, false)
        return RetentionSelectSubReasonDialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: RetentionSelectSubReasonDialogViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.itemView.radioReason.isChecked = selectPosition == position
        holder.itemView.radioReason.setOnClickListener {
            selectPosition = holder.adapterPosition
            notifyItemRangeChanged(0, data.size)
            holder.mAdapter?.onItemHolderClick(holder)
            viewModel.isSelected = true
            viewModel.itemClick(item)
        }
        holder.bind(item)
    }

    private fun onItemHolderClick(holder: RecyclerView.ViewHolder) {
        onItemClickListener?.onItemClick(
            null, holder.itemView,
            holder.adapterPosition, holder.itemId
        )
    }

    class RetentionSelectSubReasonDialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mAdapter: RetentionSelectSubReasonDialogAdapter? = null
        fun bind(item: RetentionScanSubReason) = with(itemView) {
            tvReason.text = item.subReason
            vDivider.visibility = View.INVISIBLE
        }
    }
}