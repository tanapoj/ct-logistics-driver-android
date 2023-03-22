package com.scgexpress.backoffice.android.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R

class DrawerMenuAdapter(context: Context) : RecyclerView.Adapter<DrawerMenuViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var data: ArrayList<DrawerMenuItem> = arrayListOf(
            DrawerMenuItem(R.id.menu_pickup,
                    "Pick Up",
                    R.drawable.ic_store_24dp),

            DrawerMenuItem(R.id.menu_ofd,
                    "OFD Manifest",
                    R.drawable.ic_shipping_24dp),

            DrawerMenuItem(R.id.menu_line_haul,
                    "Line Haul",
                    R.drawable.ic_zoom_out_map_24dp),

            DrawerMenuItem(R.id.menu_line,
                    "LINE",
                    R.drawable.ic_menu_24dp)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerMenuViewHolder {
        val view = inflater.inflate(R.layout.list_entry_drawer_menu_item, parent, false)
        return DrawerMenuViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DrawerMenuViewHolder, position: Int) {
        val item = data[position]

        holder.image.setImageResource(item.iconRes)
        holder.title.text = item.title

    }


}

class DrawerMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val image: ImageView = itemView.findViewById(R.id.imgIcon)
    val title: TextView = itemView.findViewById(R.id.tvTitle)

}

data class DrawerMenuItem(
        val id: Int,

        val title: String,

        @DrawableRes
        val iconRes: Int
)