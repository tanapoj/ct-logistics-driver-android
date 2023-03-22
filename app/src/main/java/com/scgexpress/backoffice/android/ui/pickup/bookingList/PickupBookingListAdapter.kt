package com.scgexpress.backoffice.android.ui.pickup.bookingList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.untilLessThan
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import kotlinx.android.synthetic.main.list_entry_pickup_booking.view.*
import kotlinx.android.synthetic.main.list_entry_pickup_booking.view.cvCard
import kotlinx.android.synthetic.main.list_entry_pickup_tracking_without_booking.view.*
import timber.log.Timber

class PickupBookingListAdapter(
        private val viewModel: PickupBookingListViewModel
) : RecyclerView.Adapter<PickupBookingListAdapter.TaskViewHolder>() {

    companion object {
        const val VIEWTYPE_BOOKING = 1
        const val VIEWTYPE_ADD = 2
    }

    var pickupTasks: List<PickupTask> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_BOOKING) {
            val view = inflater.inflate(R.layout.list_entry_pickup_booking, parent, false)
            return BookingViewHolder(view)
        } else if (viewType == VIEWTYPE_ADD) {
            val view = inflater.inflate(R.layout.list_entry_pickup_tracking_without_booking, parent, false)
            return AddViewHolder(view)
        }
        throw IllegalStateException()
    }

    override fun getItemCount(): Int = pickupTasks.size + 1

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (holder is BookingViewHolder) {
            val item = pickupTasks[holder.adapterPosition]
            holder.bind(item)
        } else if (holder is AddViewHolder) {
            holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position in 0 untilLessThan pickupTasks.size -> VIEWTYPE_BOOKING
            isNewTaskPosition(position) -> VIEWTYPE_ADD
            else -> throw IllegalStateException("item index = $position from data size ${pickupTasks.size}")
        }
    }

    private fun isNewTaskPosition(position: Int) = position == pickupTasks.size

    open class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class BookingViewHolder(view: View) : TaskViewHolder(view) {

        fun bind(pickupTask: PickupTask) = with(itemView) {
            tvShipperName.text = "${pickupTask.customerCode} ${pickupTask.customerName}"
            tvBooking.text = "${pickupTask.bookingCode} (${pickupTask.senderName})"
            pickupTask.serviceTypeCount.let { (normal, chiled, frozen) ->
                tvInfo.text = resources.getString(R.string.pickup_task_list_stat, normal, chiled, frozen)
            }

            checkbox.isChecked = viewModel.selectedTask[pickupTask.id] ?: true

            tvScanedTracking.text = "${pickupTask.pickupedCount}"
            tvTotalTracking.text = "${pickupTask.totalCount}"

            cvCard.setOnClickListener {
                val newValue = !checkbox.isChecked
                checkbox.isChecked = newValue
                viewModel.selectTask(pickupTask.id, newValue)
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.selectTask(pickupTask.id, isChecked)
            }
        }

    }

    inner class AddViewHolder(view: View) : TaskViewHolder(view) {

        fun bind() = with(itemView) {
            cvNewTaskCard.setOnClickListener {
                Timber.d("cvNewTaskCard click!")
                viewModel.saveSelectedBookingId()
                viewModel.startScanningActivityWithBlankTask()
            }
        }

    }
}