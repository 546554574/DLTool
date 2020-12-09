package com.toune.basekotlinapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.toune.basekotlinapp.R
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView
import com.toune.dltools.DLToast
import com.toune.dltools.ui.DLBaseFragment
import kotlinx.android.synthetic.main.fragment_toast.view.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ToastFragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {
    companion object {
        fun newInstance(): ToastFragment {
            val args = Bundle()
            val fragment = ToastFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val layout: Int
        get() = R.layout.fragment_toast

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }

    override fun lazyInit() {
        DLToast.showWarningToast("我出来了")
        mRootView.normalBtn.setOnClickListener {
            DLToast.showToast("hello DLTool")
        }
        mRootView.infoBtn.setOnClickListener {
            DLToast.showInfoToast("hello DLTool")
        }
        mRootView.errorBtn.setOnClickListener {
            DLToast.showErrorToast("hello DLTool")
        }
        mRootView.successBtn.setOnClickListener {
            DLToast.showSuccessToast("hello DLTool")
        }
        mRootView.warningBtn.setOnClickListener {
            DLToast.showWarningToast("hello DLTool")
        }
        mRootView.customBtn.setOnClickListener {
            DLToast.showCustomToast(
                "hello DLTool",
                resources.getDrawable(R.drawable.logo),
                Toast.LENGTH_SHORT,
                true
            )
        }
    }
}