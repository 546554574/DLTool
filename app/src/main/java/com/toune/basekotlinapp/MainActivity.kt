package com.toune.basekotlinapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.rxbus.RxBus
import com.toune.basekotlinapp.adapter.MainAdapter
import com.toune.basekotlinapp.fragment.MDialogFragment
import com.toune.basekotlinapp.fragment.TextFragment
import com.toune.basekotlinapp.fragment.ToastFragment
import com.toune.basekotlinapp.presenter.MainActivityPresenter
import com.toune.basekotlinapp.view.MainActivityView
import com.toune.dltools.DLActivityTool
import com.toune.dltools.ui.DLBaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DLBaseActivity<MainActivityView,MainActivityPresenter>(),MainActivityView {
    override val layout: Int
        get() = R.layout.activity_main
    override val titleStr: String?
        get() = ""

    override fun initPresenter(): MainActivityPresenter {
        return MainActivityPresenter()
    }

    val dataList = arrayListOf<String>("吐司","弹窗","艺术字")
    val fragmentList = arrayListOf(ToastFragment.newInstance(),MDialogFragment.newInstance(),TextFragment.newInstance())
    var adapter: MainAdapter?=null
    override fun init(savedInstanceState: Bundle?) {
        notifyAdapter()
    }

    private fun notifyAdapter() {
        if (adapter==null){
            adapter = MainAdapter(R.layout.adapter_main,dataList)
            rclView.layoutManager = GridLayoutManager(context,3)
            rclView.adapter = adapter
            adapter!!.setOnItemClickListener { adapter, view, position ->
                var bundle = Bundle()
                bundle.putString("title",dataList[position])
                DLActivityTool.skipActivity(context,ChildrenActivity::class.java,bundle)
                RxBus.getDefault().postSticky(UpdateFragmentMsg(fragmentList[position]))
            }
        }else{
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun initEventAndData() {
    }

}