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

class DateListAdapter(val onClick: (String, String) -> Unit) : ListAdapter<String, DateListAdapter.ViewHolder>(DiffUtil) {

    private var selectedPosition = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DateLayoutItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: DateLayoutItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: String) {
            with(binding) {
                dateTv.text = data
                val isSelected = layoutPosition == selectedPosition
                root.setCardBackgroundColor(ContextCompat.getColor(root.context, if (isSelected) R.color.Deep_Saffron else R.color.white))
                dateTv.setTextColor(ContextCompat.getColor(root.context, if (isSelected) R.color.white else R.color.black))

                root.setOnAnimatedClickListener {
                    onClick("root", data)
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = layoutPosition
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }


    private object DiffUtil : ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

}