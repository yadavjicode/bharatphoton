package app.bharatphoton.service.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import androidx.core.graphics.drawable.toDrawable

class GlideImageGetter(
    private val context: Context,
    private val textView: TextView
) : Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable {
        val urlDrawable = UrlDrawable()

        Glide.with(context)
            .asBitmap()
            .load(source)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val drawable = resource.toDrawable(context.resources)

                    val width = textView.width.takeIf { it > 0 } ?: 300
                    val ratio = resource.height.toFloat() / resource.width
                    val height = (width * ratio).toInt()

                    drawable.setBounds(0, 0, width, height)

                    urlDrawable.drawable = drawable
                    urlDrawable.setBounds(0, 0, width, height)

                    textView.text = textView.text
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        return urlDrawable
    }

    class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }
    }
}


