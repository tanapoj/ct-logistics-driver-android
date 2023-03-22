package com.scgexpress.backoffice.android.base.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.scgexpress.backoffice.android.R

class SpinnerBaseAdapter<T>(context: Context?, items: ArrayList<T>, var defaultText: String) : ArrayAdapter<T>(context!!, android.R.layout.simple_spinner_dropdown_item, items) {

    var everSelect = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent!!)

        val text: TextView = view.findViewById(android.R.id.text1)

        if (!everSelect) {
            text.setTextColor(ContextCompat.getColor(context, R.color.grayDark))
            text.text = defaultText
        } else {
            text.setTextColor(ContextCompat.getColor(context, R.color.grayDarker))
        }

        return view
    }

}