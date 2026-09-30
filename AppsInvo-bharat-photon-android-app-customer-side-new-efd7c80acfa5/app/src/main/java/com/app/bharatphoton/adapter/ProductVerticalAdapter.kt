@file:Suppress("KotlinConstantConditions")

package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.ProductVerticalAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.NestedProductListWithSubcategoryNameLayoutBinding
import com.app.bharatphoton.module.productResponse.ProductListingResponse.ProductListData.SubcategoryListing
import com.app.bharatphoton.module.productResponse.ProductListingResponse.ProductListData.SubcategoryListing.ProductList

class ProductVerticalAdapter(val onClick: (String, ProductList?, imageView: ImageView?, progressBar: ProgressBar?, data: SubcategoryListing?) -> Unit) :
    ListAdapter<SubcategoryListing, ViewHolder>(ProductHorizontalDiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NestedProductListWithSubcategoryNameLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: NestedProductListWithSubcategoryNameLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val nestedSubCategoryListAdapter by lazy {
            NestedSubCategoryListAdapter { type, data, imageView, progressBar ->
                onClick(type, data, imageView, progressBar, null)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: SubcategoryListing) {
            try {
                binding.apply {
                    subCategoryName.text = item.subcategoryName
                }
                if (item.id == null){
                    binding.subCategoryName.visibility = View.GONE
                }else{
                    binding.subCategoryName.visibility = View.VISIBLE
                }
                binding.viewAll.visibility = View.VISIBLE
                binding.viewAll.setOnAnimatedClickListener {
                    onClick("viewAll", null, null, null, item)
                }

                binding.productListItemRv.apply {
                    adapter = nestedSubCategoryListAdapter
                    layoutManager = GridLayoutManager(itemView.context, 2)
                }

                nestedSubCategoryListAdapter.submitList(item.products)
                nestedSubCategoryListAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                e.message.mlog()
            }


        }
    }

    private object ProductHorizontalDiffUtil : DiffUtil.ItemCallback<SubcategoryListing>() {
        override fun areItemsTheSame(oldItem: SubcategoryListing, newItem: SubcategoryListing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubcategoryListing, newItem: SubcategoryListing): Boolean {
            return oldItem == newItem
        }
    }

}