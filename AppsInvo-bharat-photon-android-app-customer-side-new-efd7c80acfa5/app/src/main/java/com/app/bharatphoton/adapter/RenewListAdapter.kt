package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.RenewListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.SubscriptionListItemViewBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.RenewPlanResponse.Data.AvilablePlan

class RenewListAdapter(val onClick: (AvilablePlan, String) -> Unit) : ListAdapter<AvilablePlan, ViewHolder>(ReviewImageDiffUtil) {
    inner class ViewHolder(private val binding: SubscriptionListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: AvilablePlan) {
            with(binding) {
                val subscriptionPrice = formatPrice(price = data.price?.toDouble() ?: 0.0, currencyType = "INR")
                price.text = subscriptionPrice

                data.description?.let {
                    val formatted = it.replace("<li>", "• ").replace("</li>", "<br>")
                    subscriptionDesTv.text = HtmlCompat.fromHtml(formatted, FROM_HTML_MODE_LEGACY)
                }

                upgradeBtn.visibility = View.VISIBLE
                purchaseBtn.visibility = View.GONE

                upgradeBtn.setOnAnimatedClickListener {
                    onClick(data, "upgradeBtn")
                }
                when (data.mode) {
                    "M" -> {
                        playType.text = "Monthly"
                    }

                    "Q" -> {
                        playType.text = "Quarterly"
                    }

                    "Y" -> {
                        playType.text = "Yearly"
                    }
                }

                when (data.planType) {
                    0 -> subscriptionType.text = "LITE"
                    1 -> subscriptionType.text = "PLUS"
                    2 -> subscriptionType.text = "PREMIUM"
                }

                val backgroundColor = if (bindingAdapterPosition % 2 == 0) {
                    R.drawable.bg_package
                } else {
                    R.drawable.p2
                }
                root.setBackgroundResource(backgroundColor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SubscriptionListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<AvilablePlan>() {
        override fun areItemsTheSame(oldItem: AvilablePlan, newItem: AvilablePlan): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AvilablePlan, newItem: AvilablePlan): Boolean {
            return oldItem == newItem
        }
    }
}