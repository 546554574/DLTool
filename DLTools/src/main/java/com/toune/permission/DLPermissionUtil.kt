package com.toune.permission

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.toune.dltools.DLSPTool
import com.toune.dltools.DLToast


class DLPermissionUtil {
    companion object {
        val APP_FIRST = "APP_FIRST"
        var dlPermissionUtil: DLPermissionUtil? = null
        /**
         * 初始化
         * @param context Activity
         * @return DLPermissionUtil
         */
        fun with(context: Activity): DLPermissionUtil {
            if (dlPermissionUtil == null) {
                dlPermissionUtil = DLPermissionUtil(context)
            }
            return dlPermissionUtil!!
        }

        fun ensureUserPrivacyDialog(context: Context){
            DLSPTool.putBoolean(context,APP_FIRST,true)
        }


        /**
         * 如果没有进行用户隐私协议确认就返回 False
         * 如果进行了确认就返回 True
         * @return Boolean
         */
        fun isAppFirst(context: Context):Boolean{
            return DLSPTool.getBoolean(context,APP_FIRST)
        }


    }
    var context: Activity? = null
    var mPermissionClickListener: DLPermissionDialog.OnPermissionClickListener?=null
    var onGrantedListener: OnGrantedListener?=null

    constructor(context: Activity) {
        this.context = context
    }

    /**
     * 当前页面展示的弹框
     * @param permissions Array<out String>
     */
    fun showPermissionDialog(vararg permissions:String) {
        if (!isAppFirst(context!!)){ //没有进行隐私协议弹窗
            DLToast.showInfoToast("首先请同意隐私弹窗，重写的弹窗请调用\nDLPermissionUtil.ensureUserPrivacyDialog(context)")
            return
        }
        if (XXPermissions.hasPermission(context,permissions)) {
            return
        }
        XXPermissions.with(context)
            // 申请权限
            .permission(permissions)
            .request(object : OnPermission {
                override fun hasPermission(granted: List<String>, all: Boolean) {
                    if (onGrantedListener!=null) {
                        onGrantedListener!!.grantedListener(granted)
                    }
                }

                override fun noPermission(denied: List<String>, never: Boolean) {
                    if (never) {
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(context, denied)
                    }
                    if (onGrantedListener!=null) {
                        onGrantedListener!!.deniedListener(denied)
                    }
                }
            })
    }

    fun setOnPerClicklistener(onPermissionClickListener: DLPermissionDialog.OnPermissionClickListener): DLPermissionUtil {
        mPermissionClickListener = onPermissionClickListener
        return dlPermissionUtil!!
    }

    fun setOnGrantedClickListener(onGrantedListener: OnGrantedListener):DLPermissionUtil{
        this.onGrantedListener = onGrantedListener
        return dlPermissionUtil!!
    }
}