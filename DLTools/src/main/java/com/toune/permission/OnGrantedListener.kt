package com.toune.permission

/**
 * @Author Dong Lei
 * @Date 2020/12/5 0005-上午 10:07
 * @Info 描述：同意和拒绝的权限
 */
interface OnGrantedListener {
    fun grantedListener(grantedList:List<String>)
    fun deniedListener(deniedList:List<String>)
}