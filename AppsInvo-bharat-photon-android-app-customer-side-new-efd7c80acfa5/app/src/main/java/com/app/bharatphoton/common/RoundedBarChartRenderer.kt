package com.app.bharatphoton.common

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val radius = 30f

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val buffer: BarBuffer = mBarBuffers[index]
        val isSingleColor = dataSet.colors.size == 1

        for (j in buffer.buffer.indices step 4) {
            val left = buffer.buffer[j]
            val top = buffer.buffer[j + 1]
            val right = buffer.buffer[j + 2]
            val bottom = buffer.buffer[j + 3]

            if (!mViewPortHandler.isInBoundsLeft(right)) continue
            if (!mViewPortHandler.isInBoundsRight(left)) break

            val rect = RectF(left, top, right, bottom)

            mRenderPaint.color = if (isSingleColor) dataSet.color else dataSet.getColor(j / 4)

            // Clip rounded only on top side
            val path = Path().apply {
                addRoundRect(rect, floatArrayOf(
                    radius, radius, // top-left
                    radius, radius, // top-right
                    0f, 0f,         // bottom-right
                    0f, 0f          // bottom-left
                ), Path.Direction.CW)
            }

            c.drawPath(path, mRenderPaint)
        }
    }
}
