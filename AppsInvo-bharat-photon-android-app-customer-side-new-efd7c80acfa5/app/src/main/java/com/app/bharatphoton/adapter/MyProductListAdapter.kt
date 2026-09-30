package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.MyProductListAdapter.ViewHolder
import com.app.bharatphoton.databinding.NestedProductListWithSubcategoryNameLayoutBinding
import com.app.bharatphoton.module.productResponse.InstalledProductListResponse.InstalledProductData
import com.app.bharatphoton.module.productResponse.InstalledProductListResponse.InstalledProductData.Product

class MyProductListAdapter(val onClick: (Product, String, String) -> Unit) : PagingDataAdapter<InstalledProductData, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: NestedProductListWithSubcategoryNameLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var orderId: String? = null

        private val nestedMyProductsAdapter by lazy {
            NestedMyProductsAdapter { data, type ->
                when (type) {
                    "ModuleLayout" -> {
                        orderId?.let { onClick(data, "ModuleLayout", it) }
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: InstalledProductData) {
            orderId = item.id
            with(binding) {
                nestedMyProductsAdapter.submitList(item.products)
                productListItemRv.apply {
                    adapter = nestedMyProductsAdapter
                    layoutManager = LinearLayoutManager(root.context)
                }
                subCategoryName.visibility = View.GONE
            }
        }
    }

    private object DiffUtil : ItemCallback<InstalledProductData>() {
        override fun areItemsTheSame(oldItem: InstalledProductData, newItem: InstalledProductData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InstalledProductData, newItem: InstalledProductData): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NestedProductListWithSubcategoryNameLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}