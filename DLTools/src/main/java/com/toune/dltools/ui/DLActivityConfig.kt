package com.toune.dltools.ui

import android.graphics.Color
import com.toune.dltools.R

/**
 * @Author Dong Lei
 * @Date 2020/12/25 0025-上午 8:32
 * @Info 描述：
 */
object DLActivityConfig {

    var loadingXml = R.layout.dialog_loading
    var loadingColor = Color.RED

    class Builder {
        fun setLoadingXml(layoutId: Int): Builder {
            loadingXml = layoutId
            return this@Builder
        }

        fun setLoadingColor(color: Int): Builder {
            loadingColor = color
            return this
        }
    }
}