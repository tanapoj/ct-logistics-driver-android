package com.scgexpress.backoffice.android.ui.pickup.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.toTrackingId
import com.scgexpress.backoffice.android.db.entity.pickup.PickupScanningTrackingEntity
import kotlinx.android.synthetic.main.list_entry_pickup_main_extra.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_main_tracking.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_main_tracking.view.tvPin
import kotlinx.android.synthetic.main.list_entry_pickup_main_extra.view.tvPin as extraPin
import kotlinx.android.synthetic.main.list_entry_pickup_main_extra.view.tvOrderId as extraOrderId
import kotlinx.android.synthetic.main.list_entry_pickup_main_extra.view.tvTracking as extraTracking
import kotlinx.android.synthetic.main.list_entry_pickup_main_tracking.view.tvOrderId as normalOrderId
import kotlinx.android.synthetic.main.list_entry_pickup_main_tracking.view.tvTracking as normalTracking

class PickupMainAdapter(val viewModel: PickupMainViewModel, val type: ScanStatus) :
    RecyclerView.Adapter<PickupMainAdapter.BaseViewHolder>() {

    enum class ScanStatus {
        Pending, Scanned
    }

    companion object {
        const val VIEWTYPE_TRACKING = 1
        const val VIEWTYPE_EXTRA = 2
    }

    var data: List<PickupScanningTrackingEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_TRACKING -> {
                val view = inflater.inflate(R.layout.list_entry_pickup_main_tracking, parent, false)
                TrackingItemViewHolder(view)
            }
            VIEWTYPE_EXTRA -> {
                val view = inflater.inflate(R.layout.list_entry_pickup_main_extra, parent, false)
                ExtraItemViewHolder(view)
            }
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        when (holder) {
            is TrackingItemViewHolder -> holder.bind(item)
            is ExtraItemViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int) = when {
        data[position].isExtra -> VIEWTYPE_EXTRA
        else -> VIEWTYPE_TRACKING
    }

    open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class TrackingItemViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(tracking: PickupScanningTrackingEntity) = with(itemView) {
            normalTracking.text = tracking.tracking.toTrackingId()
            normalOrderId.text = tracking.orderId

            if (tracking.pinbox.isNullOrBlank()) {
                tvPin.visibility = View.VISIBLE
                tvPin.text = tracking.pinbox
            } else {
                tvPin.visibility = View.GONE
                tvPin.text = ""
            }

            setOnLongClickListener {
                when (type) {
                    ScanStatus.Pending -> {
                        viewModel.setTrackingCode(tracking.tracking, true)
                        true
                    }
                    ScanStatus.Scanned -> {
                        viewModel.holdBackScannedTracking(tracking.tracking)
                        true
                    }
                }
            }
        }
    }

    inner class ExtraItemViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(tracking: PickupScanningTrackingEntity) = with(itemView) {
            extraTracking.text = tracking.tracking.toTrackingId()
            if (tracking.orderId.isBlank()) {
                extraOrderId.visibility = View.INVISIBLE
            } else {
                extraOrderId.visibility = View.VISIBLE
                extraOrderId.text = tracking.orderId
            }

            if (tracking.pinbox.isNullOrBlank()) {
                extraPin.visibility = View.VISIBLE
                extraPin.text = tracking.pinbox
            } else {
                extraPin.visibility = View.GONE
                extraPin.text = ""
            }

            setOnLongClickListener {
                when (type) {
                    ScanStatus.Pending -> {
                        true
                    }
                    ScanStatus.Scanned -> {
                        viewModel.removeScannedTracking(tracking.tracking)
                        true
                    }
                }
            }
        }
    }
}