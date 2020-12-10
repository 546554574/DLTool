package com.toune.basekotlinapp.fragment

import android.graphics.Color
import android.os.Bundle
import cn.bertsir.zbar.Qr.ScanResult
import cn.bertsir.zbar.QrConfig
import cn.bertsir.zbar.QrManager
import cn.bertsir.zbar.utils.QRUtils
import cn.bertsir.zbar.view.ScanLineView
import com.qmuiteam.qmui.util.QMUIDrawableHelper
import com.toune.basekotlinapp.R
import com.toune.dltools.DLBasePresenterImpl
import com.toune.dltools.DLBaseView
import com.toune.dltools.DLToast
import com.toune.dltools.ui.DLBaseFragment
import kotlinx.android.synthetic.main.fragment_qr.*


/**
 * @Author Dong Lei
 * @Date 2020/12/9 0009-下午 15:41
 * @Info 描述：
 */
class QRFragment : DLBaseFragment<DLBaseView, DLBasePresenterImpl<DLBaseView>>() {
    companion object {
        fun newInstance(): QRFragment {
            val args = Bundle()
            val fragment = QRFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val layout: Int
        get() = R.layout.fragment_qr

    override fun lazyInit() {
        initConfig()
        createQrBtn.setOnClickListener {
            val qrCode = QRUtils.getInstance().createQRCode("hello DLTool")
            qrIv.setImageBitmap(qrCode)
        }
        readQrBtn.setOnClickListener {
            QrManager.getInstance().init(qrConfig)
                .startScan(activity) {
                    DLToast.showSuccessToast(it!!.content)
                }
        }
        createQrByImgBtn.setOnClickListener {
            val createQRCodeAddLogo = QRUtils.getInstance().createQRCodeAddLogo(
                "hello DLTool",
                QMUIDrawableHelper.drawableToBitmap(resources.getDrawable(R.drawable.logo))
            )
            qrIv.setImageBitmap(createQRCodeAddLogo)
        }
    }

    lateinit var qrConfig: QrConfig
    private fun initConfig() {
        qrConfig = QrConfig.Builder()
            .setDesText("(识别二维码)")//扫描框下文字
            .setShowDes(false)//是否显示扫描框下面文字
            .setShowLight(true)//显示手电筒按钮
            .setShowTitle(true)//显示Title
            .setShowAlbum(true)//显示从相册选择按钮
            .setCornerColor(Color.WHITE)//设置扫描框颜色
            .setLineColor(Color.WHITE)//设置扫描线颜色
            .setLineSpeed(QrConfig.LINE_MEDIUM)//设置扫描线速度
            .setScanType(QrConfig.TYPE_QRCODE)//设置扫码类型（二维码，条形码，全部，自定义，默认为二维码）
            .setScanViewType(QrConfig.SCANVIEW_TYPE_QRCODE)//设置扫描框类型（二维码还是条形码，默认为二维码）
            .setCustombarcodeformat(QrConfig.BARCODE_I25)//此项只有在扫码类型为TYPE_CUSTOM时才有效
            .setPlaySound(true)//是否扫描成功后bi~的声音
            .setNeedCrop(true)//从相册选择二维码之后再次截取二维码
//            .setDingPath(R.raw.test)//设置提示音(不设置为默认的Ding~)
            .setIsOnlyCenter(true)//是否只识别框中内容(默认为全屏识别)
            .setTitleText("扫描二维码")//设置Tilte文字
            .setTitleBackgroudColor(Color.BLUE)//设置状态栏颜色
            .setTitleTextColor(Color.BLACK)//设置Title文字颜色
            .setShowZoom(false)//是否手动调整焦距
            .setAutoZoom(false)//是否自动调整焦距
            .setFingerZoom(false)//是否开始双指缩放
            .setScreenOrientation(QrConfig.SCREEN_PORTRAIT)//设置屏幕方向
            .setDoubleEngine(false)//是否开启双引擎识别(仅对识别二维码有效，并且开启后只识别框内功能将失效)
            .setOpenAlbumText("选择要识别的图片")//打开相册的文字
            .setLooperScan(false)//是否连续扫描二维码
            .setLooperWaitTime(5 * 1000)//连续扫描间隔时间
            .setScanLineStyle(ScanLineView.style_radar)//扫描动画样式
            .setAutoLight(false)//自动灯光
            .setShowVibrator(false)//是否震动提醒
            .create()
    }

    override fun initPresenter(): DLBasePresenterImpl<DLBaseView> {
        return DLBasePresenterImpl()
    }
}