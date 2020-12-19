package com.toune.dltools.http

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class IDLHttpCallBack<T> {
    var mType: Type? = null
    fun start() {}
    abstract fun success(t: T)
    abstract fun error(err: String?)
    fun end() {}

    init {
        //Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
        val superclass = javaClass.genericSuperclass
        mType = if (superclass is Class<*>) {
            null
        } else {
            //ParameterizedType参数化类型，即泛型
            val parameterized = superclass as ParameterizedType?
            //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
            //将Java 中的Type实现,转化为自己内部的数据实现,得到gson解析需要的泛型
            `$Gson$Types`.canonicalize(parameterized!!.actualTypeArguments[0])
        }
    }


    public inner class Builder {

        fun startH() {
            handler.sendEmptyMessage(START)
        }

        fun errorH(err: String?) {
            val message = Message()
            message.obj = err
            message.what = ERROR
            handler.sendMessage(message)
        }

        fun successH(data: T?) {
            var message = Message()
            message.obj = data
            message.what = SUCCESS
            handler.sendMessage(message)
        }

        fun endH() {
            handler.sendEmptyMessage(END)
        }

        @SuppressLint("HandlerLeak")
        var handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    START -> start()
                    SUCCESS -> success(msg.obj as T)
                    ERROR -> {
                        if (msg.obj != null) {
                            error(msg.obj.toString())
                        }
                        end()
                    }
                    TOKEN_ERROR -> {
                        //token出错
                    }
                    END -> end()
                }
            }
        }
        val START = 10001 //请求开始
        val SUCCESS = 10002 //请求成功
        val ERROR = 10003   //请求出错
        val TOKEN_ERROR = 10006 //token出错
        val END = 10004 //请求结束
    }
}