package com.toune.dltools.ui

import android.R.attr.path
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.toune.dltools.*
import com.toune.permission.DLPermissionUtil
import kotlinx.android.synthetic.main.activity_base.*
import java.io.File


open abstract class DLBaseActivity<V, T : DLBasePresenterImpl<V>?> : AppCompatActivity(), DLBaseView {
    val isSignOut = false//判断是不是双击返回按钮退出APP的页面
    lateinit var context: Context
    var mPresenter: T? = null
    var mRootView: View? = null
    private var exitTime: Long = 0

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        DLActivityTool.addActivity(this)
        val view = View.inflate(context, R.layout.activity_base, null)
        setStatusBar()
        setContentView(view)
        setPerUtil()
        titleRv.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(this), 0, 0)
        mRootView = View.inflate(context, layout, null)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        mRootRv.showContent(mRootView, layoutParams)
        //initPresenter()是抽象方法，让view初始化自己的presenter
        mPresenter = initPresenter()
        //presenter和view的绑定
        if (mPresenter != null) {
            mPresenter!!.attachView(this as V)
        }
        //标题  ：布局文件中引入lyout_title
        if (titleStr.isNullOrEmpty()) {
            titleRv.visibility = View.GONE
        } else {
            backLv.setOnClickListener(View.OnClickListener { onBackIv() })
            setToolBar(titleStr)
        }
        //一些初始化操作
        init(savedInstanceState)
        //一些业务逻辑
        initEventAndData()
    }

    /**
     * 设置权限
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPerUtil() {
        DLPermissionUtil.with(this).build()
    }

    fun addRightView(view: View){
        rightLv.addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
    /**
     * 设置沉浸式
     */
    fun setStatusBar() {
        QMUIStatusBarHelper.translucent(this)
        if (isLightColor(Color.WHITE)) {
            QMUIStatusBarHelper.setStatusBarDarkMode(this)
        } else {
            QMUIStatusBarHelper.setStatusBarLightMode(this)
        }
    }

    /**
     * 判断颜色是不是亮色
     *
     * @param color
     * @return
     * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }
    abstract val layout: Int
    abstract val titleStr: String?
    // 实例化presenter
    abstract fun initPresenter(): T
    /**
     * 返回按钮，可不实现
     */
    fun onBackIv() {
        DLActivityTool.finishActivity(this)
    }

    override fun onDestroy() {
        if (mPresenter != null) {
            mPresenter!!.detachView()
        }
        super.onDestroy()
    }

    fun setToolBar(title: String?) {
        titleTv.text = title
    }

    abstract fun init(savedInstanceState: Bundle?)
    abstract fun initEventAndData()

    //如果有自定义需求就用这个方法//如果有自定义需求就用这个方法
    lateinit var loadingDialog: QMUITipDialog

    override fun showLoading() {
        if (this::loadingDialog.isInitialized) {
            loadingDialog.show()
        } else {
            loadingDialog = QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .create(false)
            loadingDialog.setCanceledOnTouchOutside(true)
            loadingDialog.show()
        }
    }

   override fun hideLoading() {
        if (this::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.hide()
        }
    }

    override fun onBackPressed() {
        if (isSignOut) {
            //连续按2次返回键退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                DLToast.showToast("再按一次退出")
                exitTime = System.currentTimeMillis()
            } else {
                DLActivityTool.finishAllActivity()
            }
        } else {
            onBackIv()
        }
    }
}