package com.toune.dltools.http

/**
 * @Author Dong Lei
 * @Date 2020/12/16 0016-下午 14:35
 * @Info 描述：
 */
class IBaseCallBack<T> {
    companion object{
        val SUCCESS_CODE = 1
        val ERROR_CODE = 0
    }
    var data: T? = null
    var msg: String? = null
    var code: Int = 0
}