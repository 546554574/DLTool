package com.toune.basekotlinapp.fragment

import com.toune.basekotlinapp.R
import com.toune.dltools.DLAppTool
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView
import com.toune.dltools.DLToast
import com.toune.dltools.http.DLHttp
import com.toune.dltools.http.IDLHttpCallBack
import com.toune.dltools.http.IDLHttpFileCallBack
import com.toune.dltools.ui.DLBaseFragment
import kotlinx.android.synthetic.main.fragment_http.view.*
import java.io.File

/**
 * @Author Dong Lei
 * @Date 2020/12/16 0016-下午 15:54
 * @Info 描述：
 */
class HttpFragment:DLBaseFragment<DLBaseView,DLBasePresenterImpl<DLBaseView>>() {
    override val layout: Int
        get() = R.layout.fragment_http

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }

    override fun lazyInit() {
        mRootView!!.httpBtn.setOnClickListener {
            DLHttp.get("http://www.baidu.com").build(object :IDLHttpCallBack<String>(){
                override fun success(t: String) {
                    mRootView!!.textTv.text = t
                }

                override fun error(err: String?) {
                    DLToast.showErrorToast(err!!)
                }
            })
        }

        val downUrl = "https://imtt.dd.qq.com/16891/apk/D0C7FDD4BAA4AB19B376AF2E6A9BDBED.apk"
        mRootView.downFileBtn.setOnClickListener {
            DLHttp.downFile(downUrl,"")
                .down(object :IDLHttpFileCallBack(){
                    override fun start(toatleSize: Int) {
                        mRootView.pb.max = toatleSize
                    }

                    override fun progress(size: Int) {
                        mRootView.pb.progress = size
                    }

                    override fun success(file: File?) {
                        DLAppTool.InstallAPK(requireContext(),file!!)
                    }

                    override fun error(err: String?) {
                        DLToast.showErrorToast(err!!)
                    }

                })
        }
    }
}