package com.scgexpress.backoffice.android.ui.delivery.scan

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.di.glide.GlideApp
import com.scgexpress.backoffice.android.model.PhotoTitle
import java.io.File

class DeliverySelectDialogAdapter(
    context: Context,
    private val mListener: DeliverySelectDialogFragment.OnOptionSelectedListener
) :
    RecyclerView.Adapter<DeliverySelectDialogAdapter.ReStatusDialogViewHolder>() {

    var data: List<PhotoTitle> = listOf()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallback(field, value))
            result.dispatchUpdatesTo(this)
            field = value
        }

    private val mImagePlaceholder: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.ic_placeholder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReStatusDialogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.dialog_list_entry_photo_select, parent, false)
        return ReStatusDialogViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ReStatusDialogViewHolder, position: Int) {
        val item = data[holder.adapterPosition]

        holder.title.text = item.title

        if (item.photoStored.url != null) {
            if (item.photoStored.url.isEmpty()) return
            GlideApp.with(holder.image.context)
                .load(item.photoStored.url)
                .placeholder(mImagePlaceholder)
                .centerCrop()
                .into(holder.image)
        } else if (item.photoStored.filePath != null) {
            if (item.photoStored.filePath.isEmpty()) return
            GlideApp.with(holder.image.context)
                .load(Uri.fromFile(File(item.photoStored.filePath)))
                .placeholder(mImagePlaceholder)
                .centerCrop()
                .into(holder.image)
        }

        holder.container.setOnClickListener {
            mListener.onStatusSelected(item)
        }
    }

    class ReStatusDialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var container: ConstraintLayout = view.findViewById(R.id.container)
        var image: ImageView = view.findViewById(R.id.imageView)
        var title: TextView = view.findViewById(R.id.title)
    }

    class DiffCallback(private val o: List<PhotoTitle>, private val n: List<PhotoTitle>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = o.size

        override fun getNewListSize(): Int = n.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObject = o[oldItemPosition]
            val newObject = n[newItemPosition]
            //if we want faster update we also need to implement getPayloadChange
            return o == n
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            o[oldItemPosition] == n[newItemPosition]

    }
}