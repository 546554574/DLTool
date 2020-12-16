package com.toune.basekotlinapp.fragment

import android.Manifest
import android.os.Build
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.blankj.rxbus.RxBus
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.leon.lfilepickerlibrary.LFilePicker
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.toune.basekotlinapp.ChildrenActivity.Companion.REQUESTCODE_FROM_ACTIVITY
import com.toune.basekotlinapp.R
import com.toune.basekotlinapp.SelectFileMsg
import com.toune.dltools.*
import com.toune.dltools.ui.DLBaseFragment
import kotlinx.android.synthetic.main.fragment_tts.view.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * @Author Dong Lei
 * @Date 2020/12/10 0010-下午 14:59
 * @Info 描述：
 */
class TTSFragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {

    override val layout: Int
        get() = R.layout.fragment_tts

    //播放工具类
    var dlttsTool: DLTTSTool? = null

    //文件编码，默认GBK
    var code = DLFileTool.GBKcode

    //选中的文件路径
    var fileName: String? = null

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

        //初始化播放工具类
        dlttsTool = DLTTSTool.with(requireContext(), false, object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                DLToast.showToast("开始播放")
            }

            override fun onDone(utteranceId: String?) {
                try {
                    Log.e("onDone", "onDone: $utteranceId")
                    nextRead()
                } catch (e: Exception) {
                    e.printStackTrace()
                    nextRead()
                }
            }

            override fun onError(utteranceId: String?) {
                DLToast.showErrorToast("播放错误")
                nextRead()
            }
        }).setLanguage(Locale.SIMPLIFIED_CHINESE) //设置语言

        initView()
        initListener()
        if (DLSPTool.getString(requireContext(), TTS_FILE_KEY)!!.isNotEmpty()) {
            fileName = DLSPTool.getString(requireContext(), TTS_FILE_KEY)
            index = DLSPTool.getInt(requireContext(), TTS_INDEX_KEY)
            mRootView!!.fileNameTv.text = fileName
            mRootView!!.rateSb.progress = DLSPTool.getInt(requireContext(), TTS_RATE_KEY)
            mRootView!!.pitchSb.progress = DLSPTool.getInt(requireContext(), TTS_PITCH_KEY)
            code = DLSPTool.getString(requireContext(), TTS_CODE_KEY)!!
            if (code == DLFileTool.UTF8code) {
                mRootView!!.utfRb.isChecked = true
            }
            readFile(fileName!!)
        }
        RxBus.getDefault().subscribeSticky(this, object : RxBus.Callback<SelectFileMsg>() {
            override fun onEvent(t: SelectFileMsg?) {
                if (fileName != t!!.fileName!!) {
                    fileName = t!!.fileName!!
                    DLSPTool.putString(requireContext(), TTS_FILE_KEY, fileName)
                    mRootView!!.fileNameTv.text = fileName
                    index = 0
                    readFile(fileName!!)
                }
            }
        })

    }

    private fun initView() {
        mRootView!!.pitchTv.text = "音色：1.0"
        mRootView!!.rateTv.text = "音速：1.0"
        mRootView!!.pitchSb.progress = 10
        mRootView!!.rateSb.progress = 10
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initListener() {
        mRootView!!.startReadBtn.setOnClickListener {
            if (readFile2List.size > index) {
                dlttsTool!!.setText(readFile2List[index]).speek()
            }
        }
        mRootView!!.stopReadBtn.setOnClickListener {
            dlttsTool!!.stopTTS()
        }
        mRootView!!.rateSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratePro = progress
                dlttsTool!!.rate = progress / 10f
                mRootView!!.rateTv.text = "音速：${progress / 10f}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        mRootView!!.pitchSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pitchPro = progress
                dlttsTool!!.setPitch(progress / 10f)
                mRootView!!.pitchTv.text = "音色：${progress / 10f}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        mRootView!!.changeCodeBtn.setOnClickListener {
            val messageDialogBuilder = QMUIDialog.MessageDialogBuilder(requireContext())
            messageDialogBuilder.setMessage("转换之后，文档将会被重新读取")
            messageDialogBuilder.addAction("取消") { dialog, index ->
                dialog.dismiss()
            }
            messageDialogBuilder.addAction("确定") { dialog, i ->
                dlttsTool!!.stop()
                readFile2List.clear()
                index = 0
                readFile(fileName!!)
                dialog.dismiss()
            }
            messageDialogBuilder.create().show()
        }
        mRootView!!.codeRp.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.gbkRb -> {
                    code = DLFileTool.GBKcode
                }
                R.id.utfRb -> {
                    code = DLFileTool.UTF8code
                }
            }
        }

        mRootView!!.selectFileBtn.setOnClickListener {
            LFilePicker()
                .withActivity(requireActivity())
                .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                .withStartPath("/storage/emulated/0/") //指定初始显示路径
//                .withIsGreater(false) //过滤文件大小 小于指定大小的文件
                .withMutilyMode(false)//单选还是多选
//                .withFileFilter(arrayOf(".txt"))
                .withMaxNum(1)//最大文件数
                .withChooseMode(true)//选择文件
//            .withFileSize((500 * 1024).toLong()) //指定文件大小为500K
                .start()
        }

        mRootView!!.maleVoiceBtn.setOnClickListener {
            dlttsTool!!.setMaleVoice()
        }
        mRootView!!.femaleVoiceBtn.setOnClickListener {
            dlttsTool!!.setFemaleVoice()
        }

        mRootView!!.listVoiceBtn.setOnClickListener {
            val bottomListSheetBuilder = QMUIBottomSheet.BottomListSheetBuilder(context)
            for (i in dlttsTool!!.mVoices) {
                bottomListSheetBuilder.addItem(i.name)
            }
            bottomListSheetBuilder.setOnSheetItemClickListener { dialog, itemView, position, tag ->
                dlttsTool!!.setVoice(dlttsTool!!.mVoices[position])
                dlttsTool!!.setText("这个声音是这样的！").speek()
            }
            bottomListSheetBuilder.build().show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun nextRead() {
        mRootView!!.textStrTv.text = readFile2List[index]
        dlttsTool!!.setText(readFile2List[index]).speekAdd()
        index++
        mRootView!!.lineNumTv.text = "$index/${readFile2List.size}"
    }

    var readFile2List = ArrayList<String>()
    var index = 0
    var ratePro = 10
    var pitchPro = 10

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun readFile(fileName: String) {
        showLoading()
        val readFile2List1 = DLFileTool.readFile2List(DLFileTool.getFileByPath(fileName), code)
        readFile2List.clear()
        if (readFile2List1 != null) {
            readFile2List.addAll(readFile2List1!! as MutableList<String>)
        }
        if (readFile2List.size > index) {
            mRootView!!.textStrTv.text = readFile2List[index]
            mRootView!!.lineNumTv.text = "$index/${readFile2List.size}"
        }
        hideLoading()
    }

    val TTS_INDEX_KEY = "ttsIndex"
    val TTS_FILE_KEY = "ttsFileName"
    val TTS_CODE_KEY = "ttsFileCode"
    val TTS_RATE_KEY = "ttsRate"
    val TTS_PITCH_KEY = "ttsPitch"
    override fun onDestroy() {
        super.onDestroy()
        dlttsTool!!.stop()
        DLSPTool.putInt(requireContext(), TTS_INDEX_KEY, index)
        DLSPTool.putString(requireContext(), TTS_CODE_KEY, code)
        DLSPTool.putInt(requireContext(), TTS_RATE_KEY, ratePro)
        DLSPTool.putInt(requireContext(), TTS_PITCH_KEY, pitchPro)
    }

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }
}