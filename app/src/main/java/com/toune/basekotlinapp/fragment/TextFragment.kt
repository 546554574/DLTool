package com.toune.basekotlinapp.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.toune.basekotlinapp.R
import com.toune.dltools.*
import com.toune.dltools.ui.DLBaseFragment
import com.toune.dltools.view.DLPathView
import kotlinx.android.synthetic.main.fragment_text.*

/**
 * @Author Dong Lei
 * @Date 2020/12/9 0009-下午 13:14
 * @Info 描述：
 */
class TextFragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {
    companion object {
        fun newInstance(): TextFragment {
            val args = Bundle()
            val fragment = TextFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val layout: Int
        get() = R.layout.fragment_text

    var text = "Hello,开发者"
    var textSize = 60
    var duration = 6000
    var paintSize = 10f
    var colorStr = DLColorTool.randomColor()
    var mTypeFace = DLTextTool.getLongCangTypeFace()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun lazyInit() {
        pathView.starDrawPath()
        pathView.onEndListener = object : DLPathView.OnEndListener {
            override fun endListener() {
                DLToast.showSuccessToast("绘制完成")
            }
        }
        textEt.setText(text)
        textSizeEt.setText(textSize.toString())
        durationEt.setText(duration.toString())
        painSizeEt.setText(paintSize.toString())
        textColorEt.setText(colorStr)
        randomColorBtn.setOnClickListener {
            colorStr = DLColorTool.randomColor()
            textColorEt.setText(colorStr)
            pathView.mTextColor = Color.parseColor(colorStr)
        }
        locationRp.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.leftRb->{
                    pathView.mGravityIndex = pathView.mLeft
                }
                R.id.rightRb->{
                    pathView.mGravityIndex = pathView.mRight
                }
                R.id.centerRb->{
                    pathView.mGravityIndex = pathView.mCenter
                }
                R.id.centerInParentRb->{
                    pathView.mGravityIndex = pathView.mCenterInParent
                }
            }
        }
        styleRp.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.strokeRb->{
                    pathView.mPaintTypeIndex = pathView.STROKE
                }
                R.id.fillRb->{
                    pathView.mPaintTypeIndex = pathView.FILL
                }
                R.id.fillAndStrokeRb->{
                    pathView.mPaintTypeIndex = pathView.FILLANDSTROKE
                }
            }
        }
        fontRp.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.sansRb->{
                    pathView.mTypeFaceIndex = pathView.SANS
                }
                R.id.serifRb->{
                    pathView.mTypeFaceIndex = pathView.SERIF
                }
                R.id.longCangRb->{
                    pathView.mTypeFaceIndex = pathView.LONGCANG
                }
                R.id.zhiMangXingRb->{
                    pathView.mTypeFaceIndex = pathView.ZHIMANGXING
                }
            }
        }
        startBtn.setOnClickListener {
            pathView.text = textEt.text.toString()
            pathView.textSize = textSizeEt.text.toString().toInt()
            pathView.pathSize = painSizeEt.text.toString().toFloat()
            pathView.mTypeFace = mTypeFace
            pathView.resetPaint()
            pathView.starDrawPath()
        }

    }

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }
}