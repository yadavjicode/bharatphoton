package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.HomeOffersViewPagerAdapter.HomeOffersViewHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog

class HomeOffersViewPagerAdapter(val onCLick:(String) -> Unit) : ListAdapter<String, HomeOffersViewHolder>(HomeDiscountDiffUtil) {
    inner class HomeOffersViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        fun bind(item: String) {
            try {
                val imageView = binding.findViewById<ImageView>(R.id.imageView)
                Glide.with(binding.rootView).load(item).placeholder(R.drawable.placeholder).into(imageView)
                binding.rootView.setOnAnimatedClickListener {
                    onCLick("image")
                }
            } catch (e: Exception) {
                e.message.mlog()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeOffersViewHolder {
        return HomeOffersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_offers_view_pager_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeOffersViewHolder, position: Int) {
        val imageResId = getItem(position)
        holder.bind(imageResId)
    }

    private object HomeDiscountDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}