package com.toune.basekotlinapp

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.multidex.MultiDex
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.toune.dltools.DLTool
import com.toune.dltools.ui.DLActivityConfig

/**
 * @Author Dong Lei
 * @Date 2020/12/4 0004-下午 15:04
 * @Info 描述：
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DLTool.init(this)
            .setActivityConfig(DLActivityConfig.Builder().setLoadingColor(Color.YELLOW).setLoadingXml(R.layout.layout_loading_xml))
        //            .setLongCangFont()

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5b88f53c");
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}