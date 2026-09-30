package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.common.GlideImageLoaderHelper
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.HomeProductsItemViewBinding
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse.CategoryListData

class HomeProductsAdapter(val onClick: (String, CategoryListData) -> Unit) : ListAdapter<CategoryListData, HomeProductsAdapter.ModuleViewHolder>(HomeProductsDiffUtil) {
    inner class ModuleViewHolder(private val binding: HomeProductsItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryListData) {
            binding.apply {
                GlideImageLoaderHelper.glide(context = root.context, dataImage = item.image ?: "", progressBar = progressBar, imageView = productImg)
                productName.text = item.categoryName
            }
            itemView.setOnAnimatedClickListener {
                onClick("products", item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        return ModuleViewHolder(
            HomeProductsItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object HomeProductsDiffUtil : DiffUtil.ItemCallback<CategoryListData>() {
        override fun areItemsTheSame(oldItem: CategoryListData, newItem: CategoryListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryListData, newItem: CategoryListData): Boolean {
            return oldItem == newItem
        }
    }
}