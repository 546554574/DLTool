package com.toune.basekotlinapp.fragment

import android.Manifest
import android.os.Build
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.toune.basekotlinapp.R
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView
import com.toune.dltools.DLTTSTool
import com.toune.dltools.DLToast
import com.toune.dltools.ui.DLBaseFragment
import kotlinx.android.synthetic.main.fragment_ttm.*
import kotlinx.android.synthetic.main.fragment_ttm.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author Dong Lei
 * @Date 2020/12/15 0015-下午 14:55
 * @Info 描述：
 */
class TextToMP3Fragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {
    override val layout: Int
        get() = R.layout.fragment_ttm

    var dlTTSTool: DLTTSTool? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun lazyInit() {
        //请求权限
        XXPermissions.with(requireActivity())
            .permission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(object : OnPermission {
                override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                }

                override fun noPermission(denied: MutableList<String>?, never: Boolean) {
                }
            })

        dlTTSTool = DLTTSTool.with(requireContext(), false, object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {

            }

            override fun onDone(utteranceId: String?) {
            }

            override fun onError(utteranceId: String?) {
            }
        })
            .setLanguage(Locale.CHINESE)

        var array: Array<out String> = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        mRootView!!.ttmBtn.setOnClickListener {
            val toString = inputEt.text.toString()
            if (XXPermissions.hasPermission(requireContext(), array)) {
                dlTTSTool!!.setText(toString).toFile("测试文件")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dlTTSTool!!.stop()
    }

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }
}