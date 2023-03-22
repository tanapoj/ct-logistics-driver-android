package com.scgexpress.backoffice.android.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel

class RejectStatusDialogAdapter : RecyclerView.Adapter<RejectStatusDialogAdapter.ReStatusDialogViewHolder>() {

    var data: List<BookingRejectStatusModel> = listOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    var selectedPosition: Int = -1
    var isOther: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReStatusDialogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.dialog_list_entry_ofd_re_status, parent, false)
        return ReStatusDialogViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ReStatusDialogViewHolder, position: Int) {
        val item = data[holder.adapterPosition]

        holder.rdbText.text = item.catOrderName

        holder.rdbText.isChecked = selectedPosition == position

        holder.rdbText.setOnClickListener {
            selectedPosition = position
            isOther = selectedPosition == data.lastIndex
            notifyDataSetChanged()
        }
    }

    class ReStatusDialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rdbText: RadioButton = view.findViewById(R.id.rdbText)
    }

    class DiffCallback(private val o: List<BookingRejectStatusModel>, private val n: List<BookingRejectStatusModel>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]
            //if we want faster update we also need to implement getPayloadChange
            return o == n
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]

    }
}