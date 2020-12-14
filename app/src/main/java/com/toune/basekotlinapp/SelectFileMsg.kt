package com.toune.basekotlinapp

/**
 * @Author Dong Lei
 * @Date 2020/12/11 0011-下午 14:53
 * @Info 描述：
 */
class SelectFileMsg {
    var fileName: String? = null

    constructor(fileName: String) {
        this.fileName = fileName
    }
}