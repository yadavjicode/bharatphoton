package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ServiceLogSinDetailsAdapter.ReviewImageViewHolder
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.databinding.ServiceLogItemViewBinding
import com.app.bharatphoton.module.productResponse.SinDetailsServiceLogResponse.SinDetailsServiceLogData
import java.text.SimpleDateFormat
import java.util.Locale

class ServiceLogSinDetailsAdapter : PagingDataAdapter<SinDetailsServiceLogData, ReviewImageViewHolder>(ReviewImageDiffUtil) {


    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        return ReviewImageViewHolder(
            ServiceLogItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ReviewImageViewHolder(private val binding: ServiceLogItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: SinDetailsServiceLogData) {
            with(binding) {
                statusTv.apply {
                    text = when (data.complaintStatus) {
                        0 -> "Pending"
                        1 -> "In Process"
                        else -> "Completed"
                    }
                    setTextColor(root.context.getColor(if (data.complaintStatus == 0) R.color.blue else R.color.green))
                }

                Glide.with(root.context).load(data.profileImage).placeholder(R.drawable.placeholder).into(staffImg)
                staffNameTv.text = listOfNotNull(data.firstName, data.lastName).takeIf { it.isNotEmpty() }?.joinToString(" ") ?: "N/A"
                resolveDateTv.text = data.resolveDate?.let { formatReadableDate(it, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())) } ?: "N/A"
                resolveActionTakenTv.text = data.resolveActionTaken ?: "N/A"
                customerNameTv.text = data.customerName ?: "N/A"
                remarkTv.text = data.remark ?: "N/A"
                planExpire.text = Utils.formatText(
                    prefix = "Your plan will expire in ", plan = "${SharedData.sinDetailsServiceLogRemainingWarrantyDays} Days", color = root.context.getColor(R.color.Deep_Saffron)
                )
                paymentTv.text = "₹${data.payment ?: "00.00"}"
                complaintTypeTv.text = if (data.complaintReason == "Any other reason") data.description else data.complaintReason
                complaintNo.text = data.uuid ?: "N/A"
            }
        }
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<SinDetailsServiceLogData>() {
        override fun areItemsTheSame(oldItem: SinDetailsServiceLogData, newItem: SinDetailsServiceLogData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SinDetailsServiceLogData, newItem: SinDetailsServiceLogData): Boolean {
            return oldItem == newItem
        }
    }
}
