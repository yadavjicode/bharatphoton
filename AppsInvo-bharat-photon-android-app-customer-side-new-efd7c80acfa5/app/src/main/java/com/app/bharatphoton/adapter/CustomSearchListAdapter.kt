package com.app.bharatphoton.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.app.bharatphoton.R

class CustomSearchListAdapter(
    context: Context, productName: List<String>
) : ArrayAdapter<String>(context, 0, productName) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.search_layout_pop_up, parent, false)
        val productName = view.findViewById<TextView>(R.id.productName)
        productName.text = getItem(position)
        return view
    }
}