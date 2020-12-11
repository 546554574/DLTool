package com.toune.dltools.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qmuiteam.qmui.arch.QMUIFragment
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-上午 8:58
 * @Info 描述：
 */
abstract class DLBaseFragment<V, T : DLBasePresenterImpl<V>?> : QMUIFragment(), DLBaseView {

    lateinit var mRootView:View
    var mPresenter: T? = null
    abstract val layout: Int
    var isLoaded = false //控制是否执行懒加载
    override fun onCreateView(): View {
        mRootView = View.inflate(context, layout, null)
        mPresenter = initPresenter()
        //presenter和view的绑定
        if (mPresenter != null) {
            mPresenter!!.attachView(this as V)
        }
        return mRootView
    }
    override fun onResume() {
        super.onResume()
        judgeLazyInit()
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        userVisibleHint = !hidden
        judgeLazyInit()
    }

    private fun judgeLazyInit() {
        if (!isLoaded && !isHidden) {
            lazyInit()
            isLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

    //懒加载方法
    abstract fun lazyInit()

    // 实例化presenter
    abstract fun initPresenter(): T

    override fun showLoading() {
        (activity as DLBaseActivity<*,*>).showLoading()
    }

    override fun hideLoading() {
        (activity as DLBaseActivity<*,*>).hideLoading()
    }
}