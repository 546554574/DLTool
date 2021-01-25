package com.toune.permission

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.DragAndDropPermissions
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.toune.dltools.DLActivityTool
import com.toune.dltools.DLBaseDialog
import com.toune.dltools.DLSPTool
import com.toune.dltools.R
import com.toune.dltools.ui.DLWebActivity


class DLPermissionDialog : DLBaseDialog {

    var contentSpan: SpannableStringBuilder? = null
    var mContext: Context? = null
    var permissions: Array<out String>? = null

    constructor(
        context: Context,
        contentSpan: SpannableStringBuilder?,
        onPermissionClickListener: OnPermissionClickListener,
        vararg permissions: String
    ) : super(context, R.layout.dialog_permission, false) {
        this.mContext = context
        this.onPermissionClickListener = onPermissionClickListener
        this.contentSpan = contentSpan
        this.permissions = permissions
        initContentSpan()
    }

    var contentTv: TextView? = null
    private fun initContentSpan() {
        contentTv = dialogView!!.findViewById<TextView>(R.id.contentTv)
        if (contentSpan == null) {
            var firstStr = "1.您将使用矿山易购APP，使用矿山易购需要建立网络数据连接，产生的流量费请咨询当地运营商，点击“同意”接受"
            var userStr = "《用户协议》"
            var andStr = "和"
            var yinsiStr = "《隐私政策》"
            var endStr = "，浏览以上协议政策需要联网\n" +
                    "2.为了保证您的正常使用，在使用过程中，矿山易购需要获取以下权限；访问网络，获取储存空间、设备信息、获取位置等权限。\n" +
                    "这些权限矿山易购APP并不会默认开启，只有用户同意并授权后才会生效；未经授权我们不会收集、处理或泄露您的个人信息"
            var firstSpan = SpannableStringBuilder(firstStr)
            var userSpan = SpannableStringBuilder(userStr)
            var andSpan = SpannableStringBuilder(andStr)
            var yinsiSpan = SpannableStringBuilder(yinsiStr)
            var endSpan = SpannableStringBuilder(endStr)
            val colorSpan = ForegroundColorSpan(Color.parseColor("#108EE9"))
            userSpan.setSpan(colorSpan, 0, userSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val userClickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    var bundle = Bundle()
                    bundle.putString("title", userStr)
                    bundle.putString("url", "http://h5.easybuy.zydl-tec.cn/#/license/index")
                    DLActivityTool.skipActivity(context, DLWebActivity::class.java, bundle)
                }
            }
            userSpan.setSpan(
                userClickableSpan,
                0,
                userSpan.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            yinsiSpan.setSpan(colorSpan, 0, yinsiSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    var bundle = Bundle()
                    bundle.putString("title", yinsiStr)
                    bundle.putString("url", "http://h5.easybuy.zydl-tec.cn/#/license/privacy")
                    DLActivityTool.skipActivity(context, DLWebActivity::class.java, bundle)
                }
            }
            yinsiSpan.setSpan(
                clickableSpan,
                0,
                yinsiSpan.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            firstSpan.append(userSpan).append(andSpan).append(yinsiSpan).append(endSpan)
            contentSpan = firstSpan
        }
        contentTv!!.text = contentSpan
        contentTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun initEvent() {
        var refundLv = dialogView!!.findViewById<LinearLayout>(R.id.refundLv)
        var sureLv = dialogView!!.findViewById<LinearLayout>(R.id.sureLv)
        refundLv.setOnClickListener {
            if (onPermissionClickListener != null) {
                onPermissionClickListener!!.cancelClickListener()
            }
            dismiss()
            DLActivityTool.AppExit(context)
        }
        sureLv.setOnClickListener {
            DLSPTool.putBoolean(context, DLPermissionUtil.APP_FIRST, true)
            if (onPermissionClickListener != null) {
                onPermissionClickListener!!.sureClickListener()
            }
            if (!permissions.isNullOrEmpty()) {
                DLPermissionUtil.with(mContext as Activity).showPermissionDialog(*permissions!!)
            }
            dismiss()
        }

    }

    var onPermissionClickListener: OnPermissionClickListener? = null

    interface OnPermissionClickListener {
        fun sureClickListener()
        fun cancelClickListener()
    }

    /**
     * 关闭返回键
     */
    override fun onBackPressed() {
    }
}