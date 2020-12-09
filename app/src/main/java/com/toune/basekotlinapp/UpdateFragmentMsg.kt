package com.toune.basekotlinapp

import androidx.fragment.app.Fragment
import com.qmuiteam.qmui.arch.QMUIFragment

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-下午 14:12
 * @Info 描述：
 */
class UpdateFragmentMsg {
    var fragment: Fragment? = null
    constructor(fragment: Fragment){
        this.fragment = fragment
    }
}