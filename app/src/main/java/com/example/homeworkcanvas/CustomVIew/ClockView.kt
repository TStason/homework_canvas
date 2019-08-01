package com.example.homeworkcanvas.CustomVIew

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.homeworkcanvas.R
import kotlin.math.truncate

class ClockView(context: Context, attrs: AttributeSet): View(context, attrs){
    private val TAG = "ClockView"

    //time
    var hour: Int = 0
        private set
    var minute: Int = 0
        private set
    var second: Int = 0
        private set
    private lateinit var hourArrow: HArrow
    private lateinit var secondArrow: SArrow
    private lateinit var minuteArrow: MArrow
    //arrow line points
    private var yStopArrow = 0f
    private var yStopHarrow = 0f
    private var yStopMarrow = 0f
    private var yStopSarrow = 0f
    //-----------------
    //options
    var isRun: Boolean = false
    private var isFirstDraw: Boolean = true
    private val handler1: Handler
    private lateinit var runnable: Runnable
    private val margin = 10f
    private var middleX: Float = 0f
    private var middleY: Float = 0f
    private var radius: Float = 0f
    private var ratio: Float = 0f
    //clock round
    private lateinit var circle: ClockCircle
    //clock face LISTS
    private val clockfaceLinesFat = mutableListOf<ClockFaceLine>()
    //Test
    /*private var linesFatPointArray = arrayListOf<Float>()
    private var linesSlimPointArray = arrayListOf<Float>()
    private var linesSlimShortPointArray = arrayListOf<Float>()
    private lateinit var linesFloatArray1: FloatArray
    private lateinit var linesFloatArray2: FloatArray
    private lateinit var linesFloatArray3: FloatArray*/
    private val clockfaceLinesSlim = mutableListOf<ClockFaceLine>()
    private val clockfaceLinesSlimShort = mutableListOf<ClockFaceLine>()
    private val clockfaceTextFat = mutableListOf<ClockFaceText>()
    private val clockfaceTextSlim = mutableListOf<ClockFaceText>()
    //paints
    private val paintFatBlack: Paint
    private val paintSlimGray: Paint
    private val paintRed: Paint
    private val paintArrowHblack: Paint
    private val paintArrowMgray: Paint
    private val paintArrowSgreen: Paint

