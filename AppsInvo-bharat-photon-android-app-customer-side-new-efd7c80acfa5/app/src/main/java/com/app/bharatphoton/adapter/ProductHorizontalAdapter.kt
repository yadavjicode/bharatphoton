package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ProductHorizontalAdapter.ProductHorizontalHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.ProductHorizonatlItemViewBinding
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse.CategoryListData

class ProductHorizontalAdapter(private val onClick: (CategoryListData, String) -> Unit) : ListAdapter<CategoryListData, ProductHorizontalHolder>(ProductHorizontalDiffUtil) {
    inner class ProductHorizontalHolder(private val binding: ProductHorizonatlItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryListData) {
            binding.apply {
                Glide.with(root.context).load(item.image).placeholder(R.drawable.placeholder).into(productImage)
                categoryName.text = item.categoryName
                if (item.isSelected) {
                    LayoutBorder.setBackgroundResource(R.drawable.style_dash_line)
                } else {
                    LayoutBorder.setBackgroundResource(0)
                }
                root.setOnAnimatedClickListener {
                    onClick(item, "category")
                }
            }
        }
    }

    private object ProductHorizontalDiffUtil : DiffUtil.ItemCallback<CategoryListData>() {
        override fun areItemsTheSame(oldItem: CategoryListData, newItem: CategoryListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryListData, newItem: CategoryListData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHorizontalHolder {
        return ProductHorizontalHolder(
            ProductHorizonatlItemViewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductHorizontalHolder, position: Int) {
        holder.bind(getItem(position))
    }
}