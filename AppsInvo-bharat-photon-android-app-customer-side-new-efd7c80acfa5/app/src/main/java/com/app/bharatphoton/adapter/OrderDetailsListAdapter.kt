package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.OrderDetailsListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.OrderDetailsItemsItemViewBinding
import com.app.bharatphoton.module.orderResponse.OrderDetailsResponse.Data.OrderDetails.Product
import com.bumptech.glide.Glide

class OrderDetailsListAdapter(val onClick: (String, Product) -> Unit) : ListAdapter<Product, ViewHolder>(DiffUtil) {
    inner class ViewHolder(val binding: OrderDetailsItemsItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Product) {
            binding.apply {
                try {
                    productName.text = data.productName ?: ""
                    categoryName.text = data.categoryName ?: ""
                    Glide.with(root.context).load(data.image).placeholder(R.drawable.camera_icon).into(productImg)
                    val actualPrice = Utils.formatPrice(data.price?.toDouble() ?: 0.0, currencyType = "INR")
                    price.text = actualPrice
                    quantity.text = "QTY : ${data.quantity ?: 0}"
                    trackOrderBtn.setOnAnimatedClickListener {
                        onClick.invoke("trackOrder", data)
                    }
                    cancelOrderBtn.setOnAnimatedClickListener {
                        onClick.invoke("cancelOrder", data)
                    }
                    addReviewBtn.setOnAnimatedClickListener {
                        onClick.invoke("addReview", data)
                    }
                    orderAgainBtn.setOnAnimatedClickListener {
                        onClick.invoke("orderAgain", data)
                    }
                    addReviewBtn.visibility = if (data.orderStatus == 3) View.VISIBLE else View.GONE

                    if (data.installationPrice == 0) {
                        installationPriceTv.visibility = View.GONE
                    } else {
                        installationPriceTv.text = "Installation Price : ${data.installationPrice ?: 0}/Pcs."
                    }

                    if (data.orderStatus == 0) {
                        statusLayout.setCardBackgroundColor(
                            ContextCompat.getColor(
                                root.context, R.color.light_pink200
                            )
                        )
                        status.setTextColor(root.context.getColor(R.color.pink))

                        status.text = "Pending"
                    } else if (data.orderStatus == 1) {
                        statusLayout.setCardBackgroundColor(
                            ContextCompat.getColor(
                                root.context, R.color.Light_Deep_Saffron
                            )
                        )
                        status.setTextColor(
                            root.context.getColor(
                                R.color.Deep_Saffron
                            )
                        )
                        status.text = "Processed"
                    } else if (data.orderStatus == 2) {
                        statusLayout.setCardBackgroundColor(
                            ContextCompat.getColor(
                                root.context, R.color.light_blue
                            )
                        )
                        status.setTextColor(
                            root.context.getColor(
                                R.color.blue
                            )
                        )
                        status.text = "Dispatched"
                    } else if (data.orderStatus == 3) {
                        status.setTextColor(
                            root.context.getColor(R.color.green)
                        )
                        cancelOrderBtn.visibility = View.GONE
                        orderAgainBtn.visibility = View.VISIBLE
                        statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.light_green))
                        status.text = "Delivered"
                    } else if (data.orderStatus == 7 || data.orderStatus == 5) {
                        statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.light_pink200))
                        status.text = "Cancelled"
                        status.setTextColor(ContextCompat.getColor(root.context, R.color.pink))
                        cancelOrderBtn.visibility = View.GONE
                        trackOrderBtn.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(OrderDetailsItemsItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private object DiffUtil : ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}