package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.databinding.WaterReportItemViewBinding
import com.app.bharatphoton.module.Module

class UserCountAdapter : ListAdapter<Module, UserCountAdapter.ViewHolder>(DiffUtil) {

    var selectedLayout = RecyclerView.NO_POSITION + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WaterReportItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ViewHolder(private val binding: WaterReportItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (selectedLayout != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedLayout)
                }
                selectedLayout = layoutPosition
                notifyItemChanged(selectedLayout)

            }
        }

        fun bind(data: Module) {
            with(binding) {
                name.text = data.name
                if (selectedLayout == layoutPosition) {
                    root.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.Deep_Saffron))
                } else {
                    root.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
                }
            }
        }
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