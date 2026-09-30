package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ReviewAdapter.ReviewImageViewHolder
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.ReviewItemViewBinding
import com.app.bharatphoton.module.productResponse.ProductDetailsResponse.Data.Review

class ReviewAdapter : ListAdapter<Review, ReviewImageViewHolder>(ReviewImageDiffUtil) {
    inner class ReviewImageViewHolder(private val binding: ReviewItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Review) {
            with(binding) {
                try {
                    Glide.with(root.context).load(data.userProfile?.profileImage).placeholder(R.drawable.placeholder).into(userImage)
                }catch (e:Exception){
                    e.message.mlog()
                }
                userName.text = "${data.userProfile?.firstName} ${data.userProfile?.lastName}"
                review.text = data.review
                Utils.updateRatingStars(rating = data.rating ?: 0, view = ratingLayout, context = root.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        return ReviewImageViewHolder(
            ReviewItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}