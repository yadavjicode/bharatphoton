package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Color.BLACK
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.NestedMyProductsAdapter.ViewHolder
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.chaneColoOfOneTextView
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.MyProductListItemViewBinding
import com.app.bharatphoton.module.productResponse.InstalledProductListResponse.InstalledProductData.Product


class NestedMyProductsAdapter(val onClick: (Product, String) -> Unit) : ListAdapter<Product, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: MyProductListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Product) {
            with(binding) {
                productName.text = item.productName
                val orderIdValue = formatText(prefix = "Order Id : ", plan = "${item.uuid}", color = BLACK)
                orderId.text = orderIdValue
                modelName.text = item.modelNo

                if (item.remainingWarrantyDays == null || item.remainingWarrantyDays < 0) {
                    expireWarrantyDetailsTv.visibility = GONE
                } else {
                    expireWarrantyDetailsTv.visibility = View.VISIBLE
                    val days = item.remainingWarrantyDays.toInt()
                    chaneColoOfOneTextView(
                        string = "Your warranty will expire in $days Days",
                        wordToColor = "$days Days",
                        context = root.context,
                        color = R.color.Deep_Saffron,
                        finalWord = expireWarrantyDetailsTv
                    )
                }

                if (item.remainingWarrantyDays != null) {
                    planExpireTxt.visibility = if (item.remainingWarrantyDays <= 0) View.VISIBLE else GONE
                }

                if (item.costSaved != null) {
                    val costSavedValue = Utils.formatPrice(price = item.costSaved, currencyType = "INR")
                    costSavedTv.text = "$costSavedValue \nTotal Cost Saved"
                } else {
                    costSavedTv.text = "N/A"
                }
                savedLayout.visibility = if (item.isUrinalSensorAvilable == 1||item.isFieldAvailable == 1) View.VISIBLE else View.GONE

                if (item.waterSaved != null) waterSavedTv.text = "${item.waterSaved} Ltrs \nTotal Water Saved"
                else waterSavedTv.text = "N/A"
                Glide.with(root.context).load(item.image).placeholder(R.drawable.placeholder).into(productImg)

                topLayout.setOnAnimatedClickListener {
                    if (expandedLayout.visibility == View.VISIBLE) {
                        val fadeOut = AnimationUtils.loadAnimation(root.context, R.anim.fade_out_slow)
                        fadeOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {}

                            override fun onAnimationEnd(animation: Animation?) {
                                expandedLayout.visibility = GONE
                            }

                            override fun onAnimationRepeat(animation: Animation?) {}
                        })
                        expandedLayout.startAnimation(fadeOut)
                        arrow.rotation = 0f
                    } else {
                        expandedLayout.visibility = View.VISIBLE
                        val fadeIn = AnimationUtils.loadAnimation(root.context, R.anim.fade_in)
                        val animationSet = AnimationSet(true).apply {
                            addAnimation(fadeIn)
                        }
                        expandedLayout.startAnimation(animationSet)
                        arrow.rotation = 90f
                    }
                }

                moduledLayout.setOnAnimatedClickListener {
                    onClick(item, "ModuleLayout")
                }
            }
        }
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MyProductListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}