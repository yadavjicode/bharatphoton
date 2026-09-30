package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ServiceLogAdapter.ReviewImageViewHolder
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.databinding.ServiceLogItemViewBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.SubscriptionServiceLog.SubscriptionServiceData
import java.text.SimpleDateFormat
import java.util.Locale

class ServiceLogAdapter : PagingDataAdapter<SubscriptionServiceData, ReviewImageViewHolder>(ReviewImageDiffUtil) {


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
        fun bind(data: SubscriptionServiceData) {
            binding.apply {
                statusTv.text = when (data.complaintStatus) {
                    0 -> "Pending"
                    1 -> "In Process"
                    else -> "Completed"
                }
                statusTv.setTextColor(root.context.getColor(if (data.complaintStatus == 0) R.color.blue else R.color.green))

                Glide.with(root.context).load(data.profileImage).placeholder(R.drawable.placeholder).into(staffImg)

                staffNameTv.text = data.firstName?.let { fName -> data.lastName?.let { "$fName $it" } } ?: "N/A"
                customerNameTv.text = data.customerName ?: "N/A"
                remarkTv.text = data.remark ?: "N/A"
                paymentTv.text = data.payment?.let { formatPrice(it, "INR") } ?: "N/A"

                resolveDateTv.text = data.resolveDate?.let {
                    formatReadableDate(it, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                } ?: "N/A"

                resolveActionTakenTv.text = data.resolveActionTaken ?: "N/A"

                planExpire.apply {
                    text = if (data.remainingDays!! >= 0) {
                        formatText("Your plan will expire on ", "${data.remainingDays} Days", root.context.getColor(R.color.Deep_Saffron))
                    } else {
                        "Your plan is expired"
                    }
                    setTextColor(root.context.getColor(if (data.remainingDays >= 0) R.color.Deep_Saffron else R.color.red))
                }

                complaintTypeTv.text = if (data.complaintReason == "Any other reason") data.description else data.complaintReason
                complaintNo.text = data.uuid ?: ""
            }
        }
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<SubscriptionServiceData>() {
        override fun areItemsTheSame(oldItem: SubscriptionServiceData, newItem: SubscriptionServiceData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SubscriptionServiceData, newItem: SubscriptionServiceData): Boolean {
            return oldItem == newItem
        }
    }

}
