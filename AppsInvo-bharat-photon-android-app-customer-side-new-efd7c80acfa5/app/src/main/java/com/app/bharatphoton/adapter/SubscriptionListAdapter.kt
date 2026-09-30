package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.SubscriptionListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.SubscriptionListItemViewBinding
import com.app.bharatphoton.module.subcriptionResponse.SubscriptionListingResponse.SubscriptionData

class SubscriptionListAdapter(val onClick: (SubscriptionData, String) -> Unit) : ListAdapter<SubscriptionData, ViewHolder>(ReviewImageDiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SubscriptionListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: SubscriptionListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: SubscriptionData) = with(binding) {
            val safePrice = data.price?.toDouble() ?: 0.0
            val subscriptionPrice = formatPrice(price = safePrice, currencyType = "INR")
            price.text = subscriptionPrice

            val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.check_point)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            val descriptionHtml = data.description ?: ""
            val regex = Regex("<li>(.*?)</li>", RegexOption.IGNORE_CASE)
            val items = regex.findAll(descriptionHtml).map { it.groupValues[1] }.toList()

            // Build spannable text with check icons
            val spannableBuilder = SpannableStringBuilder()
            for (item in items) {
                val cleanItem = HtmlCompat.fromHtml(item, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
                if (cleanItem.isNotBlank()) {
                    val spannable = SpannableString("  $cleanItem")
                    drawable?.let {
                        val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
                        spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    spannableBuilder.append(spannable).append("\n")
                }
            }

            // ✅ Set the final spannable text
            binding.subscriptionDesTv.text = spannableBuilder

            binding.subscriptionDesTv.movementMethod = ScrollingMovementMethod() // make scrollable

            /* binding.subscriptionDesTv.setOnTouchListener { v, event ->
                 v.parent.requestDisallowInterceptTouchEvent(true)

                 if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                     v.parent.requestDisallowInterceptTouchEvent(false)
                 }

                 v.onTouchEvent(event)
                 true
             }
 */


            subscriptionType.text = when (data.planType) {
                0 -> "LITE"
                1 -> "PLUS"
                2 -> "PREMIUM"
                else -> "UNKNOWN"
            }

            playType.text = when (data.mode) {
                "M" -> "Monthly"
                "Q" -> "Quarterly"
                "H" -> "Half Yearly"
                "Y" -> "Yearly"
                else -> ""
            }

            purchaseBtn.setOnAnimatedClickListener {
                onClick(data, "purchase")
            }

            val backgroundRes = if (bindingAdapterPosition % 2 == 0) {
                R.drawable.bg_package
            } else {
                R.drawable.p2
            }
            root.setBackgroundResource(backgroundRes)
        }
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<SubscriptionData>() {
        override fun areItemsTheSame(oldItem: SubscriptionData, newItem: SubscriptionData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubscriptionData, newItem: SubscriptionData): Boolean {
            return oldItem == newItem
        }
    }

}