package com.toune.dltools.ui

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.toune.dltools.R
import com.toune.dltools.view.title.DLCustomTitleView

/**
 * @Author Dong Lei
 * @Date 2020/12/25 0025-上午 8:32
 * @Info 描述：
 */
object DLActivityConfig {

    var context: Context? = null
    var customTitleView: DLCustomTitleView? = null
    var useDefaultTitleView: Boolean = true
    var defTitleBgColor: Int = Color.WHITE
    var defTitleTextColor: Int = Color.parseColor("#333333")
    var defTitleTextSize: Float = 20f
    var defTitleBackIvImgRes: Int? = null
    var defTitleGravity: Int = Gravity.CENTER

    var isStatusBar: Boolean = true
    var loadingXml = R.layout.dialog_loading
    var loadingColor = Color.RED

    class Builder {
        /**
         * 设置否沉浸式
         * @param boolean Boolean
         * @return Builder
         */
        fun setStatusBar(boolean: Boolean): Builder {
            isStatusBar = boolean
            return this
        }

        /**
         * 是否使用默认布局
         * @param boolean Boolean
         * @return Builder
         */
        fun useDefaultTitleView(boolean: Boolean): Builder {
            useDefaultTitleView = boolean
            return this
        }

        /**
         * 设置标题的背景色
         * @param color Int
         */
        fun setDefaultTitleBgColor(color: Int): Builder {
            defTitleBgColor = color
            return this
        }

        /**
         * 设置标题字体颜色
         * @param color Int
         */
        fun setDefaultTextColor(color: Int): Builder {
            defTitleTextColor = color
            return this
        }

        /**
         * 设置标题字体大小 单位SP
         * @param size Float
         */
        fun setDefaultTextSize(size: Float): Builder {
            defTitleTextSize = size
            return this
        }

        /**
         * 设置返回的图标
         * @param res Int
         */
        fun setDefaultBackIvImgRes(res: Int): Builder {
            defTitleBackIvImgRes = res
            return this
        }

        /**
         * 设置标题在左侧或者右侧或者居中
         * @param gravity Int
         */
        fun setDefaultTextGravity(gravity: Int): Builder {
            defTitleGravity = gravity
            return this
        }

        /**
         * 设置baseActivity的title样式
         * 需要继承RelativeLayout
         * @param xml Int
         * @return Builder
         */
        fun setCustomTitleView(view: DLCustomTitleView?): Builder {
            useDefaultTitleView = false
            customTitleView = view
            return this
        }

        /**
         * 设置loading的布局
         * @param layoutId Int
         * @return Builder
         */
        fun setLoadingXml(layoutId: Int): Builder {
            loadingXml = layoutId
            return this@Builder
        }

        /**
         * 设置loading样式的颜色，如果设置了样式，则不生效
         * @param color Int
         * @return Builder
         */
        fun setLoadingColor(color: Int): Builder {
            loadingColor = color
            return this
        }
    }
}