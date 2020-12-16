package com.toune.dltools

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author Dong Lei
 * @Date 2020/12/10 0010-下午 15:40
 * @Info 描述：
 */
class DLTTSTool {
    private var context: Context? = null
    private lateinit var tts: TextToSpeech

    companion object {
        var dlttsTool: DLTTSTool? = null

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun with(
            context: Context,
            init: Boolean,
            @Nullable initListener: UtteranceProgressListener?
        ): DLTTSTool {
            if (init) {
                dlttsTool = DLTTSTool(context, initListener)
            } else {
                if (dlttsTool == null) {
                    dlttsTool = DLTTSTool(context, initListener)
                }
            }
            return dlttsTool!!
        }
    }

    var initListener: UtteranceProgressListener? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, @Nullable initListener: UtteranceProgressListener?) {
        this.context = context
        this.initListener = initListener
        tts = TextToSpeech(context) {
            init()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun init() {
        if (locale == null) {
            locale = Locale.CHINESE
        }
        val supported = tts.setLanguage(locale)
        if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
            DLToast.showErrorToast("不支持当前语言！")
//            throw unSupportedLanguageException()
        }

        for (c in tts.voices) {
            if (maleVoiceName == c.name) {
                maleVoice = c
            }
            mVoices.add(c)
        }
        femaleVoice = tts.defaultVoice
    }

    var mVoices: MutableList<Voice> = ArrayList()
    val maleVoiceName = "cmn-cn-x-ccd-local"
    var maleVoice: Voice? = null
    var femaleVoice: Voice? = null

    /**
     * 设置男声
     * @return DLTTSTool
     */
    fun setMaleVoice(): DLTTSTool {
        if (maleVoice != null) {
            setVoice(maleVoice!!)
        } else {
            DLToast.showWarningToast("没有找到男声")
        }
        return this
    }

    /**
     * 设置女声
     * @return DLTTSTool
     */
    fun setFemaleVoice(): DLTTSTool {
        setVoice(femaleVoice!!)
        return this
    }

    private var locale: Locale? = null
    fun setLanguage(locale: Locale): DLTTSTool {
        this.locale = locale
        return this
    }

    private var textStr = "请设置文本内容"
    fun setText(str: String): DLTTSTool {
        textStr = str
        return this
    }

    private var voice: Voice? = null

    fun setVoice(voice: Voice): DLTTSTool {
        this.voice = voice
        return this
    }

    private var pitch: Float = -1f
    fun setPitch(float: Float): DLTTSTool {
        this.pitch = float
        return this
    }

    fun getTTS(): TextToSpeech {
        return tts;
    }

    var rate = -1f
    fun setSpeechRate(rate: Float) {
        this.rate = rate
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speek(): DLTTSTool {
        if (voice != null) {
            tts.voice = voice
        }
        if (rate > 0) {
            tts.setSpeechRate(rate)
        }
        if (pitch > 0) {
            tts.setPitch(pitch)
        }
        tts.setOnUtteranceProgressListener(initListener)
        tts.speak(textStr, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        return this
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speekAdd(): DLTTSTool {
        if (voice != null) {
            tts.voice = voice
            Log.e("voice", "voice: ${voice!!.name}")
        } else {
            Log.e("voice", "voice: ${tts.voice.name}")
        }
        if (rate > 0) {
            tts.setSpeechRate(rate)
        }
        if (pitch > 0) {
            tts.setPitch(pitch)
        }
        tts.speak(textStr, TextToSpeech.QUEUE_ADD, null, System.currentTimeMillis().toString())
        return this
    }

    fun stopTTS() {
        tts.stop()
    }

    fun stop() {
        tts.stop()
        dlttsTool = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun toFile(filePath: String, fileName: String) {
        var path = "$filePath dltool/$fileName.mp3"
        if (!DLFileTool.isFileExists(path)) {
            val createFileByDeleteOldFile = DLFileTool.createFileByDeleteOldFile(path)
            if (createFileByDeleteOldFile) {
                tts.synthesizeToFile(
                    textStr,
                    null,
                    DLFileTool.getFileByPath(path),
                    System.currentTimeMillis().toString()
                );
            } else {
                DLToast.showWarningToast("文件创建失败")
            }
        } else {
            val synthesizeToFile = tts.synthesizeToFile(
                textStr,
                null,
                DLFileTool.getFileByPath(path),
                System.currentTimeMillis().toString()
            )
            when (synthesizeToFile) {
                /*
                    Error	-1
                    Stopped	-2
                    Success	0
                 */
                -1 -> {
                    DLToast.showErrorToast("转换失败")
                }
                -2 -> {
                    DLToast.showInfoToast("停止转换")
                }
                0 -> {
                    DLToast.showSuccessToast("成功转换")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun toFile(fileName: String) {
        toFile(DLFileTool.getSDCardPath()!!, fileName)
    }

    class unSupportedLanguageException(message: String?) : Exception(message) {

    }
}