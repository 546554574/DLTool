package com.toune.dltools

import java.lang.ref.Reference
import java.lang.ref.WeakReference

open class DLBasePresenterImpl<V> {
    var mViewRef: Reference<V?>? = null
    var view: V? = null
    var isViewAttached = mViewRef != null
    fun attachView(v: V) {
        mViewRef = WeakReference<V?>(v)
        view = mViewRef!!.get()
    }

    fun detachView() {
        if (mViewRef != null) {
            mViewRef!!.clear()
            mViewRef = null
        }
    }
}