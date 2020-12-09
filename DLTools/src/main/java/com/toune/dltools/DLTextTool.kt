package com.toune.dltools

import android.graphics.Typeface
import java.io.File

/**
 * @Author Dong Lei
 * @Date 2020/12/8 0008-下午 14:49
 * @Info 描述：
 */
object DLTextTool {

    fun getLongCangTypeFace():Typeface{
        var fontPath = "fonts${File.separator.toString()}LongCang-Regular.ttf"
        val manager = DLTool.context!!.assets
        return Typeface.createFromAsset(manager, fontPath)
    }

    fun getZhiMangXingTypeFace():Typeface{
        var fontPath = "fonts${File.separator.toString()}ZhiMangXing-Regular.ttf"
        val manager = DLTool.context!!.assets
        return Typeface.createFromAsset(manager, fontPath)
    }
}