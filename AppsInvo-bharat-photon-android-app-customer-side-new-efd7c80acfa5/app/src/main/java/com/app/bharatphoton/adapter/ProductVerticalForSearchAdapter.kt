@file:Suppress("KotlinConstantConditions")

package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ProductVerticalForSearchAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.ProductListVerticalItemViewBinding
import com.app.bharatphoton.module.productResponse.ProductListingForSearchResponse.ProductListData

class ProductVerticalForSearchAdapter(val onClick: (String, ProductListData, imageView: ImageView?, progressBar: ProgressBar?) -> Unit) :
    ListAdapter<ProductListData, ViewHolder>(ProductHorizontalDiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ProductListVerticalItemViewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: ProductListVerticalItemViewBinding) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bind(item: ProductListData) {
            try {
                with(binding) {
                    Glide.with(root.context).load(item.image?.get(0)?.url).placeholder(R.drawable.placeholder).into(productImg)
                    categoryName.text = item.categoryName
                    productName.text = item.productName
                    val formattedPrice = formatPrice(item.price?.toDouble() ?: 0.0, item.currencyType ?: "")
                    val discountPrice = formatPrice(item.priceToDisplay?.toDouble() ?: 0.0, item.currencyType ?: "")
                    previousPrice.paintFlags = previousPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    previousPrice.text = discountPrice

                    if (item.rating == 0){
                        ratingLayout.visibility = View.GONE
                    }else{
                        ratingLayout.visibility = View.VISIBLE
                        ratingTxt.text = item.rating.toString()
                    }

                    if (item.discountPercentage != 0) {
                        discountTag.text = "${item.discountPercentage} %"
                    } else {
                        discountRibbon.visibility = View.GONE
                    }
                    price.text = formattedPrice

                    if (item.isWishlist == 1) {
                        like.setImageResource(R.drawable.like)
                    } else {
                        like.setImageResource(R.drawable.wishlist)
                    }

                    if (item.quantity == 0) {
                        outOfStockLayout.visibility = View.VISIBLE
//                        layout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.out_of_stock_light_black))
                    } else {
                        layout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
                    }


                    like.setOnAnimatedClickListener {
                        if (item.isWishlist == 1) {
                            like.setImageResource(R.drawable.wishlist)
                            item.isWishlist = 0
                        } else {
                            like.setImageResource(R.drawable.like)
                            item.isWishlist = 1
                        }
                        onClick("like", item, productImg, null)
                    }

                    cart.setOnAnimatedClickListener {
                        item.quantity.mlog()
                        if (item.quantity != 0) {
                            onClick("cart", item, cart, progressBar)
                        } else {
                            Toast.makeText(root.context, "Product not available", Toast.LENGTH_SHORT).show()
                        }
                    }

                    root.setOnAnimatedClickListener {
                        onClick("products", item, productImg, null)
                    }
                }
            } catch (e: Exception) {
                //
                e.message.mlog()
            }
        }
    }

    private object ProductHorizontalDiffUtil : DiffUtil.ItemCallback<ProductListData>() {
        override fun areItemsTheSame(oldItem: ProductListData, newItem: ProductListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductListData, newItem: ProductListData): Boolean {
            return oldItem == newItem
        }
    }

}