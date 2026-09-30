package com.app.bharatphoton.common

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import render.animations.Fade
import render.animations.Render

class RecyclerAnimationHelper {
    companion object {
        fun applyRecyclerAnimation(context: Context, recyclerView: RecyclerView) {
            recyclerView.apply {
                apply {
                    layoutAnimation = AnimationUtils.loadLayoutAnimation(
                        context, R.anim.layout_animation_slide_in
                    )
                    scheduleLayoutAnimation()
                }
            }
        }

        fun applyViewsAnimation(context: Context, views: List<View>) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            views.forEach { view ->
                view.startAnimation(animation)
            }
        }

        fun animation(context: Context, views: View) {
            val render = Render(context)
            render.setDuration(700)
            render.setAnimation(Fade().InUp(views))
            render.start()
        }


    }
}