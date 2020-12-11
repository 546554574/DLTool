package com.toune.basekotlinapp.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.annotation.RequiresApi
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.toune.basekotlinapp.R
import com.toune.dltools.*
import com.toune.dltools.ui.DLBaseFragment
import kotlinx.android.synthetic.main.fragment_tts.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author Dong Lei
 * @Date 2020/12/10 0010-下午 14:59
 * @Info 描述：
 */
class TTSFragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {
    companion object {
        fun newInstance(): TTSFragment {
            val args = Bundle()
            val fragment = TTSFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val layout: Int
        get() = R.layout.fragment_tts
    var dlttsTool: DLTTSTool? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun lazyInit() {
        XXPermissions.with(requireActivity())
            .permission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(object : OnPermission {
                override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                }

                override fun noPermission(denied: MutableList<String>?, never: Boolean) {
                }
            })
        dlttsTool = DLTTSTool.with(requireContext(), false, object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                DLToast.showToast("开始播放")
            }

            override fun onDone(utteranceId: String?) {
                Log.e("onDone", "onDone: $utteranceId")
                try {
                    nextRead()
                }catch (e:Exception){
                    e.printStackTrace()
                    nextRead()
                }
            }

            override fun onError(utteranceId: String?) {
                DLToast.showErrorToast("播放错误")
                nextRead()
            }
        }).setLanguage(Locale.SIMPLIFIED_CHINESE)
            .setPitch(0.5f)
        startReadBtn.setOnClickListener {
            dlttsTool!!.setText("开始播放").speek()
        }
        stopReadBtn.setOnClickListener {
            dlttsTool!!.stop()
        }
        readFile("第3818章.txt")
    }

    private fun nextRead() {
        textStrTv.text = readFile2List[index]
        dlttsTool!!.setText(readFile2List[index]).speekAdd()
        index++
    }

    var readFile2List = ArrayList<String>()
    var index = 0
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun readFile(fileName: String) {
        dlttsTool!!.speek()
        val open = requireContext().assets.open(fileName)
        val readFile2List1 = DLFileTool.readFile2List(open, DLFileTool.UTF8code)
        if (readFile2List1 != null) {
            readFile2List.addAll(readFile2List1!! as MutableList<String>)
        }
    }

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }
}