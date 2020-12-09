package com.toune.dltools

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import com.qmuiteam.qmui.widget.dialog.QMUIDialog

abstract class DLBaseDialog : QMUIDialog {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, themeResId: Int) : super(context, themeResId) {
        initView()
    }

    protected constructor(
        context: Context?,
        cancelable: Boolean,
    ) : super(context) {
        setCanceledOnTouchOutside(cancelable)
        initView()
    }

    var dialogView: View? = null
    private fun initView() {
        dialogView = LayoutInflater.from(context).inflate(layout, null)
        initEvent()
        setContentView(dialogView!!)
    }

    protected abstract fun initEvent()
    protected abstract val layout: Int
}