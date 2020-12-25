package com.toune.dltools

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import kotlin.properties.Delegates

abstract class DLBaseDialog : QMUIDialog {

    private var layout by Delegates.notNull<Int>()

    constructor(context: Context?, layoutId: Int) : super(context) {
        this.layout = layoutId
        initView()
    }

    constructor(context: Context?, layoutId: Int, themeResId: Int) : super(context, themeResId) {
        this.layout = layoutId
        initView()
    }

    protected constructor(
        context: Context?,
        layoutId: Int,
        cancelable: Boolean,
    ) : super(context) {
        this.layout = layoutId
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
}