package com.toune.dltools

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.toune.dltools.ui.DLActivityConfig
import kotlinx.android.synthetic.main.activity_base.*
import java.io.File
import java.lang.reflect.Field

/**
 * @Author Dong Lei
 * @Date 2020/12/4 0004-下午 14:02
 * @Info 描述：
 */
object DLTool {
    private lateinit var cxt: Application

    /**
     * 初始化
     * @param cxt Application
     * @return DLTool
     */
    fun init(cxt: Application) :DLTool{
        this.cxt = cxt
        context = cxt.applicationContext
        QMUISwipeBackActivityManager.init(cxt)
        return this
    }

    /**
     * 设置DLBaseActivity属性参数
     * @param config Builder
     */
    fun setActivityConfig(config:DLActivityConfig.Builder):DLTool{
        return this
    }

    /**
     * 设置自定义字体
     * @param fontPath String 字体路径
     * @return DLTool
     */
    fun setCustomFont(fontPath:String):DLTool{
        replaceSystemDefaultFont(cxt, fontPath);
        return this
    }

    /**
     * 内置龙仓字体
     * @return DLTool
     */
    fun setLongCangFont():DLTool{
        var fontPath = "fonts${File.separator.toString()}LongCang-Regular.ttf"
        replaceSystemDefaultFont(cxt, fontPath);
        return this
    }

    /**
     * 内置之芒星字体
     * @return DLTool
     */
    fun setZhiMangXingFont():DLTool{
        var fontPath = "fonts${File.separator.toString()}ZhiMangXing-Regular.ttf"
        replaceSystemDefaultFont(cxt, fontPath);
        return this
    }


    private fun replaceSystemDefaultFont(cxt: Application, fontPath: String) {
        val manager = cxt.assets
        val font = Typeface.createFromAsset(manager, fontPath)
        //這里我们修改的是MoNOSPACE,是因为我们在主题里给app设置的默认字体就是monospace，设置其他的也可以
        replaceTypefaceField("MONOSPACE", font)
    }

    private fun replaceTypefaceField(fieldName: String, font: Typeface?) {
        try {
            val defaultField: Field = Typeface::class.java.getDeclaredField(fieldName)
            defaultField.isAccessible = true
            defaultField.set(null, font)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    var context: Context? = null
        get() {
            if (field == null) {
                throw NullPointerException("请先调用init()方法")
            }
            return field
        }
        set(value) {
            field = value
        }

}