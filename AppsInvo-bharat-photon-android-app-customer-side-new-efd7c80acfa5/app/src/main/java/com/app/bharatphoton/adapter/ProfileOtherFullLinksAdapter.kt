package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.ProfileOtherFullLinksAdapter.ProfileUserFullLinksViewHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.ProfileUserfullLinksItemViewBinding
import com.app.bharatphoton.module.Module

class ProfileOtherFullLinksAdapter(val onClick: (Int) -> Unit) :
    ListAdapter<Module, ProfileUserFullLinksViewHolder>(
        ProfileUserFullLinksDiffUtil
    ) {
    inner class ProfileUserFullLinksViewHolder(private val binding: ProfileUserfullLinksItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            with(binding) {
                icon.setImageResource(module.image)
                linkName.text = module.name
                root.setOnAnimatedClickListener {
                    onClick(layoutPosition)
                }
            }
        }
    }

    private object ProfileUserFullLinksDiffUtil : DiffUtil.ItemCallback<Module>() {
        override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ProfileUserFullLinksViewHolder {
        return ProfileUserFullLinksViewHolder(
            ProfileUserfullLinksItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileUserFullLinksViewHolder, position: Int) {
        val module = getItem(position)
        holder.bind(module)
    }
}