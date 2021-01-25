package com.toune.dltools.dialog

import android.content.Context
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheetListItemModel
import com.toune.dltools.R
import com.yanzhenjie.album.Action
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author Dong Lei
 * @Date 2020/12/7 0007-上午 9:26
 * @Info 描述：
 */
object DLSelectPhotoDialog {
    var context: Context? = null
    fun with(context: Context): DLSelectPhotoDialog {
        this.context = context
        return this
    }

    var isSingle = false

    /**
     * 选择照片的时候是单选还是多选
     * @param isSingle Boolean
     */
    fun setImgSingleChoice(isSingle: Boolean): DLSelectPhotoDialog {
        this.isSingle = isSingle
        return this
    }

    val SHOW_CAMERA_SELETION = 1001 //直接跳转到拍照
    val SHOW_IMAGE_SELETION = 1002 //直接跳转到选择照片
    val SHOW_ALL_SELECTION = 1003 //拍照和选择照片都显示
    var SHOW_MEDIA_TYPE = SHOW_ALL_SELECTION //默认显示全部


    /**
     *
     * @param boolean Boolean
     */
    fun show() {
        showBottomDialog()
    }

    /**
     *
     * @param boolean Boolean
     */
    fun show(mediaType: Int) {
        SHOW_MEDIA_TYPE = mediaType
        showBottomDialog()
    }


    private fun showBottomDialog() {
        Album.initialize(
            AlbumConfig.newBuilder(context)
                .setAlbumLoader(MediaLoader())
                .build()
        )
        when (SHOW_MEDIA_TYPE) {
            SHOW_CAMERA_SELETION -> {
                takePhoto()
            }
            SHOW_IMAGE_SELETION -> {
                selectPhoto()
            }
            SHOW_ALL_SELECTION -> {
                val bottomPopupWindow: QMUIBottomSheet.BottomListSheetBuilder =
                    QMUIBottomSheet.BottomListSheetBuilder(context)
                bottomPopupWindow.setTitle("上传图片")
                bottomPopupWindow.addItem(android.R.drawable.ic_menu_camera,"拍一张照片","camera")
                bottomPopupWindow.addItem(android.R.drawable.ic_menu_gallery,"从相册选一张","image")
                bottomPopupWindow.setOnSheetItemClickListener { dialog, itemView, position, tag ->
                    when (position) {
                        0 -> {
                            takePhoto()
                        }
                        1 -> {
                            selectPhoto()
                        }
                    }
                }
                bottomPopupWindow.build().show()
            }
        }
    }

    private fun selectPhoto() {
        if (isSingle){
            Album.image(context)
                .singleChoice()
                .camera(true)
                .columnCount(4)
                .onResult {
                    if (onDialogListener!=null){
                        onDialogListener!!.sureListener(it)
                    }
                }
                .onCancel {
                    if (onDialogListener!=null){
                        onDialogListener!!.cancelListener()
                    }
                }
                .start()
        }else{
            Album.image(context)
                .multipleChoice()
                .camera(true)
                .columnCount(4)
                .onResult {
                    if (onDialogListener!=null){
                        onDialogListener!!.sureListener(it)
                    }
                }
                .onCancel {
                    if (onDialogListener!=null){
                        onDialogListener!!.cancelListener()
                    }
                }
                .start()
        }
    }

    private fun takePhoto() {
        try {
            Album.camera(context) // Camera function.
                .image() // Take Picture.
                .onResult(Action<String> { result ->
                    var albumFile = AlbumFile()
                    albumFile.path = result
                    var albumFiles = ArrayList<AlbumFile>()
                    albumFiles.add(albumFile)
                    if(onDialogListener!=null){
                        onDialogListener!!.sureListener(albumFiles)
                    }
                })
                .onCancel(Action<String> {
                    if (onDialogListener!=null){
                        onDialogListener!!.cancelListener()
                    }
                })
                .start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    var onDialogListener:OnDialogListener?=null
    interface OnDialogListener{
        fun sureListener(files: ArrayList<AlbumFile>)
        fun cancelListener()
    }
}