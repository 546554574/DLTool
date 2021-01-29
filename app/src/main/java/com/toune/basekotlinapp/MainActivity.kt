package com.toune.basekotlinapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.rxbus.RxBus
import com.toune.basekotlinapp.adapter.MainAdapter
import com.toune.basekotlinapp.fragment.*
import com.toune.basekotlinapp.presenter.MainActivityPresenter
import com.toune.basekotlinapp.view.MainActivityView
import com.toune.dltools.DLActivityTool
import com.toune.dltools.ui.DLBaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DLBaseActivity<MainActivityView, MainActivityPresenter>(), MainActivityView {
    override val layout: Int
        get() = R.layout.activity_main
    override val titleStr: String?
        get() = ""

    override fun initPresenter(): MainActivityPresenter {
        return MainActivityPresenter()
    }

    companion object {
        val dataList = arrayListOf<String>("吐司", "弹窗", "艺术字", "二维码", "语音朗读", "文本转MP3", "http工具")
        val fragmentList = arrayListOf(
            ToastFragment.newInstance(),
            MDialogFragment.newInstance(),
            TextFragment.newInstance(),
            QRFragment.newInstance(),
            TTSFragment(),
            TextToMP3Fragment(),
            HttpFragment()
        )
    }

    var adapter: MainAdapter? = null
    override fun init(savedInstanceState: Bundle?) {
        showLoading()
        Handler().postDelayed({
            hideLoading()
        }, 3000)
        notifyAdapter()
    }

    private fun notifyAdapter() {
        if (adapter == null) {
            adapter = MainAdapter(R.layout.adapter_main, dataList)
            rclView.layoutManager = GridLayoutManager(context, 3)
            rclView.adapter = adapter
            adapter!!.setOnItemClickListener { adapter, view, position ->
                var bundle = Bundle()
                bundle.putString("title", dataList[position])
                bundle.putInt("position", position)
                DLActivityTool.skipActivity(context, ChildrenActivity::class.java, bundle)
//                DLActivityTool.skipActivity(context,TtsDemo::class.java)
            }
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initEventAndData() {
//        showUserPrivacyDialog(null, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}