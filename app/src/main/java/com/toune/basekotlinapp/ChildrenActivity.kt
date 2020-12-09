package com.toune.basekotlinapp

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blankj.rxbus.RxBus
import com.qmuiteam.qmui.arch.QMUIFragment
import com.toune.basekotlinapp.fragment.ToastFragment
import com.toune.basekotlinapp.presenter.ChildrenActivityPresenter
import com.toune.basekotlinapp.view.ChildrenActivityView
import com.toune.dltools.DLFragmentTool
import com.toune.dltools.ui.DLBaseActivity

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-下午 13:21
 * @Info 描述：
 */
class ChildrenActivity:DLBaseActivity<ChildrenActivityView,ChildrenActivityPresenter>(),ChildrenActivityView {
    override val layout: Int
        get() = R.layout.activity_children
    override val titleStr: String?
        get() = "演示"

    override fun initPresenter(): ChildrenActivityPresenter {
        return ChildrenActivityPresenter()
    }

    var fragment:Fragment?=null
    override fun init(savedInstanceState: Bundle?) {
        setToolBar(intent.extras!!.getString("title"))
        RxBus.getDefault().subscribeSticky(this,object :RxBus.Callback<UpdateFragmentMsg>(){
            override fun onEvent(t: UpdateFragmentMsg?) {
                if (fragment==null) {
                    fragment = t!!.fragment!!
                    DLFragmentTool.showFragment(supportFragmentManager,R.id.frameId,fragment!!)
                }
            }
        })
    }

    override fun initEventAndData() {
    }
}