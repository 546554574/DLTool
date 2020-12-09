package com.toune.basekotlinapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toune.dltools.DLActivityTool
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView
import com.toune.dltools.DLToast
import com.toune.dltools.ui.DLBaseActivity
import com.toune.dltools.view.DLPathView
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : DLBaseActivity<DLBaseView,DLBasePresenterImpl<DLBaseView>>() {
    override val layout: Int
        get() = R.layout.activity_splash
    override val titleStr: String?
        get() = null

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }

    override fun init(savedInstanceState: Bundle?) {
        pathView.starDrawPath()
        pathView.onEndListener = object :DLPathView.OnEndListener{
            override fun endListener() {
                DLActivityTool.skipActivityAndFinish(this@SplashActivity,MainActivity::class.java)
            }
        }
    }

    override fun initEventAndData() {
    }
}