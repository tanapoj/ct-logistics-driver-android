package com.scgexpress.backoffice.android.ui.delivery.retention.reason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.model.delivery.RetentionScanReason
import com.scgexpress.backoffice.android.ui.dialog.RetentionSelectSubReasonDialogFragment
import kotlinx.android.synthetic.main.list_retention_scan_items.view.*

class RetentionReasonAdapter(val viewModelRetentionReason: RetentionReasonViewModel) : RecyclerView.Adapter<RetentionReasonAdapter.RetentionScanViewHolder>() {

    var data: List<RetentionScanReason> = listOf()

    private var selectPosition = -1
    private var onItemClickListener: AdapterView.OnItemClickListener? = null

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetentionScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_retention_scan_items, parent, false)
        return RetentionScanViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: RetentionScanViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.itemView.radioReason.isChecked = selectPosition == position
        holder.itemView.radioReason.setOnClickListener {
            selectPosition = holder.adapterPosition
            notifyItemRangeChanged(0, data.size)
            holder.mAdapter?.onItemHolderClick(holder)
            val activity = holder.itemView.context as? RetentionReasonActivity
            val fragment = RetentionSelectSubReasonDialogFragment.newInstance()
            var bundle = Bundle().apply {
                putString("Id",item.id)
                putString("Reason",item.reason)
            }.also {
                fragment.arguments = it
                fragment.show(activity!!.supportFragmentManager, Const.PARAMS_DIALOG_RE_STATUS)
            }
        }
        holder.bind(item)
    }

    private fun onItemHolderClick(holder: RecyclerView.ViewHolder) {
        onItemClickListener?.onItemClick(
            null, holder.itemView,
            holder.adapterPosition, holder.itemId
        )
    }

    class RetentionScanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mAdapter: RetentionReasonAdapter? = null
        fun bind(item: RetentionScanReason) = with(itemView) {
            tvReason.text = item.reason
        }
    }
}