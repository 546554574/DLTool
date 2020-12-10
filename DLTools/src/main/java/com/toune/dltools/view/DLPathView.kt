package com.toune.dltools.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import com.eftimoff.androipathview.PathView
import com.toune.dltools.DLColorTool
import com.toune.dltools.DLTextTool
import com.toune.dltools.R

/**
 * Created by Administrator on 2018/4/25.
 */
@RequiresApi(Build.VERSION_CODES.O)
class DLPathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    val NORMAL = 0
    val SANS = 1
    val SERIF = 2
    val MONISPACE = 3
    val LONGCANG = 4
    val ZHIMANGXING = 5

    val STROKE = 0
    val FILL = 1
    val FILLANDSTROKE = 2

    val mLeft = 0
    val mRight = 1
    val mCenter = 2
    val mCenterInParent = 3

    var text = "DLTool"
    var mTextColor = Color.parseColor(DLColorTool.randomColor())
    private var mBackgroundColor = Color.parseColor(DLColorTool.randomColor())
    var mTypeFaceIndex = 4
    var mPaintTypeIndex = 0
    var mGravityIndex = 0
    var pathSize = 60f
    var textSize = 130
    var mTypeFace = DLTextTool.getLongCangTypeFace()
    private var textPaint: Paint
    private var pathMeasure = PathMeasure()
    private var textPath: Path? = null
    private var drawingPath: Path? = null
    var duration: Long = 6000

    //测量Path具体范围
    private val mPathBounds = RectF()
    private var drawAnimator: ValueAnimator? = null
    private val position = FloatArray(2) //当前绘制点坐标
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        textPath!!.computeBounds(mPathBounds, true)
    }

    private var pathLength: Float = 0f
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //由于 path 绘制是从左上顶点坐标开始绘制的，所以画布平移距离
        var x = 0f
        var y = 0f
        when (mGravityIndex) {
            mLeft -> {
                x = -mPathBounds.left
                y = 0f
            }
            mRight -> {
                x = width - mPathBounds.width() / 2
                y = 0f
            }
            mCenter -> {
                x = width / 2 - mPathBounds.left - mPathBounds.width() / 2
                y = 0f
            }
            mCenterInParent -> {
                x = width / 2 - mPathBounds.left - mPathBounds.width() / 2
                y = height / 2 - mPathBounds.top - mPathBounds.height() / 2
            }
        }
        canvas.translate(x,y)
        canvas.drawPath(drawingPath!!, textPaint)
    }

    var isFinish = false
    fun starDrawPath() {
        drawAnimator = ValueAnimator.ofFloat(0f, pathLength)
        drawAnimator!!.duration = duration
        drawAnimator!!.interpolator = LinearInterpolator()
        drawAnimator!!.addUpdateListener(AnimatorUpdateListener { animation ->
            var animatedValue = animation.animatedValue as Float
            pathMeasure.setPath(textPath, false)
            drawingPath!!.reset()
            while (animatedValue > pathMeasure.length) {
                animatedValue -= pathMeasure.length
                //获取之前几个片段路径保存在 drawingPath 中
                pathMeasure.getSegment(0f, pathMeasure.length, drawingPath, true)
                if (!pathMeasure.nextContour()) {
                    break
                }
            }
            val clipRes = pathMeasure.getSegment(0f, animatedValue, drawingPath, true)
            pathMeasure.getPosTan(animatedValue, position, null)
            postInvalidate()
        })
        drawAnimator!!.addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                if (!isFinish) {
                    if (onEndListener != null && !isFinish) {
                        onEndListener!!.endListener()
                        isFinish = true
                    }
                }
            }
        })
        drawAnimator!!.start()
    }

    private var drawLength = 0f

    /**
     * TODO 当前点画的圆出不来，待修改
     */
    private fun starDrawPath1() {
        drawLength = 0f
        drawingPath!!.reset()
        drawAnimator = ValueAnimator.ofFloat(0f, pathLength)
        drawAnimator!!.setDuration(6000)
        drawAnimator!!.setInterpolator(LinearInterpolator())
        pathMeasure.setPath(textPath, false)
        drawLength = pathMeasure.length
        drawAnimator!!.addUpdateListener(AnimatorUpdateListener { animation ->
            var animatedValue = animation.animatedValue as Float
            Log.i("pathView", "anim value = $animatedValue")
            //判断执行动画的长度，大于当前路径片段（一个字为一个片段）的长度，就去下一路径片段
            //进行同样的判断，同时将上一片段的路径信息保存到 drawingPath 中，否则再次绘制前面
            //的路径会没有
            if (animatedValue >= drawLength) {
//                    pathMeasure.getSegment(0, pathMeasure.getLength(), drawingPath, true);
                animatedValue -= drawLength
                if (pathMeasure.nextContour()) {
                    drawLength += pathMeasure.length
                    //                        pathMeasure.getSegment(0, animatedValue, drawingPath, true);
                }
            }
            if (animatedValue <= drawLength) {
//                    pathMeasure.getSegment(0, drawLength - pathMeasure.getLength(), drawingPath, true);
                animatedValue -= (drawLength - pathMeasure.length)
            }
            val clipRes = pathMeasure.getSegment(0f, animatedValue, drawingPath, true)
            pathMeasure.getPosTan(animatedValue, position, null)
            Log.i(
                "position",
                "clipRes = " + clipRes + "x = " + position[0] + "  y = " + position[1]
            )
            invalidate()
        })
        drawAnimator!!.start()
    }

    fun stopDrawPath() {
        if (drawAnimator != null && drawAnimator!!.isRunning) drawAnimator!!.cancel()
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PathView)
        textSize = typedArray.getDimensionPixelSize(R.styleable.PathView_textSize, 130)
        text = typedArray.getString(R.styleable.PathView_text)!!
        mTextColor = typedArray.getColor(R.styleable.PathView_textColor, mTextColor)
        mBackgroundColor =
            typedArray.getColor(R.styleable.PathView_backgroundColor, mBackgroundColor)
        duration = typedArray.getInteger(R.styleable.PathView_duration, duration.toInt()).toLong()
        mTypeFaceIndex = typedArray.getInteger(R.styleable.PathView_typeface, 0)
        pathSize = typedArray.getFloat(R.styleable.PathView_pathSize, 60f)
        mPaintTypeIndex = typedArray.getInteger(R.styleable.PathView_paintStyle, 0)
        mGravityIndex = typedArray.getInteger(R.styleable.PathView_gravity, 0)
        typedArray.recycle()
        //关闭硬件加速，否则 drawPath 不显示
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        resetPaint()
    }

    fun resetPaint() {
        when (mTypeFaceIndex) {
            NORMAL -> {
                mTypeFace = Typeface.MONOSPACE
            }
            SANS -> {
                mTypeFace = Typeface.SANS_SERIF
            }
            SERIF -> {
                mTypeFace = Typeface.SERIF
            }
            MONISPACE -> {
                mTypeFace = Typeface.MONOSPACE
            }
            LONGCANG -> {
                mTypeFace = DLTextTool.getLongCangTypeFace()
            }
            ZHIMANGXING -> {
                mTypeFace = DLTextTool.getZhiMangXingTypeFace()
            }
        }
        when (mPaintTypeIndex) {
            STROKE -> textPaint.style = Paint.Style.STROKE
            FILL -> textPaint.style = Paint.Style.FILL
            FILLANDSTROKE -> textPaint.style = Paint.Style.FILL_AND_STROKE
        }
        textPaint.strokeWidth = pathSize
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = mTextColor
        textPaint.textSize = textSize.toFloat()
        textPaint.typeface = mTypeFace
        textPath = Path()
        drawingPath = Path()
        textPaint.getTextPath(
            text,
            0,
            text.length,
            (width / 2).toFloat(),
            -textPaint.fontMetrics.ascent,
            textPath
        )
        pathMeasure.setPath(textPath, false)
        pathLength = pathMeasure.length
        while (pathMeasure.nextContour()) {
            pathLength += pathMeasure.length
        }
    }

    var onEndListener: OnEndListener? = null

    interface OnEndListener {
        fun endListener()
    }
}