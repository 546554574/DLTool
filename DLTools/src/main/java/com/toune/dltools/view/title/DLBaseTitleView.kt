package com.toune.dltools.view.title

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.toune.dltools.DLActivityTool
import com.toune.dltools.R
import com.toune.dltools.ui.DLActivityConfig
import kotlinx.android.synthetic.main.layout_base_activity_title.view.*

/**
 * @Author Dong Lei
 * @Date 2020/12/25 0025-下午 16:48
 * @Info 描述：
 */
class DLBaseTitleView(context: Context,mRootViewGroup: ViewGroup,titleString: String?) : DLCustomTitleView(titleString) {

    private var mRootTitileView: ViewGroup? = null

    fun addRightView(view: View) {
        mRootTitileView!!.rightLv.addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
    var cxt:Context = context
    var viewGroup = mRootViewGroup
    override fun initView(context: Context,mRootViewGroup: ViewGroup): ViewGroup {
        mRootTitileView =
            View.inflate(cxt, R.layout.layout_base_activity_title, mRootViewGroup) as ViewGroup
        //标题  ：布局文件中引入lyout_title
        mRootTitileView!!.titleRv.setBackgroundColor(DLActivityConfig.defTitleBgColor)
        mRootTitileView!!.titleTv.setTextColor(DLActivityConfig.defTitleTextColor)
        mRootTitileView!!.titleTv.textSize = DLActivityConfig.defTitleTextSize
        if (DLActivityConfig.defTitleBackIvImgRes != null) {
            mRootTitileView!!.backIv.setImageResource(DLActivityConfig.defTitleBackIvImgRes!!)
        }
        mRootTitileView!!.titleTv.gravity = DLActivityConfig.defTitleGravity
        mRootTitileView!!.backLv.setOnClickListener(View.OnClickListener { onBackIv((context as Activity)) })
        mRootTitileView!!.titleTv.text = getTitleStr()
        return mRootTitileView!!
    }

    override fun setTitleStr(string: String?) {
        super.setTitleStr(string)
        mRootTitileView!!.titleTv.text = getTitleStr()
    }
}