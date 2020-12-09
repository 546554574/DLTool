package com.toune.dltools.dialog

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.toune.dltools.R

class AdapterPlateNum(layoutResId: Int, data: MutableList<String?>) :
    BaseQuickAdapter<String?, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: String?) {
        val linearLayout: LinearLayout = helper.getView(R.id.root_layout)
        linearLayout.removeAllViews()
        if (item != "del") {
            val textView = TextView(context)
            textView.text = item
            linearLayout.addView(textView)
            linearLayout.setOnClickListener(View.OnClickListener {
                if (onCustomListener != null) {
                    onCustomListener!!.onAdd(item)
                }
            })
        } else {
            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.ic_del)
            linearLayout.addView(imageView)
            linearLayout.setOnClickListener(View.OnClickListener {
                if (onCustomListener != null) {
                    onCustomListener!!.onDel()
                }
            })
            linearLayout.setOnLongClickListener {
                if (onCustomListener != null) {
                    onCustomListener!!.onDelALl()
                }
                true
            }
        }
    }

    var onCustomListener: DLPlateNumDialog.OnCustomListener? = null
}