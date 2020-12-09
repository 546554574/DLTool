package com.toune.dltools
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.NonNull
import es.dmoral.toasty.Toasty

/**
 * @Author Dong Lei
 * @Date 2020/12/4 0004-下午 14:15
 * @Info 描述：toast 工具类
 */
object DLToast {
    var context = DLTool.context
    fun showToast(toastStr:String){
        Toasty.normal(context!!,toastStr).show()
    }
    fun showToast(toastStr:String,icon:Drawable){
        Toasty.normal(context!!,toastStr,icon).show()
    }
    fun showInfoToast(toastStr:String){
        Toasty.info(context!!,toastStr).show()
    }
    fun showInfoToast(toastStr:String,withIcon: Boolean){
        Toasty.info(context!!,toastStr,Toast.LENGTH_SHORT,withIcon).show()
    }
    fun showErrorToast(toastStr:String){
        Toasty.error(context!!,toastStr).show()
    }
    fun showErrorToast(toastStr:String,withIcon: Boolean){
        Toasty.error(context!!,toastStr,Toast.LENGTH_SHORT,withIcon).show()
    }
    fun showSuccessToast(toastStr:String){
        Toasty.success(context!!,toastStr).show()
    }
    fun showSuccessToast(toastStr:String,withIcon: Boolean){
        Toasty.success(context!!,toastStr,Toast.LENGTH_SHORT,withIcon).show()
    }
    fun showWarningToast(toastStr:String){
        Toasty.warning(context!!,toastStr).show()
    }
    fun showWarningToast(toastStr:String,withIcon: Boolean){
        Toasty.warning(context!!,toastStr,Toast.LENGTH_SHORT,withIcon).show()
    }
    fun showCustomToast(toastStr: String,icon:Drawable ,
                        duration:Int, withIcon:Boolean){
        Toasty.custom(context!!,toastStr,icon,duration,withIcon).show()
    }
}