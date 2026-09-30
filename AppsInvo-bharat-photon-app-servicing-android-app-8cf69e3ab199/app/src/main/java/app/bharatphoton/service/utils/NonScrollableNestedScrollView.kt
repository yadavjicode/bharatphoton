package app.bharatphoton.service.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class NonScrollableNestedScrollView(context: Context, attrs: AttributeSet) :
    NestedScrollView(context, attrs) {
    var isScrollable: Boolean = true
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onInterceptTouchEvent(ev)
    }
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onTouchEvent(ev)
    }
}