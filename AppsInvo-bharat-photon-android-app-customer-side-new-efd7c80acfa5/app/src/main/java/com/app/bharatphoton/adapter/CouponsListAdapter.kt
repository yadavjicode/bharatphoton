package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.CouponsAndOffersItemViewBinding
import com.app.bharatphoton.module.couponResponse.CouponListResponse.CouponListData

class CouponsListAdapter(val onClick: (CouponListData, String) -> Unit) : ListAdapter<CouponListData, CouponsListAdapter.ViewHolder>(DiffUtil) {
    private var isAnyCouponApplied = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CouponsAndOffersItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: CouponsAndOffersItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: CouponListData) {
            with(binding) {
                data.description?.let {
                    val cleanedDescription = it.replace("<br>", "").replace("<p>", "")
                    description.text = Html.fromHtml(cleanedDescription, Html.FROM_HTML_MODE_COMPACT)
                }
                title.text = data.title ?: ""

                if (data.couponType == 1) {
                    saveDescriptionTv.text = Html.fromHtml("SAVE ₹ ${data.discount} ON THIS ORDER", Html.FROM_HTML_MODE_COMPACT)
                } else {
                    saveDescriptionTv.text = Html.fromHtml("SAVE ${data.discount} % ON THIS ORDER", Html.FROM_HTML_MODE_COMPACT)
                }
                // Check if any coupon is already applied
                if (data.isCouponApplied == true) {
                    isAnyCouponApplied = true
                }

                applyBtn.text = if (data.isCouponApplied == true) "Remove" else "Apply"
                if (data.isCouponApplied == true) {
                    buttonCardView.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.red))
                } else {
                    buttonCardView.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.Deep_Saffron))
                }


                // Click Action
                applyBtn.setOnAnimatedClickListener {
                    when {
                        // If this coupon is applied → remove it
                        data.isCouponApplied == true -> {
                            onClick.invoke(data, "remove")
                        }

                        // If another coupon is already applied → show message
                        isAnyCouponApplied -> {
                            Toast.makeText(root.context, "Please remove the applied coupon first", Toast.LENGTH_SHORT).show()
                        }

                        // Otherwise → apply this one
                        else -> {
                            onClick.invoke(data, "apply")
                        }
                    }
                }
            }
        }
    }

    fun updateCoupons(list: List<CouponListData?>?) {
        // Update the list and recompute whether any coupon is applied
        isAnyCouponApplied = list?.any { it?.isCouponApplied == true } == true
        submitList(list)
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<CouponListData>() {
        override fun areItemsTheSame(oldItem: CouponListData, newItem: CouponListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CouponListData, newItem: CouponListData): Boolean {
            return oldItem == newItem
        }
    }

}