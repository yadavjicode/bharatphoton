package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.SinListLayoutItemViewBinding
import com.app.bharatphoton.module.productResponse.InstalledProductDetailsResponse.Data.Order.InstallationInfo
import java.text.SimpleDateFormat
import java.util.Locale

class MyProductSinListAdapter(val onClick: (String, InstallationInfo) -> Unit) : ListAdapter<InstallationInfo, MyProductSinListAdapter.ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SinListLayoutItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: SinListLayoutItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: InstallationInfo) {
            with(binding) {
//                sinNo.text = "SIN : ${data.sin}"
                val sinNumber = formatText(prefix = "SIN : ", plan = "${data.sin?.uppercase()}", color = Color.BLACK)
                sinNo.text = sinNumber




                if (data.warrentyStartDate != null) {
                    val warrantyStartDate = formatReadableDate(
                        dateString = data.warrentyStartDate, simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    )
                    val warrantyEndDate = formatReadableDate(
                        dateString = data.warrentyEndDate ?: "", simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    )
                    startDate.text = warrantyStartDate
                    endDate.text = warrantyEndDate


                } else {
                    startDate.text = "N/A"
                    endDate.text = "N/A"
                }

                root.setOnAnimatedClickListener {
                    onClick.invoke("root", data)
                }
            }
        }
    }

    private object DiffUtil : ItemCallback<InstallationInfo>() {
        override fun areItemsTheSame(oldItem: InstallationInfo, newItem: InstallationInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InstallationInfo, newItem: InstallationInfo): Boolean {
            return oldItem == newItem
        }
    }
}