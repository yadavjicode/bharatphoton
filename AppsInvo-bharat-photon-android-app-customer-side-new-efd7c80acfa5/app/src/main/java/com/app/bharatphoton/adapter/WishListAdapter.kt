package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.WishListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.WishlistItemViewBinding
import com.app.bharatphoton.module.wishlistResponse.WishListResponse.WishListData

class WishListAdapter(val onClick: (String, WishListData) -> Unit) : PagingDataAdapter<WishListData, ViewHolder>(DiffUtil) {
    inner class ViewHolder(val binding: WishlistItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: WishListData) {
            binding.apply {
                try {
                    productName.text = data.productName
                    if (data.productImage?.isNotEmpty() == true) {
                        Glide.with(root.context).load(data.productImage.get(0)?.url).placeholder(R.drawable.camera_icon).into(productImg)
                    }
                    val actualPrice = Utils.formatPrice(data.price?.toDouble() ?: 0.0, currencyType = data.currencyType ?: "")
                    val previousPrice = Utils.formatPrice(data.priceToDisplay?.toDouble() ?: 0.0, currencyType = data.currencyType ?: "")
                    price.text = actualPrice
                    discountPrice.paintFlags = price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    discountPrice.text = previousPrice
                    ratingTv.text = data.rating.toString()
                    like.setOnAnimatedClickListener {
                        onClick.invoke("like", data)
                    }
                    root.setOnAnimatedClickListener {
                        onClick.invoke("root", data)
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
        return ViewHolder(WishlistItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private object DiffUtil : ItemCallback<WishListData>() {
        override fun areItemsTheSame(oldItem: WishListData, newItem: WishListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WishListData, newItem: WishListData): Boolean {
            return oldItem == newItem
        }
    }
}