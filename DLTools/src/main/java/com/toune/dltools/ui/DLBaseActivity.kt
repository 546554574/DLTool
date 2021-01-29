package com.toune.dltools.ui

import android.app.Dialog
import android.content.Context
import android.content.PeriodicSync
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.DragAndDropPermissions
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.toune.dltools.*
import com.toune.dltools.dialog.LoadingDialog
import com.toune.dltools.view.title.DLBaseTitleView
import com.toune.dltools.view.title.DLCustomTitleView
import com.toune.permission.DLPermissionDialog
import com.toune.permission.DLPermissionUtil
import com.toune.permission.OnGrantedListener
import kotlinx.android.synthetic.main.activity_base.*


open abstract class DLBaseActivity<V, T : DLBasePresenterImpl<V>?> : AppCompatActivity(),
    DLBaseView {

    val MMlayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )

    var MWlayoutParams = LinearLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    val isSignOut = false//判断是不是双击返回按钮退出APP的页面
    lateinit var context: Context
    var mPresenter: T? = null
    var mRootView: View? = null //多布局根View
    var mTitleRootView: DLCustomTitleView? = null //标题的View
    private var exitTime: Long = 0
    private var isFirst = false

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        DLActivityTool.addActivity(this)
        DLActivityConfig.context = this
        val view = View.inflate(context, R.layout.activity_base, null)

        //是否设置沉浸式
        if (DLActivityConfig.isStatusBar) {
            setStatusBar()
        }
        setContentView(view)
        //插入标题控件
        if (titleStr.isNullOrEmpty()) { //标题隐藏
            baseTitleLv.visibility = View.GONE
        } else {  //标题显示
            baseTitleLv.visibility = View.VISIBLE
            baseTitleLv.removeAllViews()
            if (DLActivityConfig.isStatusBar) {
                //是否沉浸式
                baseTitleLv.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(context), 0, 0)
            }
            if (DLActivityConfig.useDefaultTitleView) {
                //使用自带控件
                mTitleRootView = DLBaseTitleView(this, baseTitleLv, titleStr)
            } else {
                mTitleRootView = DLActivityConfig.customTitleView
            }
            mTitleRootView!!.reView(context, baseTitleLv, titleStr!!)
        }
        mRootView = View.inflate(context, layout, null)

        mRootRv.showContent(mRootView, MMlayoutParams)
        //initPresenter()是抽象方法，让view初始化自己的presenter
        mPresenter = initPresenter()
        //presenter和view的绑定
        if (mPresenter != null) {
            mPresenter!!.attachView(this as V)
        }
        //一些初始化操作
        init(savedInstanceState)
        //一些业务逻辑
        initEventAndData()
    }

    /**
     * 显示用户隐私协议弹框
     * 直接使用自己的dialog
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun showUserPrivacyDialog(dialog: Dialog) {
        if (!DLPermissionUtil.isAppFirst(this)) {
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    /**
     * 显示用户隐私协议弹框
     * 用默认的，设置contentSpan就行
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun showUserPrivacyDialog(contentSpan: SpannableStringBuilder?, vararg permissions: String) {
        if (!DLPermissionUtil.isAppFirst(this)) {
            showUserPrivacy(contentSpan, *permissions)
        }
    }

    /**
     * 用户隐私协议弹窗
     */
    private fun showUserPrivacy(contentSpan: SpannableStringBuilder?, vararg permissions: String) {
        var dialog = DLPermissionDialog(
            this,
            contentSpan,
            object : DLPermissionDialog.OnPermissionClickListener {
                override fun sureClickListener() {

                }

                override fun cancelClickListener() {

                }
            },
            *permissions)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    /**
     * 设置这个页面需要请求的权限
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun showPermission(vararg permissions: String) {
        DLPermissionUtil.with(this).showPermissionDialog(*permissions)
    }

    /**
     * 显示用户隐私协议弹框
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun showPermission(onGrantedListener: OnGrantedListener, vararg permissions: String) {
        DLPermissionUtil.with(this).setOnGrantedClickListener(onGrantedListener)
            .showPermissionDialog(*permissions)
    }


    /**
     * 使用默认的title才生效
     * @param view View
     */
    fun addRightView(view: View) {
        if (DLActivityConfig.useDefaultTitleView) {
            (mTitleRootView as DLBaseTitleView).addRightView(view)
        }
    }

    /**
     * 设置沉浸式
     */
    private fun setStatusBar() {
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
        if (mTitleRootView != null) {
            mTitleRootView!!.onBackIv(this)
        } else {
            DLActivityTool.finishAllActivity()
        }
    }

    fun setTitleStr(string: String?) {
        mTitleRootView!!.setTitleStr(string)
    }

    override fun onDestroy() {
        if (mPresenter != null) {
            mPresenter!!.detachView()
        }
        super.onDestroy()
    }

    /**
     * 本Activity进行的跳转
     * @param clazz Class<Any>
     */
    fun startToActivity(clazz :Class<Any>){
        DLActivityTool.skipActivity(this,clazz)
    }

    /**
     * 本Activity进行的带参数跳转
     * @param clazz Class<Any>
     * @param bundle Bundle
     */
    fun startToActivity(clazz :Class<Any>,bundle: Bundle){
        DLActivityTool.skipActivity(this,clazz,bundle)
    }

    abstract fun init(savedInstanceState: Bundle?)
    abstract fun initEventAndData()

    //如果有自定义需求就用这个方法//如果有自定义需求就用这个方法
    lateinit var loadingDialog: LoadingDialog

    override fun showLoading() {
        if (this::loadingDialog.isInitialized) {
            loadingDialog.show()
        } else {
            loadingDialog =
                LoadingDialog(this, DLActivityConfig.loadingColor, DLActivityConfig.loadingXml)
            loadingDialog.setOwnerActivity(this)
            loadingDialog.setCanceledOnTouchOutside(true)
            loadingDialog.show()
        }
    }

    override fun hideLoading() {
        if (this::loadingDialog.isInitialized && !loadingDialog.ownerActivity!!.isDestroyed && loadingDialog.isShowing) {
            loadingDialog.hide()
            loadingDialog.dismiss()
        }
    }

    /**
     * 显示主内容
     */
    fun showContent() {
        mRootRv.showContent()
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