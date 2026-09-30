package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.ReviewImageItemViewBinding
import com.app.bharatphoton.module.reviewResponse.ReviewListResponse.ReviewListData.Image

class ReviewImageAdapter(val onClick: (String, Image) -> Unit) : ListAdapter<Image, ReviewImageAdapter.ReviewImageViewHolder>(ReviewImageDiffUtil) {
    inner class ReviewImageViewHolder(private val binding: ReviewImageItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Image) {
            with(binding) {
                Glide.with(root.context).load(data.url).placeholder(R.drawable.placeholder).into(reviewImg)
                progressBar.visibility = View.GONE
                root.setOnAnimatedClickListener {
                    onClick("image", data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        return ReviewImageViewHolder(
            ReviewImageItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}