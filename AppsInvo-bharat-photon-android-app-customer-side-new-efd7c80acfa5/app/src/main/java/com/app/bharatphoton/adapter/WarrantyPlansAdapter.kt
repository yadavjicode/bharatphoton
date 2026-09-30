package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.WarrantyPlansAdapter.ViewHolder
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.WarrantyPlansItemViewBinding
import com.app.bharatphoton.module.warrantyApiResponse.AvailableWarrantyResponse.AvailableWarrantyData

class WarrantyPlansAdapter(val onClick: (String, AvailableWarrantyData) -> Unit) : ListAdapter<AvailableWarrantyData, ViewHolder>(DiffUtil) {
    var layout = 0

    inner class ViewHolder(private val binding: WarrantyPlansItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnAnimatedClickListener {
                val position = layoutPosition
                if (layout != position) {
                    val previousPosition = layout
                    layout = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(position)
                }
                notifyItemChanged(position)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: AvailableWarrantyData) {
            binding.apply {
                val planPrice = Utils.formatPrice(price = data.price?.toDouble() ?: 0.0, currencyType = data.currencyType ?: "")
                price.text = planPrice

                when (data.mode) {
                    "M" -> {
                        modeTv.text = "Monthly: $planPrice/month"
                        months.text = "1 Months"
                    }

                    "Y" -> {
                        modeTv.text = "Yearly: $planPrice/year"
                        months.text = "1 Year"
                    }

                    "T" -> {
                        months.text = "2 Year"
                    }

                    "H" -> {
                        modeTv.text = "Half Yearly: $planPrice/year"
                        months.text = "6 Months"
                    }

                    "F" -> {
                        months.text = "5 Year"
                    }

                    "Q" -> {
                        modeTv.text = "Quarterly: $planPrice/quarter"
                        months.text = "3 Months"
                    }
                }
                if (layoutPosition == layout) {
                    onClick("root", data)
                    radioBtn.setImageResource(R.drawable.radio_selected)
                    root.setBackgroundResource(R.drawable.style_deep_saffron)
                } else {
                    root.setBackgroundResource(R.drawable.light_white_corner_round_style)
                    radioBtn.setImageResource(R.drawable.radio_btn_unselect)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            WarrantyPlansItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffUtil : ItemCallback<AvailableWarrantyData>() {
        override fun areItemsTheSame(oldItem: AvailableWarrantyData, newItem: AvailableWarrantyData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AvailableWarrantyData, newItem: AvailableWarrantyData): Boolean {
            return oldItem == newItem
        }
    }
}