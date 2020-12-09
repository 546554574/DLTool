package com.toune.permission

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.toune.dltools.DLSPTool


class DLPermissionUtil {
    companion object {
        val APP_FIRST = "APP_FIRST"
        var isShow = false
        var dlPermissionUtil: DLPermissionUtil? = null
        /**
         * 初始化
         * @param context Activity
         * @return DLPermissionUtil
         */
        @RequiresApi(Build.VERSION_CODES.M)
        fun with(context: Activity): DLPermissionUtil {
            if (dlPermissionUtil == null) {
                dlPermissionUtil = DLPermissionUtil(context)
            }
            return dlPermissionUtil!!
        }
    }

    var context: Activity? = null
    lateinit var mPermissionClickListener: DLPermissionDialog.OnPermissionClickListener
    var canShowDialog: MutableList<String> = ArrayList()  //只有在登录和首页需要用户隐私协议弹框
    var needShowPermission: MutableList<String> = ArrayList()  //需要弹出权限框的页面
    lateinit var permissions: Array<out String>
    lateinit var onGrantedListener: OnGrantedListener
    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Activity) {
        this.context = context
    }
    
    public fun build(){
        when (DLSPTool.getInt(context!!,APP_FIRST)) {
            -1 -> {
                //第一次,弹出同意用户隐私协议框
                if (isShow) {
                    return
                }
                if (checkNeedShowDialog(context!!)) {
                    var dialog = DLPermissionDialog(
                        context!!,
                        object : DLPermissionDialog.OnPermissionClickListener {
                            override fun sureClickListener() {
                                isShow = true
                                getBasePermission()
                                if (mPermissionClickListener != null) {
                                    mPermissionClickListener.sureClickListener()
                                }
                            }

                            override fun cancelClickListener() {
                                if (mPermissionClickListener != null) {
                                    mPermissionClickListener.cancelClickListener()
                                }
                            }
                        })
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }
            else -> {
                getBasePermission()
            }
        }
    }

    public fun checkNeedShowPer(context: Activity): Boolean {
        for (cla in needShowPermission) {
            if (context::class.java.simpleName == cla) {
                return true
            }
        }
        return false
    }

    public fun checkNeedShowDialog(context: Activity): Boolean {
        for (cla in canShowDialog) {
            if (context::class.java.simpleName == cla) {
                return true
            }
        }
        return false
    }

    public fun getBasePermission() {
        if (!checkNeedShowPer(context!!) || XXPermissions.hasPermission(context,permissions)) {
            return
        }
        XXPermissions.with(context)
            // 申请权限
            .permission(permissions)
            .request(object : OnPermission {
                override fun hasPermission(granted: List<String>, all: Boolean) {
                    if (onGrantedListener!=null) {
                        onGrantedListener.grantedListener(granted)
                    }
                }

                override fun noPermission(denied: List<String>, never: Boolean) {
                    if (never) {
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(context, denied)
                    }
                    if (onGrantedListener!=null) {
                        onGrantedListener.deniedListener(denied)
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

    /**
     * 设置那些页面可以展示用户隐私协议确认弹框
     * @param clazz MutableList<Activity>
     */
    fun setCanShowThisDialogActivity(clazz: MutableList<String>): DLPermissionUtil {
        canShowDialog.addAll(clazz)
        return dlPermissionUtil!!
    }

    /**
     * 设置那些页面需要弹出权限请求
     * @param clazz MutableList<Activity>
     */
    fun setNeedShowPermissionActivity(clazz: MutableList<String>): DLPermissionUtil {
        needShowPermission.addAll(clazz)
        return dlPermissionUtil!!
    }

    /**
     * 设置需要的权限
     * @param pers Array<out String>
     * @return DLPermissionUtil
     */
    fun setPermissionList(vararg pers: String): DLPermissionUtil {
        permissions = pers
        return dlPermissionUtil!!
    }
}