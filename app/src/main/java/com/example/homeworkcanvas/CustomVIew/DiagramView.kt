package com.example.homeworkcanvas.CustomVIew

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DiagramView(context: Context, attrs: AttributeSet?): View(context, attrs) {

    val TAG = "DiagramView"

    var startEventRect: Int = 0
    private val defaultWidth = 100
    private val defaultTextSize = 50f
    private var bodyHeight: Int = 0

    var paint: Paint
    var paintText: Paint
    val list = mutableListOf(1, 4, 10, 3, 4, 5)
    private val listOfRect = mutableListOf<Pair<RectHeader,Rect>>()
    var maxValue = list.max() ?: 0
    var widthPerView = 0
    var heightPerValue = 0f
    init{
        paint = Paint()
        paint.color = Color.GREEN
        paintText = Paint()
        paintText.color = Color.BLACK
        paintText.textSize = defaultTextSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED){
            Log.e(TAG, "onMeasure UNSPEC")
            setMeasuredDimension(if (list.isEmpty()) 0 else defaultWidth * list.size, heightSize)
            widthPerView = if (list.isEmpty()) 0 else defaultWidth
        } else if (widthMode == MeasureSpec.AT_MOST){
            Log.e(TAG, "onMeasure AT_MOST")
        }else if (widthMode == MeasureSpec.EXACTLY){
            Log.e(TAG, "onMeasure EXACTLY")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            widthPerView = if (list.isEmpty()) 0 else measuredWidth / list.size
        }
        bodyHeight = measuredHeight - defaultTextSize.toInt()
        heightPerValue = if (list.isEmpty()) 0f else bodyHeight.toFloat() / maxValue
        Log.e(TAG, "\nheightPerValue $heightPerValue\nmaxValue $maxValue")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        listOfRect.clear()
        setListOfRect()
    }
    override fun onDraw(canvas: Canvas?) {
        if (list.isEmpty()) return
        for ((k, v) in listOfRect){
            canvas?.drawRect(v, paint)
            canvas?.drawText(k.num.toString(), k.x, k.y, paintText)
            if (paint.color == Color.GREEN) paint.color = Color.BLACK else paint.color = Color.GREEN
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return super.onTouchEvent(event)
        val x = event.x.toInt()
        val y = event.y.toInt()
        Log.d(TAG, "Touch in x: $x, y: $y")
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "Down")
                for ((k, v) in listOfRect){
                    if (v.contains(x, y)){
                        startEventRect = listOfRect.indexOf(Pair(k, v))
                        Log.e(TAG, "touched $startEventRect ")
                        break
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "UP")
                if (listOfRect[startEventRect].second.contains(x, y)){
                    //important Log.d :)------------------------------------->
                    Log.e(TAG, "Incremented ${list[startEventRect]} -> ${++list[startEventRect]}")
                    updateMaxValue()
                    requestLayout()
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> Log.d(TAG, "Move")
            else -> Log.d(TAG, "Another action")
        }

        return true
    }

    private fun setListOfRect(){
        var curX = 0
        for (item in list){
            val rect = Rect(
                    curX,
                    (height - heightPerValue * item).toInt(),
                    (curX + widthPerView),
                    height
            )
            val header = RectHeader(
                    item,
                    (curX + widthPerView / 2).toFloat(),
                    (height - heightPerValue * (item)).toFloat()
            )
            listOfRect.add(Pair(header, rect))
            curX += widthPerView
        }
    }

    private fun updateMaxValue(){
        list.max()?.let{
            if (maxValue < it)
                maxValue = it
        }
    }
}

class RectHeader(var num: Int, val x: Float, val y: Float)