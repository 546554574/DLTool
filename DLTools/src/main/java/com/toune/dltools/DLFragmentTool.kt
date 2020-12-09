package com.toune.dltools

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.qmuiteam.qmui.arch.QMUIFragment

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-下午 14:16
 * @Info 描述：
 */
object DLFragmentTool {

    fun showFragment(fragmentManager: FragmentManager, id: Int, fragment: Fragment) {
        val beginTransaction = fragmentManager.beginTransaction()
        if (fragment.isAdded) {
            beginTransaction.show(fragment)
        } else {
            beginTransaction.add(id, fragment)
        }
        beginTransaction.commitNow()
    }


    fun hideFragment(fragmentManager: FragmentManager, id: Int, fragment: Fragment) {
        val beginTransaction = fragmentManager.beginTransaction()
        if (fragment.isAdded) {
            beginTransaction.hide(fragment)
        }
        beginTransaction.commitNow()
    }

    fun replaceFragment(fragmentManager: FragmentManager, id: Int, fragment: Fragment) {
        val beginTransaction = fragmentManager.beginTransaction()
        if (fragment.isAdded) {
            beginTransaction.show(fragment)
        } else {
            beginTransaction.replace(id, fragment)
            beginTransaction.show(fragment)
        }
        beginTransaction.commitNow()
    }
}