package com.toune.basekotlinapp.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.toune.basekotlinapp.R
import com.toune.dltools.DLColorTool
import java.lang.StringBuilder
import kotlin.random.Random

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-上午 10:40
 * @Info 描述：
 */
class MainAdapter(layoutResId: Int, data: MutableList<String>?) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setBackgroundColor(R.id.textView,Color.parseColor(DLColorTool.randomColor()))
        holder.setText(R.id.textView,item)
    }
}