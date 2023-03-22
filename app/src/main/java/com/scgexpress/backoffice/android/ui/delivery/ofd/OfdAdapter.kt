package com.scgexpress.backoffice.android.ui.delivery.ofd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.ui.delivery.DeliveryViewModel
import kotlinx.android.synthetic.main.list_entry_ofd.view.*

class OfdAdapter(val viewModel: DeliveryViewModel) : RecyclerView.Adapter<OfdAdapter.OfdViewHolder>() {

    var data: List<Manifest> = listOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfdViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_entry_ofd, parent, false)
        return OfdViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: OfdViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.bind(item, viewModel)
    }


    class OfdViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Manifest, viewModel: DeliveryViewModel) = with(itemView) {
            /*holder.padding.visibility = if (holder.adapterPosition >= data.size - 1) View.VISIBLE
        else View.GONE*/

            tvOfdId.text = item.barcode
            var statusColorRes = R.color.colorIncomplete

            when {
                item.isNewOfd() -> {
                    tvStatus.text = context.getString(R.string.new_ofd)
                    ivStatus.setImageResource(R.drawable.ic_baseline_add_circle)
                    statusColorRes = R.color.colorNew
                }
                item.isIncompleteOfd() -> {
                    tvStatus.text = context.getString(R.string.incomplete)
                    ivStatus.setImageResource(R.drawable.ic_baseline_error_white)
                    statusColorRes = R.color.colorIncomplete
                }
                item.isCompletedOfd() -> {
                    tvStatus.text = context.getString(R.string.completed)
                    ivStatus.setImageResource(R.drawable.ic_baseline_check_circle_white)
                    statusColorRes = R.color.colorCompleted
                }
                else -> {
                    tvStatus.text = ""
                    ivStatus.visibility = View.GONE
                }
            }

            val statusColor = ContextCompat.getColor(context, statusColorRes)

            tvStatus.setTextColor(statusColor)
            ivStatus.setColorFilter(statusColor)

            try {
                tvRemain.text = item.getRemainsPickup().toString()
                tvOfdRemain.text = item.getRemainsOfd().toString()
            } catch (e: Exception) {
                tvRemain.text = 0.toString()
                tvOfdRemain.text = 0.toString()
            }

            tvPicked.text = item.bookingsDone
            tvTotal.text = item.bookingsTotal

            tvDeliveryDelivered.text = item.noOfItemsDelivered
            tvDeliveryRetention.text = item.noOfItemsRetention
            tvDeliveryTotal.text = item.noOfItemsTotal

            tvDate.text = item.lastModified

            itemView.setOnClickListener { viewModel.ofdClick(item) }
            btnScan.setOnClickListener { viewModel.ofdScan(item) }
            btnSent.setOnClickListener { viewModel.ofdSent(item) }
            btnCantSent.setOnClickListener { viewModel.ofdCantSent(item) }
        }
    }

    class DiffCallback(private val o: List<Manifest>, private val n: List<Manifest>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is Manifest && newObject is Manifest) {
                oldObject.id == newObject.id &&
                        oldObject.barcode == newObject.barcode &&
                        oldObject.dateCreated == newObject.dateCreated
            } else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]

    }
}