    init{
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0)
        hour = a.getInt(R.styleable.ClockView_hour, 0)
        minute = a.getInt(R.styleable.ClockView_minute, 0)
        second = a.getInt(R.styleable.ClockView_second, 0)
        paintFatBlack = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
            textSize = 20f
            textAlign = Paint.Align.LEFT
        }
        paintSlimGray = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 4f
            textSize = 10f
            textAlign = Paint.Align.LEFT
        }
        paintRed = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = 20f
        }
        paintArrowHblack = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
        paintArrowMgray = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        paintArrowSgreen = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 7f
        }

        handler1 = Handler()
        runnable = Runnable {
            handler1.postDelayed({
                secondArrow.oneTick()
                second = secondArrow.value
                if (second == 0){
                    minuteArrow.oneTick()
                    minute = minuteArrow.value
                    if (minute == 0 )
                        hourArrow.oneTick()
                    hour = hourArrow.value
                }
                //requestLayout()
                setClockArrows()
                setClockArrowsOnTime()
                //Log.e(TAG, "Current time $hour $minute $second")
                invalidate()
            }, 0)
            handler1.postDelayed(runnable, 1000)
        }
    }
    fun start(){
        handler1.removeCallbacks(runnable)
        handler1.postDelayed(runnable, 0)
        isRun = true
    }

    fun stop(){
        handler1.removeCallbacks(runnable)
        isRun = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED){
            Log.e(TAG, "onMeasure UNSPEC")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        if (widthMode == MeasureSpec.AT_MOST){
            Log.e(TAG, "onMeasure AT_MOST")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        if (widthMode == MeasureSpec.EXACTLY){
            Log.e(TAG, "onMeasure EXACTLY")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            middleX = measuredWidth.toFloat() / 2
        } else {
            middleX = 200f * resources.displayMetrics.density
        }
        if (heightMode == MeasureSpec.EXACTLY){
            Log.e(TAG, "onMeasure EXACTLY")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            middleY = measuredHeight.toFloat() / 2
        } else {
            middleY = 200f * resources.displayMetrics.density
        }

        radius = minOf(middleX, middleY) - margin
        ratio = radius / 100//resources.displayMetrics.density

        if (isFirstDraw){
            setCircle()
            setClockFace()
            updatePaints()
            setClockArrows()
            isFirstDraw = false
        }
        setClockArrowsOnTime()
    }

    override fun onDraw(canvas: Canvas?) {
        //Test
        //canvas?.drawLines(linesFloatArray3, paintSlimGray)
        //canvas?.drawLines(linesFloatArray2, paintSlimGray)
        //canvas?.drawLines(linesFloatArray1, paintFatBlack)

        for (line in clockfaceLinesSlimShort){
            canvas?.drawLine(line.startX, line.startY, line.stopX, line.stopY, paintSlimGray)
        }
        for (line in clockfaceLinesSlim){
            canvas?.drawLine(line.startX, line.startY, line.stopX, line.stopY, paintSlimGray)
        }
        for (line in clockfaceLinesFat){
            canvas?.drawLine(line.startX, line.startY, line.stopX, line.stopY, paintFatBlack)
        }
        for (text in clockfaceTextFat){
            canvas?.drawText(text.text, text.x, text.y, paintFatBlack)
            //canvas?.drawCircle( text.x, text.y, 2f, paintFatBlack)
        }
        for (text in clockfaceTextSlim){
            canvas?.drawText(text.text, text.x, text.y, paintSlimGray)
            //canvas?.drawCircle( text.x, text.y, 2f, paintSlimGray)
        }
        canvas?.drawCircle(
            circle.x,
            circle.y,
            circle.rad,
            paintFatBlack
        )
        canvas?.drawLine(hourArrow.startX, hourArrow.startY, hourArrow.stopX, hourArrow.stopY, paintArrowHblack)
        canvas?.drawLine(minuteArrow.startX, minuteArrow.startY, minuteArrow.stopX, minuteArrow.stopY, paintArrowMgray)
        canvas?.drawLine(secondArrow.startX, secondArrow.startY, secondArrow.stopX, secondArrow.stopY, paintArrowSgreen)
        canvas?.drawCircle(middleX, middleY, ratio*5, paintRed)
    }

    private fun setCircle(){
        circle = ClockCircle(middleX, middleY, radius)
    }

    private fun setClockFace(){
        //face
        val xStart = middleX
        val yStart = middleY - radius
        val yStopFat = yStart + ratio * 20
        val yStopSlim = yStart + ratio * 15
        val yStopSlimShort = yStart + ratio * 8
        val yTextFat = yStopFat + ratio * 10
        val yTextSlim = yStopSlim + ratio * 7

        val matrix1 = Matrix()
        val lineFat = ClockFaceLine(xStart, yStart, xStart, yStopFat)
        val lineSlim = ClockFaceLine(xStart, yStart, xStart, yStopSlim)
        val lineSlimShort = ClockFaceLine(xStart, yStart, xStart, yStopSlimShort)
        val textFat = ClockFaceText("", xStart, yTextFat)
        val textSlim = ClockFaceText("", xStart, yTextSlim)
        //set fat line
        for( i in 0..3){
            matrix1.setRotate(i*90f, middleX, middleY)
            val tmp = lineFat.copy().apply { transform(matrix1) }
            clockfaceLinesFat.add(tmp)
            //Test
            //linesFatPointArray.addAll(arrayOf(tmp.startX, tmp.startY, tmp.stopX, tmp.stopY))
            matrix1.reset()
        }
        //set slim line
        for (i in 0..11){
            matrix1.setRotate(i*30f, middleX, middleY)
            val tmp = lineSlim.copy().apply { transform(matrix1) }
            clockfaceLinesSlim.add(tmp)
            //Test
            //linesSlimPointArray.addAll(arrayOf(tmp.startX, tmp.startY, tmp.stopX, tmp.stopY))
            matrix1.reset()
        }
        //set slim short line
        for( i in 0..59){
            matrix1.setRotate(i*6f, middleX, middleY)
            val tmp = lineSlimShort.copy().apply { transform(matrix1) }
            clockfaceLinesSlimShort.add(tmp)
            //Test
            //linesSlimShortPointArray.addAll(arrayOf(tmp.startX, tmp.startY, tmp.stopX, tmp.stopY))
            matrix1.reset()
        }
        //Test
        //linesFloatArray1 = linesFatPointArray.toFloatArray()
        //linesFloatArray2 =linesSlimPointArray.toFloatArray()
        //linesFloatArray3 = linesSlimShortPointArray.toFloatArray()
        //set text
        for (i in 1..12){
            matrix1.setRotate(i*30f, middleX, middleY)
            if (i % 3 == 0){
                val tmp = textFat.copy().apply {
                    text = i.toString()
                    transform(matrix1)
                    //kostyl
                    resizeWithMeasure(paintFatBlack.measureText(text), (paintFatBlack.descent() + paintFatBlack.ascent()))
                }
                clockfaceTextFat.add(tmp)
            } else {
                val tmp = textSlim.copy().apply {
                    text = i.toString()
                    transform(matrix1)
                    //kostyl
                    resizeWithMeasure(paintFatBlack.measureText(text), (paintSlimGray.descent() + paintSlimGray.ascent()))
                }
                clockfaceTextSlim.add(tmp)
            }
            matrix1.reset()
        }
        //Log.e(TAG, "Current time $hour $minute $second")
    }

    private fun setClockArrows(){
        if (isFirstDraw) {
            yStopArrow = middleY - radius
            yStopHarrow = yStopArrow + ratio * 45
            yStopMarrow = yStopArrow + ratio * 40
            yStopSarrow = yStopArrow + ratio * 30
        }
        hourArrow = HArrow(middleX, middleY, middleX, yStopHarrow)
        minuteArrow = MArrow(middleX, middleY, middleX, yStopMarrow)
        secondArrow = SArrow(middleX, middleY, middleX, yStopSarrow)
    }

    private fun updatePaints(){
        paintFatBlack.textSize = ratio * 10
        paintFatBlack.strokeWidth = ratio * 5
        paintSlimGray.textSize = ratio * 5
        paintSlimGray.strokeWidth = ratio * 2
        paintRed.strokeWidth = ratio * 10
        paintArrowHblack.strokeWidth = ratio * 5
        paintArrowMgray.strokeWidth = ratio * 2.5f
        paintArrowSgreen.strokeWidth = ratio * 2
    }

    private fun setClockArrowsOnTime(){
        hourArrow.setTime(hour + minute.toFloat() / 60)
        minuteArrow.setTime(minute + second.toFloat() / 60)
        secondArrow.setTime(second.toFloat())
    }

    fun setTime(h: Int, m: Int, s: Int){
        hour = h
        minute = m
        second = s
        //drop arrow to default
        setClockArrows()
        //and set time
        setClockArrowsOnTime()
    }

}

