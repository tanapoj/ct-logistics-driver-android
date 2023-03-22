package com.scgexpress.backoffice.android.ui.topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.Title
import com.scgexpress.backoffice.android.model.Topic
import kotlinx.android.synthetic.main.list_entry_topic_item.view.*
import kotlinx.android.synthetic.main.list_entry_topic_title.view.*

class TopicAdapter(private val viewModel: TopicViewModel) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    companion object {
        const val VIEWTYPE_TITLE = 1
        const val VIEWTYPE_ITEM = 2
    }

    var data: List<Any> = arrayListOf()
        set(value) {
            val result = DiffUtil.calculateDiff(TopicDiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEWTYPE_TITLE) {
            val view = inflater.inflate(R.layout.list_entry_topic_title, parent, false)
            return TopicTitleViewHolder(view)
        } else if (viewType == VIEWTYPE_ITEM) {
            val view = inflater.inflate(R.layout.list_entry_topic_item, parent, false)
            return TopicItemViewHolder(view)
        }
        return TopicViewHolder(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        if (holder is TopicTitleViewHolder) {
            val item = data[holder.adapterPosition] as Title
            holder.bind(item)

        } else if (holder is TopicItemViewHolder) {
            val item = data[holder.adapterPosition] as Topic
            holder.bind(item, viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Title -> VIEWTYPE_TITLE
            is Topic -> VIEWTYPE_ITEM
            else -> super.getItemViewType(position)
        }

    }

    open class TopicViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class TopicTitleViewHolder(view: View) : TopicViewHolder(view) {

        fun bind(item: Title) = with(itemView) {
            txtTitle.text = item.title
        }
    }

    class TopicItemViewHolder(view: View) : TopicViewHolder(view) {

        fun bind(item: Topic, viewModel: TopicViewModel) = with(itemView) {
            txtTopicTitle.text = item.title
            txtTopicBody.text = item.body
        }
    }

    class TopicDiffCallback(private val o: List<Any>, private val n: List<Any>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]

            //if we want faster update we also need to implement getPayloadChange
            return if (oldObject is Topic && newObject is Topic) {
                oldObject.id == newObject.id &&
                        oldObject.userId == newObject.userId &&
                        oldObject.title == newObject.title &&
                        oldObject.body == newObject.body
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