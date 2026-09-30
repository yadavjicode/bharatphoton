package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.NestedSubCategoryListAdapter.ViewHolder
import com.app.bharatphoton.common.GlideImageLoaderHelper
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.ProductListVerticalItemViewBinding
import com.app.bharatphoton.module.productResponse.ProductListingResponse.ProductListData.SubcategoryListing.ProductList

class NestedSubCategoryListAdapter(val onClick: (String, ProductList, imageView: ImageView?, progressBar: ProgressBar?) -> Unit) : ListAdapter<ProductList, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: ProductListVerticalItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProductList) {
            try {
                with(binding) {
                    GlideImageLoaderHelper.glide(context = root.context, dataImage = item.image?.get(0)?.url?:"", progressBar = progressBar, imageView = productImg)
                    categoryName.text = item.categoryName
                    productName.text = item.productName
                    if (item.rating == 0){
                        ratingLayout.visibility = View.GONE
                    }else{
                        ratingLayout.visibility = View.VISIBLE
                        ratingTxt.text = item.rating.toString()
                    }
                    val formattedPrice = formatPrice(item.price ?: 0.0, item.currencyType ?: "")
                    val discountPrice = formatPrice(item.priceToDisplay ?: 0.0, item.currencyType ?: "")
                    previousPrice.paintFlags = previousPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    previousPrice.text = discountPrice
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

                    if (item.quantity!! <= 0 && item.isQuantityUnlimited != 1) {
                        outOfStockLayout.visibility = View.VISIBLE
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

                    if (item.cartStatus == 1) {
                        cart.setImageResource(R.drawable.cart_selected)
                    } else {
                        cart.setImageResource(R.drawable.cart)
                    }

                    cart.setOnAnimatedClickListener {
                        cart.isEnabled = false
                        if (item.cartStatus == 1) {
                            Toast.makeText(root.context, "Product already added in cart", Toast.LENGTH_SHORT).show()
                            cart.setImageResource(R.drawable.cart_selected)
                            return@setOnAnimatedClickListener
                        } else {
                            if (item.isQuantityUnlimited == 1){
                                onClick("cart", item, cart, progressBar)
                            }else if (item.quantity != 0 && item.quantity!! > 0  ) {
                                onClick("cart", item, cart, progressBar)
                            } else {
                                Toast.makeText(root.context, "Product not available", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    root.setOnAnimatedClickListener {
                        onClick("products", item, productImg, null)
                    }
                }
            } catch (e: Exception) {
                e.message.mlog()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProductListVerticalItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}

private object DiffUtil : ItemCallback<ProductList>() {
    override fun areItemsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
        return oldItem == newItem
    }

}