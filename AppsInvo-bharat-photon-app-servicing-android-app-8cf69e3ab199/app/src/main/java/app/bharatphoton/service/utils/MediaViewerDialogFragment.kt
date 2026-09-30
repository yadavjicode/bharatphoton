package app.bharatphoton.service.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.fragment.app.DialogFragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentMediaViewerDialogBinding
import app.bharatphoton.service.utils.Constants.ALL_MEDIA_URLS
import app.bharatphoton.service.utils.Constants.CLICKED_MEDIA_URL
import com.bumptech.glide.Glide

class MediaViewerDialogFragment : DialogFragment() {
    private var _binding: FragmentMediaViewerDialogBinding? = null
    private val binding get() = _binding!!
    private var visibleMediaUrl: String? = null
    private var allMediaUrls: ArrayList<String>? = arrayListOf()
    private var currentIndex = 0
    private var player: ExoPlayer? = null

    override fun onResume() {
        super.onResume()
        showVideoViewerDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentMediaViewerDialogBinding.inflate(layoutInflater)

        allMediaUrls = arguments?.getStringArrayList(ALL_MEDIA_URLS) ?: arrayListOf()
        visibleMediaUrl = arguments?.getString(CLICKED_MEDIA_URL, "") ?: ""
        currentIndex = allMediaUrls?.indexOf(visibleMediaUrl) ?: 0

        if (isVideo(visibleMediaUrl)) {
            player = ExoPlayer.Builder(requireContext()).build()
        }

        binding.apply {
            ivBack.setOnClickWithAnimation { dismiss() }

            ivShare.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                val message = if (isVideo(visibleMediaUrl)) "Share this video..." else "Share this image..."
                (context as BaseActivity).showAnimatedCustomToast(message)
                visibleMediaUrl?.let { shareTextToOtherApp(it) }
            }

            cvNext.setOnClickListener { navigateToMedia(1) }
            cvPrev.setOnClickListener { navigateToMedia(-1) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupMediaViewer()
        return binding.root
    }

    private fun setupMediaViewer() {
        updateNavigationButtons()
        binding.apply {
            if (isVideo(visibleMediaUrl)) {
                setupVideoViewer()
            } else {
                setupImageViewer()
            }
        }
    }

    @SuppressLint("UseKtx")
    @OptIn(UnstableApi::class)
    private fun setupVideoViewer() {
        binding.apply {
            cvVideo.visibility = View.VISIBLE
            ivImage.visibility = View.GONE

            if (player == null) {
                player = ExoPlayer.Builder(requireContext()).build()
            }

            vvVideo.player = player
            val uri = Uri.parse(visibleMediaUrl)
            val cacheDataSourceFactory = createCacheDataSourceFactory()
            val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))

            player?.apply {
                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true
            }
        }
    }

    private fun setupImageViewer() {
        binding.apply {
            cvVideo.visibility = View.GONE
            ivImage.visibility = View.VISIBLE

            Glide.with(requireContext())
                .load(visibleMediaUrl)
                .into(ivImage)
        }
    }

    private fun navigateToMedia(direction: Int) {
        currentIndex += direction
        visibleMediaUrl = allMediaUrls?.get(currentIndex)
        releasePlayer()
        setupMediaViewer()
    }

    private fun updateNavigationButtons() {
        binding.cvPrev.visibility = if (currentIndex > 0) View.VISIBLE else View.GONE
        binding.cvNext.visibility = if (currentIndex < (allMediaUrls?.size ?: 1) - 1) View.VISIBLE else View.GONE
    }

    private fun isVideo(url: String?): Boolean {
        return url?.endsWith(".mp4") == true || url?.endsWith(".mkv") == true
    }

    private fun releasePlayer() {
        player?.pause()
        player?.release()
        player = null
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }

    private fun showVideoViewerDialog() {
        val dialog = requireDialog()
        val window: Window? = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawableResource(R.drawable.white_uncornered_bg)
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    @OptIn(UnstableApi::class)
    private fun createCacheDataSourceFactory(): CacheDataSource.Factory {
        val cache = CacheManager.getCache(requireContext())
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun shareTextToOtherApp(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}

