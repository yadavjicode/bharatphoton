package app.bharatphoton.service.utils

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator

/*fun View.setOnClickWithAnimation(delay: Long = 100, action: () -> Unit) {
    this.setOnClickListener {
        if (!this.isClickable) return@setOnClickListener
        this.isClickable = false

        this.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
            .start()

        this.postDelayed({
            action.invoke()
            this.isClickable = true
        }, delay + 200)
    }
}*/

fun View.setOnClickWithAnimation(
    delay: Long = 100,
    action: () -> Unit
) {
    this.setOnClickListener {
        this.isEnabled = false
        this.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
            .start()
        this.postDelayed({
            action.invoke()
            this.isEnabled = true
        }, delay + 200)
    }
}

object ClickDebounceUtil {
    private var lastClickTime = 0L
    private const val MIN_CLICK_INTERVAL = 1000L

    fun canClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastClickTime >= MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }
}

