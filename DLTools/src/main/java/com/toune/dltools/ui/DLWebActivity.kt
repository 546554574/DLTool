package com.toune.dltools.ui

import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
import com.toune.dltools.R
import kotlinx.android.synthetic.main.activity_web.*

class DLWebActivity : AppCompatActivity() {
    private var url: String? = null
    private var titleStr = ""
    private var mAgentWeb: AgentWeb? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        var extras = intent.extras
        titleStr = extras!!.getString("title")!!
        titleTv.text = titleStr
        url = extras!!.getString("url")
        setWeb()
    }

    private fun setWeb() {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent((webLv as LinearLayout?)!!, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .addJavascriptInterface("app", MyJs())
            .setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    val title = view.title
                    if (!TextUtils.isEmpty(title) && title.length < 20) {
                        // 设置标题
                        titleTv.text = title
                    }
                }
            })
            .createAgentWeb()
            .ready()
            .go(url)
    }

    class MyJs {

    }
}