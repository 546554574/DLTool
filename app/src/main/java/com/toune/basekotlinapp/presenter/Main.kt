package com.toune.basekotlinapp.presenter

import android.os.Bundle
import com.toune.basekotlinapp.R
import com.toune.basekotlinapp.view.MainActivityView
import com.toune.dltools.ui.DLBaseActivity

/**
 * @Author Dong Lei
 * @Date 2020/12/25 0025-下午 16:13
 * @Info 描述：
 */
class Main:DLBaseActivity<MainActivityView,MainActivityPresenter>(),MainActivityView {
    override val layout: Int
        get() = R.layout.activity_main  //布局文件
    override val titleStr: String?
        get() = "首页"  //页面标题，返回""或者null则不显示

    override fun initPresenter(): MainActivityPresenter {
        return MainActivityPresenter()  //返回当前presenter
    }

    override fun init(savedInstanceState: Bundle?) {
        //业务逻辑
    }

    override fun initEventAndData() {
        //监听事件
    }
}