private data class ClockCircle(val x: Float, val y: Float, val rad: Float)

private data class ClockFaceText(var text: String, var x: Float, var y: Float){

    fun transform(matrix: Matrix){
        val tmp = x
        val coef = FloatArray(9)
        matrix.getValues(coef)
        x = x * coef[0] + y * coef[1] + coef[2]
        y = tmp * coef[3] + y * coef[4] + coef[5]
    }

    fun resizeWithMeasure(measureX: Float, measureY: Float){
        x -= measureX / 2
        y -= measureY / 2
    }
}

private class ClockFaceLine(startX: Float, startY: Float, stopX: Float, stopY: Float):
    Line(startX, startY, stopX, stopY){

    fun copy(startX: Float = this.startX,
             startY: Float = this.startY,
             stopX: Float = this.stopX,
             stopY: Float = this.stopY
    ) = ClockFaceLine(startX, startY, stopX, stopY)
    private val TAG = "CLOCKFACELINE"
    /*fun transform(matrix: Matrix){
        //Log.d(TAG, "$matrix")
        var tmp = startX
        val coef = FloatArray(9)
        matrix.getValues(coef)
        startX = startX* coef[0] + startY*coef[1] + coef[2]
        startY = tmp*coef[3] + startY*coef[4] + coef[5]
        tmp = stopX
        stopX = stopX* coef[0] + stopY*coef[1] + coef[2]
        stopY = tmp* coef[3] + stopY*coef[4] + coef[5]
    }*/
}

