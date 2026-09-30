package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ReviewsAllAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.updateRatingStars
import com.app.bharatphoton.databinding.ReviewAllItemViewBinding
import com.app.bharatphoton.module.reviewResponse.ReviewListResponse.ReviewListData
import com.app.bharatphoton.module.reviewResponse.ReviewListResponse.ReviewListData.Image
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewsAllAdapter(val onClick: (String, List<Image?>?) -> Unit) : PagingDataAdapter<ReviewListData, ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ReviewAllItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ViewHolder(private val binding: ReviewAllItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        private val reviewImageAdapter by lazy {
            ReviewImageAdapter { type, data ->
                when (type) {
                    "image" -> {
                       val allImages = snapshot().items.flatMap { it.image ?: emptyList() }
                        onClick("image", allImages)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: ReviewListData) {
            with(binding) {
                userName.text = data.userFirstName + " " + data.userLastName
                reviewTv.text = data.review
                updateRatingStars(rating = data.rating?.toInt() ?: 0, view = ratingLayout, context = root.context)
                Glide.with(root.context).load(data.userProfileImage).placeholder(R.drawable.placeholder).into(userProfile)
                val date = formatReadableDate(dateString = data.createdAt ?: "", simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                dateTv.text = date

                imageRv.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = reviewImageAdapter
                }

                reviewImageAdapter.submitList(data.image)

            }
        }
    }

    private object DiffUtil : ItemCallback<ReviewListData>() {
        override fun areItemsTheSame(oldItem: ReviewListData, newItem: ReviewListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewListData, newItem: ReviewListData): Boolean {
            return oldItem == newItem
        }
    }

}