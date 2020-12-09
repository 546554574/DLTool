package com.toune.dltools

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


class DLBaseDialogFragment : DialogFragment() {
    var rootView: View? = null
    var mContext: Context? = null
    fun getRootView(container: ViewGroup?, resId: Int): View? {
        mContext = activity
        rootView = LayoutInflater.from(mContext).inflate(resId, container, false)
        return rootView
    }

    fun <T : View?> obtainView(resId: Int): T? {
        return if (null == rootView) {
            null
        } else rootView!!.findViewById<View>(resId) as T
    }

    /**
     * Display the dialog, adding the fragment to the given FragmentManager.  This
     * is a convenience for explicitly creating a transaction, adding the
     * fragment to it with the given tag, and committing it.  This does
     * *not* add the transaction to the back stack.  When the fragment
     * is dismissed, a new transaction will be executed to remove it from
     * the activity.
     * @param manager The FragmentManager this fragment will be added to.
     * @param tag The tag for this fragment, as per
     * [FragmentTransaction.add].
     */
    override fun show(manager: FragmentManager, tag: String?) {
//        mDismissed = false;
//        mShownByMe = true;
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        // 这里吧原来的commit()方法换成了commitAllowingStateLoss()
        ft.commitAllowingStateLoss()
    }
}