private class HArrow(startX: Float, startY: Float, stopX: Float, stopY: Float):
    Line(startX, startY, stopX, stopY), Arrow{
    var matrix1: Matrix
    var value: Int = 0
        private set
    init {
        matrix1 = Matrix()
        matrix1.setRotate(30f,startX, startY)
    }
    override fun oneTick(){
        //transform(matrix1)
        value = ++value % 24
    }
    override fun setTime(t: Float){
        value = truncate(t).toInt()
        matrix1.reset()
        matrix1.setRotate(t*30f, startX, startY)
        transform(matrix1)
        matrix1.reset()
        matrix1.setRotate(30f,startX, startY)
    }
}

private class SArrow(startX: Float, startY: Float, stopX: Float, stopY: Float):
    Line(startX, startY, stopX, stopY), Arrow{
    var matrix1: Matrix
    var value: Int = 0
        private set
    init {
        matrix1 = Matrix()
        matrix1.setRotate(6f,startX, startY)
    }
    override fun oneTick(){
        //transform(matrix1)
        value = ++value % 60
    }
    override fun setTime(t: Float){
        value = t.toInt()
        matrix1.reset()
        matrix1.setRotate(t*6f, startX, startY)
        transform(matrix1)
        matrix1.reset()
        matrix1.setRotate(6f,startX, startY)
    }
}

private class MArrow(startX: Float, startY: Float, stopX: Float, stopY: Float):
    Line(startX, startY, stopX, stopY), Arrow{
    var matrix1: Matrix
    val angle = 6f / 60
    var value: Int = 0
        private set
    init {
        matrix1 = Matrix()
        matrix1.setRotate(angle,startX, startY)
    }
    override fun oneTick(){
        //transform(matrix1)
        value = ++value % 60
    }
    override fun setTime(t: Float){
        value = truncate(t).toInt()
        matrix1.reset()
        matrix1.setRotate(t*6f, startX, startY)
        transform(matrix1)
        matrix1.reset()
        matrix1.setRotate(angle,startX, startY)
    }
}

private open class Line(var startX: Float, var startY: Float, var stopX: Float, var stopY: Float){
    fun transform(matrix: Matrix){
        //Log.d(TAG, "$matrix")
        var tmp = startX
        val coef = FloatArray(9)
        matrix.getValues(coef)
        startX = startX* coef[0] + startY*coef[1] + coef[2]
        startY = tmp*coef[3] + startY*coef[4] + coef[5]
        tmp = stopX
        stopX = stopX* coef[0] + stopY*coef[1] + coef[2]
        stopY = tmp* coef[3] + stopY*coef[4] + coef[5]
    }
}

interface Arrow{
    //val value with private set
    fun oneTick()
    //fun updateTime()
    fun setTime(t: Float)
}
