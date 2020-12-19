package com.toune.dltools.http

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.File

/**
 * 下载文件返回
 */
abstract class IDLHttpFileCallBack {
    abstract fun start(toatleSize: Int)
    abstract fun progress(size: Int)
    abstract fun success(file: File?)
    abstract fun error(err: String?)
    fun end() {}

    inner class Builder{
        fun errorH(err: String?) {
            val message = Message()
            message.obj = err
            message.what = FILE_ERROR
            fileHandler.sendMessage(message)
        }

        fun startH(total: Long) {
            var message = Message()
            message.obj = total.toInt()
            message.what = FILE_START
            fileHandler.sendMessage(message)
        }

        fun progressH(sum: Long) {
            // 下载中更新进度条
            var message = Message()
            message = Message()
            message.obj = sum.toInt()
            message.what = FILE_PROGRESS
            fileHandler.sendMessage(message)
        }

        fun successH(file: File?) {
            // 下载完成
            var message = Message()
            message = Message()
            message.obj = file
            message.what = FILE_SUCCESS
            fileHandler.sendMessage(message)
        }

        var fileHandler: Handler = @SuppressLint("HandlerLeak")
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    FILE_START -> start(msg.obj as Int)
                    FILE_PROGRESS -> progress(msg.obj as Int)
                    FILE_SUCCESS -> success(msg.obj as File)
                    FILE_ERROR -> {
                        if (msg.obj != null) {
                            error(msg.obj.toString())
                        }
                        end()
                    }
                    FILE_END -> end()
                }
            }
        }
        private val FILE_START = 100001
        private val FILE_SUCCESS = 100002
        private val FILE_ERROR = 100003
        private val FILE_END = 100004
        private val FILE_PROGRESS = 100005
    }
}