package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.AddMoreSitePhotosAdapter.ReviewImageViewHolder
import com.app.bharatphoton.databinding.SitePhotoItemViewBinding

class AddMoreSitePhotosAdapter(val onClick: (String, Int) -> Unit) : ListAdapter<String, ReviewImageViewHolder>(ReviewImageDiffUtil) {
    inner class ReviewImageViewHolder(private val binding: SitePhotoItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            with(binding) {
                binding.upload.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                Glide.with(root.context).load(data).placeholder(R.drawable.placeholder).into(imageView)

                delete.setOnClickListener {
                    onClick.invoke("Delete", layoutPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        return ReviewImageViewHolder(
            SitePhotoItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object ReviewImageDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}