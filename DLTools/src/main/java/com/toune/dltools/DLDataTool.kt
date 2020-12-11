package com.toune.dltools

import com.toune.dltools.DLConstTool.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * @Author Dong Lei
 * @Date 2020/12/11 0011-上午 9:33
 * @Info 描述：
 */
object DLDataTool {
    /**
     * 字节数转合适大小
     *
     * 保留3位小数
     *
     * @param byteNum 字节数
     * @return 1...1024 unit
     */
    fun byte2FitSize(byteNum: Long): String? {
        return if (byteNum < 0) {
            "shouldn't be less than zero!"
        } else if (byteNum < KB) {
            String.format(Locale.getDefault(), "%.3fB", byteNum.toDouble())
        } else if (byteNum < MB) {
            java.lang.String.format(Locale.getDefault(), "%.3fKB", byteNum.toDouble() / KB)
        } else if (byteNum < GB) {
            java.lang.String.format(Locale.getDefault(), "%.3fMB", byteNum.toDouble() / MB)
        } else {
            java.lang.String.format(Locale.getDefault(), "%.3fGB", byteNum.toDouble() / GB)
        }
    }

    /**
     * inputStream转byteArr
     *
     * @param is 输入流
     * @return 字节数组
     */
    fun inputStream2Bytes(`is`: InputStream?): ByteArray? {
        return input2OutputStream(`is`)!!.toByteArray()
    }

    /**
     * inputStream转outputStream
     *
     * @param is 输入流
     * @return outputStream子类
     */
    fun input2OutputStream(`is`: InputStream?): ByteArrayOutputStream? {
        return if (`is` == null) {
            null
        } else try {
            val os = ByteArrayOutputStream()
            val b = ByteArray(KB)
            var len: Int
            while (`is`.read(b, 0, KB).also { len = it } != -1) {
                os.write(b, 0, len)
            }
            os
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            DLFileTool.closeIO(`is`)
        }
    }

}