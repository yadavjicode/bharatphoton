package com.app.bharatphoton.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.net.URL

object ImageGetterHelper {

    class GlideImageGetter(private val textView: TextView) : Html.ImageGetter {

        override fun getDrawable(source: String?): Drawable {
            val urlDrawable = URLDrawable()

            Glide.with(textView.context)
                .asBitmap()
                .load(source)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val maxWidth = textView.width - textView.paddingLeft - textView.paddingRight
                        val aspectRatio = resource.height.toFloat() / resource.width.toFloat()
                        val scaledHeight = (maxWidth * aspectRatio).toInt()

                        val drawable = BitmapDrawable(textView.resources, resource)
                        drawable.setBounds(0, 0, maxWidth, scaledHeight)
                        urlDrawable.drawable = drawable
                        urlDrawable.setBounds(0, 0, maxWidth, scaledHeight)

                        // Refresh textView so image appears
                        textView.text = textView.text
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

            return urlDrawable
        }

        private class URLDrawable : Drawable() {
            var drawable: Drawable? = null

            override fun draw(canvas: Canvas) {
                drawable?.draw(canvas)
            }

            override fun setAlpha(alpha: Int) {
                drawable?.alpha = alpha
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
                drawable?.colorFilter = colorFilter
            }

            @Deprecated("Deprecated in Java")
            override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
        }
    }
}

