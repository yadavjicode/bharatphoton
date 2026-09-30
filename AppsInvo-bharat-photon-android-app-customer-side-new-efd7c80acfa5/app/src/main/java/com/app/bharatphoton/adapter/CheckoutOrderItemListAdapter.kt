package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.CheckoutOrdersListItemViewBinding
import com.app.bharatphoton.module.checkoutResponse.CheckOutResponse.Data.Cart.Product


class CheckoutOrderItemListAdapter(private val onClick: (Product, String) -> Unit) : ListAdapter<Product, CheckoutOrderItemListAdapter.ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: CheckoutOrdersListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Product) {
            with(binding) {
                try {
                    Glide.with(root.context).load(data.image?.get(0)?.url).placeholder(R.drawable.placeholder).into(productImg)
                    productName.text = data.productName
                    val actualPrice = formatPrice(price = data.price?.toDouble() ?: 0.0, currencyType = "INR")
                    val discountedPrice = formatPrice(price = data.priceToDisplay?.toDouble() ?: 0.0, currencyType = "INR")
                    price.text = actualPrice
                    discountPrice.text = discountedPrice
                    discountPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    quantity.text = "Qty: ${data.quantity}"
                    if (data.installationPrice == null) {
                        installationPrice.visibility = View.GONE
                        close.visibility = View.GONE

                    } else {
                        installationPrice.visibility = View.VISIBLE
                        close.visibility = View.VISIBLE
                        installationPrice.text = "Installation : ${formatPrice(price = data.installationPrice!!.toDouble(), currencyType = "INR")} /Pcs."
                    }
                    close.setOnClickListener {
                        onClick(data, "delete")
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }


    private object DiffUtil : ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CheckoutOrdersListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}