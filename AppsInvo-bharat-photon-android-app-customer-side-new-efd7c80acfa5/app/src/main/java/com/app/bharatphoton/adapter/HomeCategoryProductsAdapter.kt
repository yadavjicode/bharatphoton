package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.common.GlideImageLoaderHelper
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.HomeProductsItemViewBinding
import com.app.bharatphoton.module.homePageResponse.HomePageResponse.HomePageData.Category

class HomeCategoryProductsAdapter(val onClick: (Category, String) -> Unit) : ListAdapter<Category, HomeCategoryProductsAdapter.ModuleViewHolder>(HomeProductsDiffUtil) {
    inner class ModuleViewHolder(private val binding: HomeProductsItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                GlideImageLoaderHelper.glide(context = root.context, dataImage = item.image ?: "", progressBar = progressBar, imageView = productImg)
                productName.text = item.categoryName
            }
            itemView.setOnAnimatedClickListener {
                onClick(item, "products")
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

    private object HomeProductsDiffUtil : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

}