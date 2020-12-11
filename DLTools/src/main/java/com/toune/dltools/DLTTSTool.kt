package com.toune.dltools

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.util.*

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
    var initListener: UtteranceProgressListener?=null
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
        if (locale==null){
            locale = Locale.CHINESE
        }
        val supported = tts.setLanguage(locale)
        if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
            throw unSupportedLanguageException("不支持当前语言！")
        }
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

    private var pitch: Float = 1.0f
    fun setPitch(float: Float): DLTTSTool {
        this.pitch = float
        return this
    }

    fun getTTS(): TextToSpeech {
        return tts;
    }

    var rate = -1f
    fun setSpeechRate(rate:Float){
        this.rate = rate
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speek(): DLTTSTool {
        if (voice!=null) {
            tts.voice = voice
        }
        if (rate>0){
            tts.setSpeechRate(rate)
        }
        tts.setPitch(pitch)
        tts.setSpeechRate(rate)
        tts.setOnUtteranceProgressListener(initListener)
        tts.speak(textStr, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        return this
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speekAdd(): DLTTSTool {
        tts.speak(textStr, TextToSpeech.QUEUE_ADD, null, System.currentTimeMillis().toString())
        return this
    }

    fun stop() {
        tts.stop()
    }

    class unSupportedLanguageException(message: String?) : Exception(message) {

    }
}