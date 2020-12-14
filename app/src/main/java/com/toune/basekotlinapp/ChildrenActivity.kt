package com.toune.basekotlinapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.rxbus.RxBus
import com.qmuiteam.qmui.arch.QMUIFragment
import com.toune.basekotlinapp.fragment.ToastFragment
import com.toune.basekotlinapp.presenter.ChildrenActivityPresenter
import com.toune.basekotlinapp.view.ChildrenActivityView
import com.toune.dltools.DLFragmentTool
import com.toune.dltools.DLToast
import com.toune.dltools.ui.DLBaseActivity

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-下午 13:21
 * @Info 描述：
 */
class ChildrenActivity : DLBaseActivity<ChildrenActivityView, ChildrenActivityPresenter>(),
    ChildrenActivityView {

    companion object {
        val REQUESTCODE_FROM_ACTIVITY = 1000
    }

    override val layout: Int
        get() = R.layout.activity_children
    override val titleStr: String?
        get() = "演示"

    override fun initPresenter(): ChildrenActivityPresenter {
        return ChildrenActivityPresenter()
    }

    var fragment: Fragment? = null
    override fun init(savedInstanceState: Bundle?) {
        setToolBar(intent.extras!!.getString("title"))
        val position = intent.extras!!.getInt("position")
        if (fragment == null) {
            fragment = MainActivity.fragmentList[position]
            DLFragmentTool.showFragment(supportFragmentManager, R.id.frameId, fragment!!)
        }else{
            DLFragmentTool.hideFragment(supportFragmentManager,fragment!!)
            fragment = MainActivity.fragmentList[position]
            DLFragmentTool.showFragment(supportFragmentManager, R.id.frameId, fragment!!)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == QMUIFragment.RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //如果是文件选择模式，需要获取选择的所有文件的路径集合
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                val list: List<String> = data!!.getStringArrayListExtra("paths")
                RxBus.getDefault().postSticky(SelectFileMsg(list[0]))
                DLToast.showToast(list[0])
                //如果是文件夹选择模式，需要获取选择的文件夹路径
//                val path = data!!.getStringExtra("path")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun initEventAndData() {
    }
}