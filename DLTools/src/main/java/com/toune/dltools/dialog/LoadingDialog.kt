package com.toune.dltools.dialog

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.github.ybq.android.spinkit.style.Wave
import com.toune.dltools.DLBaseDialog
import com.toune.dltools.R
import com.toune.dltools.ui.DLActivityConfig


/**
 * @Author Dong Lei
 * @Date 2020/12/24 0024-下午 13:41
 * @Info 描述：
 */
class LoadingDialog(context: Context?, color: Int, layoutId: Int) :
    DLBaseDialog(context, layoutId) {
    var color: Int? = color

    init {
        if (layoutId==R.layout.dialog_loading) {
            this.color = color
            val progressBar: ProgressBar =
                dialogView!!.findViewById<View>(R.id.pb_loading) as ProgressBar
            val doubleBounce: Sprite = Wave()
            if (color != null) {
                doubleBounce.color = color!!
            }
            progressBar!!.indeterminateDrawable = doubleBounce
        }
    }

    override fun initEvent() {
    }

}