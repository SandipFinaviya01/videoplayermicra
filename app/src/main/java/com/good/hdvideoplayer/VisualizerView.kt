package com.good.hdvideoplayer


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class AudioVisualizerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 4f
    }
    private var waveform: ByteArray = ByteArray(0)

    fun updateVisualizer(data: ByteArray) {
        waveform = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (waveform.isEmpty()) return
        val height = height
        val width = width
        val centerY = height / 2
        for (i in waveform.indices) {
            val x = width * i / waveform.size
            val y = (waveform[i] + 128) * height / 256
            canvas.drawLine(x.toFloat(), centerY.toFloat(), x.toFloat(), y.toFloat(), paint)
        }
    }
}