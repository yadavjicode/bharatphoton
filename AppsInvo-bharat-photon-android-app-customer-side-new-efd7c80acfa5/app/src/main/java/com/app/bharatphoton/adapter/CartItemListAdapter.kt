package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.setupExpandableTextView
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.CartItemListItemViewBinding
import com.app.bharatphoton.module.cartResponse.CartDetailsResponse.Data.Cart.Product
import com.bumptech.glide.Glide

class CartItemListAdapter(val onClick: (Product, String, Int, view: View?, progressBar: ProgressBar?, countTv: TextView?) -> Unit) :
    ListAdapter<Product, CartItemListAdapter.ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartItemListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: CartItemListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Product) {
            val count = 0
            with(binding) {
                try {
                    Glide.with(root.context).load(data.image?.get(0)?.url).into(productImg)
                    productName.text = data.productName

                    setupExpandableTextView(
                        productDescription, viewMore = viewMore, data.description ?: "", initialMaxLines = 1
                    )
                    if (data.productquantity == -1 && data.isQuantityUnlimited == 1) {
                        outOfStockLayout.visibility = View.GONE
                    } else if (data.productquantity!! <= 0 && data.isQuantityUnlimited == 0) {
                        outOfStockLayout.visibility = View.VISIBLE
                    }
                    val priceAmount = Utils.formatPrice(price = data.price?.toDouble() ?: 0.0, currencyType = "INR")
                    val discountAmount = Utils.formatPrice(price = data.priceToDisplay?.toDouble() ?: 0.0, currencyType = "INR")
                    price.text = priceAmount
                    if (data.rating == 0) {
                        ratingLayout.visibility = View.GONE
                    } else {
                        ratingLayout.visibility = View.VISIBLE
                        ratingTxt.text = data.rating.toString()
                    }
                    discountPrice.text = discountAmount
                    discountPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    countTv.text = data.quantity.toString()

                    plus.setOnAnimatedClickListener {
                        if (data.isQuantityUnlimited == 1) {
                            onClick(data, "plus", count, plus, progressBar, countTv)
                        } else if (data.quantity!! < data.productquantity) {
                            onClick(data, "plus", count, plus, progressBar, countTv)
                            plus.isEnabled = false
                        } else {
                            Toast.makeText(root.context, "Item is out of stock", Toast.LENGTH_SHORT).show()
                        }
                    }

                    minus.setOnAnimatedClickListener {
                        if (countTv.text.toString() > "1") {
                            minus.isEnabled = false
                            onClick(data, "minus", count, minus, progressBar, countTv)
                        }
                    }

                    delete.setOnAnimatedClickListener {
                        onClick(data, "delete", count, null, null, null)

                    }

                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    fun removeItem(data: Product) {
        val currentList: MutableList<Product> = currentList.toMutableList()
        currentList.remove(data)
        submitList(currentList)
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