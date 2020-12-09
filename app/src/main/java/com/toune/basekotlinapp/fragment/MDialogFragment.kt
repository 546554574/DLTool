package com.toune.basekotlinapp.fragment

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import com.toune.basekotlinapp.R
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView
import com.toune.dltools.DLTextTool
import com.toune.dltools.DLToast
import com.toune.dltools.dialog.DLPlateNumDialog
import com.toune.dltools.dialog.DLSelectPhotoDialog
import com.toune.dltools.ui.DLBaseFragment
import com.toune.dltools.view.DLPathView
import kotlinx.android.synthetic.main.fragment_m_dialog.*


/**
 * @Author Dong Lei
 * @Date 2020/12/8 0008-上午 11:35
 * @Info 描述：
 */
class MDialogFragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {
    companion object {
        fun newInstance(): MDialogFragment {
            val args = Bundle()
            val fragment = MDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val layout: Int
        get() = R.layout.fragment_m_dialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun lazyInit() {
        selectPhotoBtn.setOnClickListener {
            DLSelectPhotoDialog.with(requireContext()).show(DLSelectPhotoDialog.SHOW_ALL_SELECTION)
        }
        selectPlateNumberBtn.setOnClickListener {
            DLPlateNumDialog.with(requireContext())
                .setOnPlateNumDialogClickListener(object :
                    DLPlateNumDialog.OnPlateNumberButtonListener {
                    override fun cancel() {

                    }

                    override fun done(str: StringBuilder?) {
                        DLToast.showSuccessToast(str!!.toString())
                    }
                }).build().show()
        }
        showLoadingBtn.setOnClickListener {
            showLoading()
        }
    }

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }
}