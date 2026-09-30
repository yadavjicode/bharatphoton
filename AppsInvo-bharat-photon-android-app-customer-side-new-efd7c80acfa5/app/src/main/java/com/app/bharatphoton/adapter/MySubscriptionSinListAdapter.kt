package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.MySubscriptionSinListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.MySubscriptionSinListItemViewLayoutBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.PurchasedSubscriptionListResponse.SubscriptionListData
import com.app.bharatphoton.module.purchaseSubscriptionResponse.PurchasedSubscriptionListResponse.SubscriptionListData.InstallationMetric
import java.text.SimpleDateFormat
import java.util.Locale

class MySubscriptionSinListAdapter(private val parentItem: SubscriptionListData, val onClick: (String, InstallationMetric, SubscriptionListData) -> Unit) :
    ListAdapter<InstallationMetric, ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MySubscriptionSinListItemViewLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: MySubscriptionSinListItemViewLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: InstallationMetric) {
            with(binding) {
                SIN.text = formatText(prefix = "SIN : ", plan = "${item.sin?.uppercase(Locale.getDefault())}", color = Color.BLACK)
//                if (item.isPlanExpired == 1) noteTv.visibility = View.GONE else noteTv.text = "Note: Your plan has not expired yet, so you can't renew it now"
                val startDateTxt = item.subscriptionStartDate?.let { formatReadableDate(it, SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())) } ?: "N/A"
                startDate.text = startDateTxt
                val expireDateTxt = item.subscriptionExpiryDate?.let { formatReadableDate(it, SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())) } ?: "N/A"
                endDate.text = expireDateTxt

                reNewPlan.setOnAnimatedClickListener {
                    if (item.isPlanExpired == 1) onClick("reNewPlan", item, parentItem)
                    else root.context.toast("Your plan has not expired yet, so you can't renew it now")
                }

                registerComplaint.setOnAnimatedClickListener {
                    onClick("registerComplaint", item, parentItem)
                }
                root.setOnAnimatedClickListener {
                    onClick("root", item, parentItem)
                }
            }
        }
    }

    private object DiffUtil : ItemCallback<InstallationMetric>() {
        override fun areItemsTheSame(oldItem: InstallationMetric, newItem: InstallationMetric): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InstallationMetric, newItem: InstallationMetric): Boolean {
            return oldItem == newItem
        }
    }
}