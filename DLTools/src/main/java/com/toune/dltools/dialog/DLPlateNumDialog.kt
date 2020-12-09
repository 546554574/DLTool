package com.toune.dltools.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toune.dltools.DLToast
import com.toune.dltools.R
import java.util.*

class DLPlateNumDialog {
    var cancleTv: TextView? = null
    var sureTv: TextView? = null
    var rcyView: RecyclerView? = null
    private var dialog: Dialog? = null
    private val layoutId = 0
    private var view: View? = null
    private val datas: MutableList<String?> = ArrayList()
    private var adapterPlateNum: AdapterPlateNum? = null
    private var plateNumTv: TextView? = null

    companion object {
        private var context: Context? = null
        private var display: Display? = null
        private var onPlateNumberButtonListener: OnPlateNumberButtonListener? = null
        private var plateNum: StringBuilder? = null
        fun with(context: Context): DLPlateNumDialog.Companion {
            this.context = context
            @SuppressLint("WrongConstant") val windowManager: WindowManager =
                context.getSystemService("window") as WindowManager
            display = windowManager.defaultDisplay
            return this
        }

        fun setOnPlateNumDialogClickListener(onPlateNumberButtonListener: OnPlateNumberButtonListener): DLPlateNumDialog.Companion {
            this.onPlateNumberButtonListener = onPlateNumberButtonListener
            return this
        }

        fun initPlateNum(plateNum: StringBuilder?): DLPlateNumDialog.Companion {
            if (plateNum == null) {
                this.plateNum = StringBuilder()
            } else {
                this.plateNum = StringBuilder(plateNum)
            }
            return this
        }

        fun build():DLPlateNumDialog{
            return DLPlateNumDialog()
        }
    }

    fun show(): DLPlateNumDialog {
        if (plateNum == null) {
            plateNum = StringBuilder()
        }
        view = LayoutInflater.from(context).inflate(R.layout.dialog_plate_num, null as ViewGroup?)
        view!!.minimumWidth = display!!.width
        rcyView = view!!.findViewById(R.id.rcy_view)
        cancleTv = view!!.findViewById<TextView>(R.id.cancle_tv)
        sureTv = view!!.findViewById<TextView>(R.id.sure_tv)
        plateNumTv = view!!.findViewById<TextView>(R.id.plate_num_tv)
        if (plateNum!!.isNotEmpty()) {
            plateNumTv!!.text = plateNum
        }
        cancleTv!!.setOnClickListener(View.OnClickListener {
            if (onPlateNumberButtonListener != null) {
                onPlateNumberButtonListener!!.cancel()
            }
            dialog!!.dismiss()
        })
        sureTv!!.setOnClickListener(View.OnClickListener {
            if (plateNum!!.length != 7) {
                DLToast.showInfoToast("请输入正确的车牌号")
            } else {
                if (onPlateNumberButtonListener != null) {
                    onPlateNumberButtonListener!!.done(plateNum)
                }
                dialog!!.dismiss()
            }
        })
        dialog = Dialog(context!!, R.style.ActionGeneralDialog)
        dialog!!.setContentView(view!!)
        val dialogWindow = dialog!!.window
        dialogWindow!!.setGravity(83)
        val lp: WindowManager.LayoutParams = dialogWindow.attributes
        lp.x = 0
        lp.y = 0
        dialogWindow.attributes = lp
        initView()
        dialog!!.show()
        return this
    }

    private fun initView() {
        initDatas()
        notifyAdapter()
    }

    private fun initDatas() {
        if (plateNum!!.isEmpty()) {
            datas.clear()
            for (i in PROVINCE_CODE.indices) {
                datas.add(PROVINCE_CODE[i])
            }
        } else if (plateNum!!.isNotEmpty()) {
            datas.clear()
            for (i in PLATE_NUM.indices) {
                datas.add(PLATE_NUM[i])
            }
        }
    }

    private fun notifyAdapter() {
        initDatas()
        if (adapterPlateNum == null) {
            adapterPlateNum = AdapterPlateNum(R.layout.adapter_plate_num, datas)
            rcyView!!.layoutManager = GridLayoutManager(context, 6)
            //添加Android自带的分割线
            rcyView!!.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            rcyView!!.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.HORIZONTAL
                )
            )
            rcyView!!.adapter = adapterPlateNum!!
            adapterPlateNum!!.onCustomListener = object : OnCustomListener {
                override fun onAdd(item: String?) {
                    if (plateNum!!.length < 7) {
                        plateNum!!.append(item)
                        onPlateNum(plateNum)
                    }
                    notifyAdapter()
                }

                override fun onDel() {
                    if (plateNum!!.isNotEmpty()) {
                        plateNum!!.deleteCharAt(plateNum!!.length - 1)
                        onPlateNum(plateNum)
                    }
                    notifyAdapter()
                }

                override fun onDelALl() {
                    plateNum = StringBuilder()
                    onPlateNum(plateNum)
                    notifyAdapter()
                }
            }
        } else {
            adapterPlateNum!!.notifyDataSetChanged()
        }
    }

    fun onPlateNum(str: StringBuilder?) {
        plateNumTv!!.text = str
    }

    interface OnCustomListener {
        fun onAdd(item: String?)
        fun onDel()
        fun onDelALl()
    }

    interface OnPlateNumberButtonListener {
        fun cancel()
        fun done(str: StringBuilder?)
    }

    var PROVINCE_CODE = arrayOf(
        "京",
        "津",
        "沪",
        "渝",
        "冀",
        "豫",
        "鲁",
        "晋",
        "陕",
        "皖",
        "苏",
        "浙",
        "鄂",
        "湘",
        "赣",
        "闽",
        "粤",
        "桂",
        "琼",
        "川",
        "贵",
        "云",
        "辽",
        "吉",
        "黑",
        "蒙",
        "甘",
        "宁",
        "青",
        "新",
        "藏",
        "港",
        "澳",
        "台",
        "",
        "del"
    )
    var PLATE_NUM = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "J",
        "K",
        "L",
        "M",
        "N",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "挂",
        "del"
    )

}