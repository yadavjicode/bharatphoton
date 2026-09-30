package com.app.bharatphoton.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.TroubleShootingVideoAdapter.ViewHolder
import com.app.bharatphoton.databinding.YoutubePlayerListItemViewBinding
import com.app.bharatphoton.module.cmsResponse.TroubleShootVideos.TroubleShootVideoData.TroubleVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class TroubleShootingVideoAdapter : ListAdapter<TroubleVideo, ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(YoutubePlayerListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ViewHolder(private val binding: YoutubePlayerListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TroubleVideo) {
            binding.apply {
                val regex = Regex("""(?:youtu\.be/|v=|embed/|watch\?v=)([a-zA-Z0-9_-]{11})""")
                val matchResult = data.url?.let { it1 -> regex.find(it1) }
                val videoId = matchResult?.groupValues?.get(1)

                youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        super.onReady(youTubePlayer)
                        youTubePlayer.cueVideo(videoId = videoId ?: "", startSeconds = 0f)
                    }

                })
            }
        }
    }

    private object DiffUtil : ItemCallback<TroubleVideo>() {
        override fun areItemsTheSame(oldItem: TroubleVideo, newItem: TroubleVideo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TroubleVideo, newItem: TroubleVideo): Boolean {
            return oldItem == newItem
        }
    }

}