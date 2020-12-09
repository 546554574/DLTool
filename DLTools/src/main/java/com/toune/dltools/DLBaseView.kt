package com.toune.dltools;

/**
 * Created by wangjitao on 2016/11/8 0008.
 * 一般的Activity中要用到View操作无非是显示加载框、影藏加载框、显示出错信息、显示当数据为空的时候的view之类的
 */
interface DLBaseView {

    //显示loading
    fun showLoading()

    //隐藏loading
    fun hideLoading()
}