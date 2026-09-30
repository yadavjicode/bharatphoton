package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.DateLayoutItemViewBinding
import com.app.bharatphoton.module.Module

class DateListSinDetailsAdapter : ListAdapter<Module, DateListSinDetailsAdapter.ViewHolder>(DiffUtil) {

    private var selectedPosition = 0

    override fun onBindViewHolder(holder: DateListSinDetailsAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateListSinDetailsAdapter.ViewHolder {
        return ViewHolder(
            DateLayoutItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: DateLayoutItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Module) {
            with(binding) {
                dateTv.text = data.name
                val isSelected = layoutPosition == selectedPosition
                root.setCardBackgroundColor(ContextCompat.getColor(root.context, if (isSelected) R.color.Deep_Saffron else R.color.white))
                dateTv.setTextColor(ContextCompat.getColor(root.context, if (isSelected) R.color.white else R.color.black))

                root.setOnAnimatedClickListener {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = layoutPosition
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
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