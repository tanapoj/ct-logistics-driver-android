package com.scgexpress.backoffice.android.ui.notification

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.model.MetaBooking
import kotlinx.android.synthetic.main.list_entry_notification_item.view.*
import timber.log.Timber

class NotificationAdapter(private val viewModel: NotificationViewModel) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    var data: List<NotificationModel> = listOf()
        set(value) {
            val result = DiffUtil.calculateDiff(MenuDiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_entry_notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        try {
            val item = data[position]
            holder.bind(item, viewModel)

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val intColor = ContextCompat.getColor(view.context, R.color.grayLight)

        fun bind(item: NotificationModel, viewModel: NotificationViewModel) = with(itemView) {
            var metaBooking = MetaBooking()

            txtDatePickup.text = item.category
            txtDateTime.text = viewModel.getDateTime(item.timestamp)
            txtMessage.text = item.message

            if (item.category == "PICKUP_BOOKINGS" || item.category == "PICKUP BOOKINGS") {
                btnGo.visibility = View.VISIBLE
                btnTel.visibility = View.GONE
                btnLocation.visibility = View.GONE

                if (item.meta != "") {
                    metaBooking = Utils.convertStringToMetaBooking(item.meta)
                    txtBookingID.text = "Booking ID : " + metaBooking.bookingID + "\n"
                    //txtMessage.text = "BookingInfo ID : " + metaBooking.bookingID + "\n\n" + metaBooking.description
                } else {
                    txtMessage.text = item.message
                }

            } else {
                btnTel.visibility = View.GONE
                btnLocation.visibility = View.GONE
                if (context != null) {
                    btnGo.text = context.resources.getString(R.string.ok)
                }
            }

            if (item.type == "CANCEL") {
                btnGo.text = btnGo.context.resources.getString(R.string.ok)
            } else {
                btnGo.text = btnGo.context.resources.getString(R.string.go)
            }

            btnTel.setOnClickListener {
                //disableItem(holder)
                viewModel.acceptBooking(metaBooking)
            }
            btnLocation.setOnClickListener {
                //disableItem(holder)
                viewModel.rejectBooking(metaBooking)
            }
            btnGo.setOnClickListener {
                //disableItem(holder)
                viewModel.goBooking(metaBooking.bookingID)
            }

            if (item.seen) {
                disableItem(this)
            }
        }

        private fun disableItem(holder: View) {
            holder.cvNotification.alpha = 0.5f
            holder.btnTel.isEnabled = false
            holder.btnLocation.isEnabled = false
            holder.btnGo.isEnabled = false

            holder.imgNotification.setColorFilter(intColor, PorterDuff.Mode.SRC_IN)
        }
    }

    class MenuDiffCallback(private val o: List<NotificationModel>, private val n: List<NotificationModel>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = o[oldItemPosition] === n[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = o[oldItemPosition] == n[newItemPosition]

    }
}