package com.toune.dltools.view.title

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blankj.rxbus.RxBus
import com.toune.dltools.DLActivityTool
import com.toune.dltools.ui.DLActivityConfig
import com.toune.dltools.ui.DLBaseActivity

/**
 * @Author Dong Lei
 * @Date 2020/12/28 0028-上午 11:29
 * @Info 描述：
 */
abstract class DLCustomTitleView {

    private var context: Context? = null
    private var view: ViewGroup? = null
    private var title: String? = null

    constructor() {
        reView(DLActivityConfig.context, null, null)
    }

    constructor(titleString: String?) {
        reView(DLActivityConfig.context, null, titleString)
    }

    open fun getMView(): ViewGroup? {
        return view
    }

    open fun getTitleStr(): String {
        return title!!
    }

    open fun setTitleStr(string: String?) {
        title = string
    }

    open fun onBackIv(activity: Activity) {
        DLActivityTool.finishActivity(activity)
    }

    fun reView(context: Context?, mRootView: ViewGroup?, titleStr: String?) {
        title = titleStr
        if (context != null && mRootView != null) {
            view = initView(context, mRootView)
        }
    }

    abstract fun initView(context: Context, mRootView: ViewGroup): ViewGroup


}