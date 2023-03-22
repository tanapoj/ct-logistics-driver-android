package com.scgexpress.backoffice.android.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import kotlinx.android.synthetic.main.list_entry_menu_item.view.*


class MenuAdapter(private val viewModel: MenuViewModel) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    var data: ArrayList<MenuModel> = ArrayList()
        set(value) {
            val result = DiffUtil.calculateDiff(MenuDiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_entry_menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.bind(item, position, itemCount, viewModel)
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: MenuModel, position: Int, itemCount: Int, viewModel: MenuViewModel) = with(itemView) {
            tvTitle.setTextColor(item.textColor)
            tvTitle.text = item.title

            if (position != itemCount - 1) {
                vPaddingBottom.visibility = View.GONE
            } else {
                vPaddingBottom.visibility = View.VISIBLE
            }

            clTop.setBackgroundColor(item.bgColor)
            clTop.setOnClickListener { viewModel.menuClicked(item) }

            imgIcon.setImageResource(item.iconRes)
        }
    }

    class MenuDiffCallback(private val o: ArrayList<MenuModel>, private val n: ArrayList<MenuModel>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = o[oldItemPosition] === n[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = o[oldItemPosition] == n[newItemPosition]

    }
}
