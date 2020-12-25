package com.toune.basekotlinapp.dialog

import android.content.Context
import com.toune.basekotlinapp.R
import com.toune.dltools.DLBaseDialog
import com.toune.dltools.DLToast
import com.toune.dltools.view.DLVerifyCodeInputView
import kotlinx.android.synthetic.main.dialog_phone_verify_code.view.*

/**
 * @Author Dong Lei
 * @Date 2020/12/18 0018-下午 16:35
 * @Info 描述：
 */
class InputPhoneVerifyCodeDialog(context: Context?) : DLBaseDialog(context,R.layout.dialog_phone_verify_code) {
    override fun initEvent() {
        dialogView!!.closeIv.setOnClickListener {
            dismiss()
        }

        dialogView!!.inputView.finishListener = object : DLVerifyCodeInputView.FinishListener {
            override fun finish(code: String) {
                DLToast.showErrorToast(code)
            }
        }

        dialogView!!.sureBtn.setOnClickListener {
            //确定按钮
        }
    }

}