package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ProductDetailsSpecificationAdapter.ViewHolder
import com.app.bharatphoton.databinding.ProductDetailsSpecificationLayoutBinding
import com.app.bharatphoton.module.productResponse.ProductDetailsResponse.Data.ProductDetails.Specification

class ProductDetailsSpecificationAdapter : ListAdapter<Specification, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: ProductDetailsSpecificationLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Specification, position: Int) {
            with(binding) {
                field1.text = data.filedName
                field2.text = data.value

                val backgroundColor = if (position % 2 == 0) {
                    ContextCompat.getColor(itemView.context, R.color.Light_SkyBlue)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.white)
                }
                root.setBackgroundColor(backgroundColor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProductDetailsSpecificationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)

    }

    private object DiffUtil : ItemCallback<Specification>() {
        override fun areItemsTheSame(oldItem: Specification, newItem: Specification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Specification, newItem: Specification): Boolean {
            return oldItem == newItem
        }
    }
}