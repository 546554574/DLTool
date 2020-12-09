package com.toune.dltools

import java.lang.StringBuilder

/**
 * @Author Dong Lei
 * @Date 2020/12/8 0008-下午 14:18
 * @Info 描述：颜色相关工具类
 */
object DLColorTool {
    val colorDatas = arrayListOf<String>("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F")

    fun randomColor():String{
        var sb = StringBuilder()
        sb.append("#")
        for (index in 0..5) {
            var random = (0..15).random()
            sb.append(colorDatas[random])
        }
        return sb.toString()
    }
}