package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.FAQListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FaqListItemViewBinding
import com.app.bharatphoton.module.cmsResponse.FAQResponse.FAQData

class FAQListAdapter(val onClick: (String) -> Unit) : ListAdapter<FAQData, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: FaqListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: FAQData) {
            with(binding) {
                questionTv.text = data.question
                data.answer?.let {
                    answerTv.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }

                arrow.setOnAnimatedClickListener {
                    onClick("Expand")

                    if (expandedLayout.visibility == View.VISIBLE) {
                        val fadeOut = AnimationUtils.loadAnimation(root.context, R.anim.fade_out_slow)
                        fadeOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {}

                            override fun onAnimationEnd(animation: Animation?) {
                                expandedLayout.visibility = View.GONE
                            }

                            override fun onAnimationRepeat(animation: Animation?) {}
                        })
                        expandedLayout.startAnimation(fadeOut)
                        arrow.rotation = 0f
                    } else {
                        expandedLayout.visibility = View.VISIBLE
                        val fadeIn = AnimationUtils.loadAnimation(root.context, R.anim.fade_in)
                        val animationSet = AnimationSet(true).apply {
                            addAnimation(fadeIn)
                        }
                        expandedLayout.startAnimation(animationSet)
                        arrow.rotation = 90f
                    }

                }

            }
        }
    }

    private object DiffUtil : ItemCallback<FAQData>() {
        override fun areItemsTheSame(oldItem: FAQData, newItem: FAQData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FAQData, newItem: FAQData): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FaqListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}