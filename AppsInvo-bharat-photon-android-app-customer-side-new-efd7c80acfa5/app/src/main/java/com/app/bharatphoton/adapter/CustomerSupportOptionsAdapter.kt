package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.CustomerSupportOptionsAdapter.ViewHolder
import com.app.bharatphoton.databinding.CustomerSupportItemViewBinding
import com.app.bharatphoton.module.Module

class CustomerSupportOptionsAdapter(val onClick:(Int)->Unit) : ListAdapter<Module, ViewHolder>(DiffUtil) {
    private var layout = RecyclerView.NO_POSITION + 1

    inner class ViewHolder(private val binding: CustomerSupportItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onClick(layoutPosition)
                val position = layoutPosition
                if (layout != position) {
                    val previousPosition = layout
                    layout = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(position)
                }
            }
        }

        fun bind(data: Module) {
            with(binding) {
                name.text = data.name
                if (layoutPosition == layout) {
                    root.setCardBackgroundColor(
                        ContextCompat.getColor(
                            root.context, R.color.Deep_Saffron
                        )
                    )
                } else {
                    root.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomerSupportItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    private object DiffUtil : ItemCallback<Module>() {
        override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem == newItem
        }
    }
}