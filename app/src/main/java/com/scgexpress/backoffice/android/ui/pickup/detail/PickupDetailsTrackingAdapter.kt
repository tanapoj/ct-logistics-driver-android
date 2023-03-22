package com.scgexpress.backoffice.android.ui.pickup.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.tracking.SubmitTracking
import kotlinx.android.synthetic.main.list_entry_pickup_summary_item.view.*

class PickupDetailsTrackingAdapter(private val viewModel: PickupDetailsViewModel) :
    RecyclerView.Adapter<PickupDetailsTrackingAdapter.PickupDetailsTrackingViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupDetailsTrackingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TITLE) {
            val view = inflater.inflate(R.layout.list_entry_pickup_summary_title, parent, false)
            return PickupDetailsTrackingTitleViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM) {
            val view = inflater.inflate(R.layout.list_entry_pickup_summary_item, parent, false)
            return PickupDetailsTrackingItemViewHolder(view)
        }
        return PickupDetailsTrackingViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PickupDetailsTrackingViewHolder, position: Int) {
        if (holder is PickupDetailsTrackingTitleViewHolder) {
            holder.bind(viewModel)

        } else if (holder is PickupDetailsTrackingItemViewHolder) {
            val item = data[holder.adapterPosition] as SubmitTracking
            holder.bind(item, viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Title -> VIEWTYPE_TITLE
            is SubmitTracking -> VIEWTYPE_ITEM
            else -> super.getItemViewType(position)
        }

    }

    open class PickupDetailsTrackingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class PickupDetailsTrackingTitleViewHolder(view: View) : PickupDetailsTrackingViewHolder(view) {

        fun bind(viewModel: PickupDetailsViewModel) = with(itemView) {
            /*if (viewModelRetentionReason.businessCustomer.value!!) {
                containerDeliveryTitle.visibility = View.GONE
                tvCodTitle.visibility = View.GONE
                vDividerTitle3.visibility = View.GONE
                vDividerTitle4.visibility = View.GONE
            } else {
                containerDeliveryTitle.visibility = View.VISIBLE
                tvCodTitle.visibility = View.VISIBLE
                vDividerTitle3.visibility = View.VISIBLE
                vDividerTitle4.visibility = View.VISIBLE
            }*/
        }

    }

    class PickupDetailsTrackingItemViewHolder(view: View) : PickupDetailsTrackingViewHolder(view) {

        fun bind(item: SubmitTracking, viewModel: PickupDetailsViewModel) = with(itemView) {
            /*if (viewModelRetentionReason.businessCustomer.value!!) {
                containerDelivery.visibility = View.GONE
                containerCod.visibility = View.GONE
                vDivider3.visibility = View.GONE
                vDivider4.visibility = View.GONE
            } else {
                containerDelivery.visibility = View.VISIBLE
                containerCod.visibility = View.VISIBLE
                vDivide r3.visibility = View.VISIBLE
                vDivider4.visibility = View.VISIBLE
            }*/
            val trackingID = item.tracking.toTrackingId()
            if (trackingID == "") {
                tvCode.text = item.tracking
            } else {
                tvCode.text = trackingID
            }

            tvZipCode.text = item.zipcode
            tvService.text = item.serviceName
            tvSize.text = item.sizeName
            tvPrice.text = Utils.setCurrencyFormat(item.deliveryFee)
            tvCodPercentage.text = Utils.setCurrencyFormat(item.codFee ?: .0)
            val cod = "("+Utils.setCurrencyFormat(item.codAmount ?: .0)+")"
            tvCod.text = cod
            tvCarton.text = item.cartonFee?.toCurrencyFormat() ?: "0"

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
        }
    }

    class PickupSummaryDiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is SubmitTracking && newObject is SubmitTracking) {
                oldObject.tracking == newObject.tracking &&
                        oldObject.zipcode == newObject.zipcode &&
                        oldObject.sizeId == newObject.sizeId
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