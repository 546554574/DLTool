package com.toune.dltools.view.title

import android.content.Context

/**
 * @Author Dong Lei
 * @Date 2020/12/28 0028-下午 13:13
 * @Info 描述：
 */
class DLPostCustomTitleMsg {
    lateinit var context: Context
    lateinit var titleString: String

    constructor(context: Context, titleString: String?) {
        this.context = context
        this.titleString = titleString!!
    }
}