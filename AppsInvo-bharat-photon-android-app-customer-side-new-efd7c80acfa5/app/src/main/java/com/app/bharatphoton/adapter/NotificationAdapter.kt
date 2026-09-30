package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.databinding.NotificationItemViewBinding
import com.app.bharatphoton.module.notificationRessponse.NotificationListResponse.NotificationData
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter : PagingDataAdapter<NotificationData, NotificationAdapter.ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: NotificationItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotificationData) {
            with(binding) {
                notificationTv.text = data.body
                Glide.with(root.context).load(data.profileImage).placeholder(
                    R.drawable.placeholder
                ).into(profilePhoto)
                val date = formatReadableDate(dateString = data.createdAt ?: "", simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                dateTv.text = date
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NotificationAdapter.ViewHolder {
        return ViewHolder(
            NotificationItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean {
            return oldItem == newItem
        }
    }
}