package com.toune.dltools.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qmuiteam.qmui.arch.QMUIFragment
import com.toune.dltools.DLActivityTool
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

    // 实例化presenter
    abstract fun initPresenter(): T

    //懒加载方法
    abstract fun lazyInit()

    override fun showLoading() {
        (activity as DLBaseActivity<*,*>).showLoading()
    }

    override fun hideLoading() {
        (activity as DLBaseActivity<*,*>).hideLoading()
    }

    /**
     * 本Fragment进行的跳转
     * @param clazz Class<Any>
     */
    fun startToActivity(clazz :Class<Any>){
        DLActivityTool.skipActivity(requireContext(),clazz)
    }

    /**
     * 本Fragment进行的带参数跳转
     * @param clazz Class<Any>
     * @param bundle Bundle
     */
    fun startToActivity(clazz :Class<Any>,bundle: Bundle){
        DLActivityTool.skipActivity(requireContext(),clazz,bundle)
    }
}