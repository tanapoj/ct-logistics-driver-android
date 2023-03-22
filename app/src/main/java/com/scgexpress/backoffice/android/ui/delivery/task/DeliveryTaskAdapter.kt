package com.scgexpress.backoffice.android.ui.delivery.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_RETENTION
import com.scgexpress.backoffice.android.common.toCurrencyFormat
import com.scgexpress.backoffice.android.common.toDateTimeFormat
import com.scgexpress.backoffice.android.common.toPhoneNumber
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.delivery.DeliveryReverseFilter
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.model.delivery.TitleDeliveryCompleted
import kotlinx.android.synthetic.main.list_entry_delivery_task_completed.view.*
import kotlinx.android.synthetic.main.list_entry_delivery_task_in_progress.view.*
import kotlinx.android.synthetic.main.list_entry_delivery_task_title_completed.view.*

class DeliveryTaskAdapter(val viewModel: DeliveryTaskViewModel) :
    RecyclerView.Adapter<DeliveryTaskAdapter.DeliveryTaskViewHolder>() {
    companion object {
        const val VIEWTYPE_TITLE_IN_PROGRESS = 1
        const val VIEWTYPE_IN_PROGRESS = 2
        const val VIEWTYPE_COMPLETED = 3
        const val VIEWTYPE_TITLE_COMPLETED = 4
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryTaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_TITLE_IN_PROGRESS -> {
                val view =
                    inflater.inflate(R.layout.list_entry_delivery_task_title_drag, parent, false)
                DeliveryTaskTitleViewHolder(view)
            }
            VIEWTYPE_IN_PROGRESS -> {
                val view =
                    inflater.inflate(R.layout.list_entry_delivery_task_in_progress, parent, false)
                DeliveryTaskInProgressViewHolder(view, viewModel)
            }
            VIEWTYPE_COMPLETED -> {
                val view =
                    inflater.inflate(R.layout.list_entry_delivery_task_completed, parent, false)
                DeliveryTaskCompletedViewHolder(view, viewModel)
            }
            VIEWTYPE_TITLE_COMPLETED -> {
                val view =
                    inflater.inflate(
                        R.layout.list_entry_delivery_task_title_completed,
                        parent,
                        false
                    )
                DeliveryTaskTitleCompletedViewHolder(view, viewModel)
            }
            /* VIEWTYPE_REVERSE_FILTER -> {
                 val view =
                     inflater.inflate(
                         R.layout.list_entry_delivery_task_reverse_filter,
                         parent,
                         false
                     )
                 DeliveryTaskReverseFilterViewHolder(view, viewModelRetentionReason)
             }*/
            else -> DeliveryTaskViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DeliveryTaskViewHolder, position: Int) {
        when (holder) {
            is DeliveryTaskTitleViewHolder -> {
                holder.bind()
            }
            is DeliveryTaskInProgressViewHolder -> {
                val item = data[holder.adapterPosition] as DeliveryTask
                holder.bind(item, holder.adapterPosition)
            }
            is DeliveryTaskCompletedViewHolder -> {
                val item = data[holder.adapterPosition] as DeliveryTask
                holder.bind(item, holder.adapterPosition)
            }
            is DeliveryTaskTitleCompletedViewHolder -> {
                val item = data[holder.adapterPosition] as TitleDeliveryCompleted
                holder.bind(item)
            }
            /*is DeliveryTaskReverseFilterViewHolder -> {
                holder.bind()
            }*/
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Title -> VIEWTYPE_TITLE_IN_PROGRESS
            is TitleDeliveryCompleted -> VIEWTYPE_TITLE_COMPLETED
            is DeliveryTask -> getDeliveryTaskViewType(position, data[position] as DeliveryTask)
            else -> super.getItemViewType(position)
        }
    }

    private fun getDeliveryTaskViewType(position: Int, data: DeliveryTask): Int {
        return when (data.deliveryStatus) {
            PARAMS_DELIVERY_TASK_IN_PROGRESS -> VIEWTYPE_IN_PROGRESS
            PARAMS_DELIVERY_TASK_COMPLETED, PARAMS_DELIVERY_TASK_RETENTION -> VIEWTYPE_COMPLETED
            else -> super.getItemViewType(position)
        }
    }

    open class DeliveryTaskViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class DeliveryTaskTitleViewHolder(view: View) :
        DeliveryTaskViewHolder(view) {
        fun bind() {}
    }

    class DeliveryTaskInProgressViewHolder(
        view: View,
        private val viewModel: DeliveryTaskViewModel
    ) :
        DeliveryTaskViewHolder(view) {

        fun bind(item: DeliveryTask, position: Int) = with(itemView) {

            val customer = listOfNotNull(item.serviceName, item.sizeName).joinToString(" - ")
            tvServiceIP.text = customer

            if (item.createdAt != "") {
                tvDateIP.text = item.createdAt.toDateTimeFormat()
            } else tvDateIP.text = ""

            tvTrackingIP.text = item.trackingCode

            tvBackOrderIP.text = "ค้างส่ง 2 วัน"

            tvCodIP.text = item.codAmount?.toCurrencyFormat()

            val recipientName = listOfNotNull("Recipient :", item.recipientName).joinToString(" ")
            tvRecipientIP.text = recipientName

            val address = "Address : " + item.recipientLocation.address
            tvAddressIP.text = address

            val tel = listOfNotNull("Tel :", item.senderTel.toPhoneNumber()).joinToString(" ")
            tvTelephoneIP.text = tel

            val remark =
                "Remark : ${item.remark} (Delivery time ${item.deliveryAt.toDateTimeFormat()})"
            tvRemarkIP.text = remark

            containerIP.setOnClickListener { viewModel.itemClick(item) }
            btnTelIP.setOnClickListener { if (item.senderTel != "") viewModel.phoneCall(item.senderTel) }
            btnLocationIP.setOnClickListener {
                if (item.recipientLocation.latitude != "" && item.recipientLocation.longitude != "")
                    viewModel.showAddress(item.recipientLocation)
            }
            btnCameraIP.setOnClickListener { viewModel.onActionPickup(item) }
        }
    }

    class DeliveryTaskTitleCompletedViewHolder(
        view: View,
        private val viewModel: DeliveryTaskViewModel
    ) :
        DeliveryTaskViewHolder(view) {

        private val btnReserveList by lazy {
            listOf<Button>(view.btnReverseAll, view.btnReverse, view.btnReverseNon)
        }
        lateinit var dataFilter: List<DeliveryReverseFilter>

        private val adapter: DeliveryTaskAdapter by lazy {
            DeliveryTaskAdapter(viewModel)
        }

        /*private val reverseCounterList: List<Int> by lazy {
            viewModelRetentionReason.mapDataReverse(item.itemList)
        }*/

        fun bind(item: TitleDeliveryCompleted) = with(itemView) {

            dataFilter = viewModel.mapDataReverseCompleted(item.itemList)
            dataFilter.let {
                for (i in it.indices) {
                    btnReserveList[i].text = it[i].title
                }
            }
            initButton()

            val title =
                "${item.title} (${item.itemList.size}) - COD (${viewModel.getCodCount(item.itemList)}) ${viewModel.getCodSum(
                    item.itemList
                )}"
            tvTitleDeliveryCompleted.text = title

            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            layoutManager.reverseLayout = true
            recyclerview.layoutManager = layoutManager
            recyclerview.adapter = adapter
            adapter.data = item.itemList

            containerTitle.setOnClickListener {
                if (containerReverse.visibility == View.GONE) containerReverse.visibility =
                    View.VISIBLE
                else containerReverse.visibility = View.GONE
            }
        }

        private fun initButton() {
            for (i in btnReserveList.indices) {
                btnReserveList[i].setOnClickListener {
                    viewModel.filterList(i, btnReserveList)
                    adapter.data = dataFilter[i].itemList
                }
            }
        }
    }

    class DeliveryTaskCompletedViewHolder(
        view: View,
        private val viewModel: DeliveryTaskViewModel
    ) :
        DeliveryTaskViewHolder(view) {

        fun bind(item: DeliveryTask, position: Int) = with(itemView) {

            val customer = listOfNotNull(item.serviceName, item.sizeName).joinToString(" - ")
            tvServiceCP.text = customer

            if (item.deliveryStatusChangeAt != "") {
                tvDateCP.text = item.deliveryStatusChangeAt?.toDateTimeFormat()
            } else tvDateCP.text = ""

            tvTrackingCP.text = item.trackingCode

            tvCodCP.text = item.codAmount?.toCurrencyFormat()

            tvDeliveredCP.text = item.deliveryStatus
            tvRecipientCP.text = item.recipientName

            when (item.deliveryStatus) {
                PARAMS_DELIVERY_TASK_COMPLETED -> {
                    tvDeliveredCP.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.deliveryGreen
                        )
                    )
                    imgStatusIconCP.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.deliveryGreen
                        )
                    )
                }
                PARAMS_DELIVERY_TASK_RETENTION -> {
                    tvDeliveredCP.setTextColor(ContextCompat.getColor(context, R.color.deliveryRed))
                    imgStatusIconCP.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.deliveryRed
                        )
                    )
                }
            }

            containerCP.setOnClickListener { viewModel.itemClick(item) }
        }
    }

    class DiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is DeliveryTask && newObject is DeliveryTask) {
                oldObject.id == newObject.id &&
                        oldObject.id == newObject.id &&
                        oldObject.trackingCode == newObject.trackingCode
            } else if (oldObject is Title && newObject is Title) {
                oldObject.title == newObject.title
            } else {
                o == n
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]
    }
}