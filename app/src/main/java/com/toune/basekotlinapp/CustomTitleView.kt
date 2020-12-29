package com.toune.basekotlinapp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.toune.dltools.view.title.DLCustomTitleView

/**
 * @Author Dong Lei
 * @Date 2020/12/28 0028-上午 11:34
 * @Info 描述：
 */
class CustomTitleView() : DLCustomTitleView() {
    override fun initView(context: Context,mRootView:ViewGroup): ViewGroup {
        var v = View.inflate(context,R.layout.layout_base_cus_title,mRootView) as ViewGroup
        v.findViewById<TextView>(R.id.titleTv).text = getTitleStr()
        return v
    }
}