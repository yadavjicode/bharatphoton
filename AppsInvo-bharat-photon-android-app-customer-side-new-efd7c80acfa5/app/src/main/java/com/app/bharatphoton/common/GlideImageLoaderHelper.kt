package com.app.bharatphoton.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.bharatphoton.R

class GlideImageLoaderHelper {
    companion object {
        fun glide(context: Context, dataImage: String, progressBar: ProgressBar, imageView: ImageView) {
            com.bumptech.glide.Glide.with(context).load(dataImage).placeholder(R.drawable.placeholder).listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }


                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            }).into(imageView)
        }
    }
}