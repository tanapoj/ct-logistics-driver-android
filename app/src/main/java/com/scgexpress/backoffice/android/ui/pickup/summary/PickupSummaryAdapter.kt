package com.scgexpress.backoffice.android.ui.pickup.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import com.scgexpress.backoffice.android.model.Title
import kotlinx.android.synthetic.main.list_entry_pickup_summary_item.view.*

class PickupSummaryAdapter(private val viewModel: PickupSummaryViewModel) :
    RecyclerView.Adapter<PickupSummaryAdapter.PickupSummaryViewHolder>() {

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
            val result = DiffUtil.calculateDiff(PickupSummaryDiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupSummaryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TITLE) {
            val view = inflater.inflate(R.layout.list_entry_pickup_summary_title, parent, false)
            return PickupSummaryTitleViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM) {
            val view = inflater.inflate(R.layout.list_entry_pickup_summary_item, parent, false)
            return PickupSummaryItemViewHolder(view)
        }
        return PickupSummaryViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PickupSummaryViewHolder, position: Int) {
        if (holder is PickupSummaryTitleViewHolder) {
            holder.bind(viewModel)

        } else if (holder is PickupSummaryItemViewHolder) {
            val item = data[holder.adapterPosition] as PickupScanningTrackingEntity
            holder.bind(item, viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Title -> VIEWTYPE_TITLE
            is PickupScanningTrackingEntity -> VIEWTYPE_ITEM
            else -> super.getItemViewType(position)
        }

    }

    open class PickupSummaryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class PickupSummaryTitleViewHolder(view: View) : PickupSummaryViewHolder(view) {

        fun bind(viewModel: PickupSummaryViewModel) = with(itemView) {
        }

    }

    class PickupSummaryItemViewHolder(view: View) : PickupSummaryViewHolder(view) {

        fun bind(item: PickupScanningTrackingEntity, viewModel: PickupSummaryViewModel) = with(itemView) {
            val trackingID = item.tracking.toTrackingId()
            if (trackingID == "") {
                tvCode.text = item.tracking
            } else {
                tvCode.text = trackingID
            }

            tvZipCode.text = item.zipcode
            tvSize.text = item.sizeName
            tvService.text = item.serviceName
            tvPrice.text = Utils.setCurrencyFormat(item.deliveryFee)
            tvCodPercentage.text = Utils.setCurrencyFormat(item.codAmount!!)
            val codFee = "(" + Utils.setCurrencyFormat(item.codFee!!) + ")"
            tvCod.text = codFee
            tvCarton.text = item.cartonFee?.toCurrencyFormat() ?: "0"

            itemView.setOnClickListener {
                viewModel.showDialogConfirmDeletePickup(itemView.context, item.tracking)
            }
            if (item.senderImgPath.isNullOrBlank() && item.senderImgUrl.isNullOrBlank() &&
                item.receiverImgUrl.isNullOrBlank() && item.receiverImgPath.isNullOrBlank()
            ) {
                btnPhotos.visibility = View.INVISIBLE
            } else {
                btnPhotos.visibility = View.VISIBLE
            }
            btnPhotos.setOnClickListener {
                viewModel.viewPhoto(item)
            }

            if(item.senderImgPath == null && item.receiverImgPath == null){
                btnPhotos.visibility = View.GONE
            }
        }
    }

    class PickupSummaryDiffCallback(private val o: List<Any>, private val n: List<Any>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is PickupScanningTrackingEntity && newObject is PickupScanningTrackingEntity) {
                oldObject.tracking == newObject.tracking &&
                        oldObject.zipcode == newObject.zipcode &&
                        oldObject.deliveryFee == newObject.deliveryFee
            } else if (oldObject is Title && newObject is Title)
                oldObject.title == newObject.title
            else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}