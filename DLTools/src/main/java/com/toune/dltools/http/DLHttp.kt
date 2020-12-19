package com.toune.dltools.http

import android.os.Handler
import android.os.Message
import com.toune.dltools.DLFileTool
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author Dong Lei
 * @Date 2020/12/16 0016-下午 12:35
 * @Info 描述：网络请求
 */
class DLHttp {
    companion object {
        private val REQUEST_POST = 1001 //post请求
        private val REQUEST_GET = 1002  //get请求
        private var REQUEST_METHOD = REQUEST_GET //默认请求方式GET


        private val JSON_TYPE = 301  //JSON格式提交
        private val FILE_TYPE = 302  //提交文件
        private val FORM_TYPE = 303  //表单提交
        private var BUILD_TYPE = FORM_TYPE //默认表单提交


        private var dlHttpClient: OkHttpClient? = null
        private var dlHttp: DLHttp? = null
        private var requestUrl: String? = null

        private var downUrl: String? = null
        private var filrDir: String? = null

        /**
         * 初始化
         */
        fun instance() {
            if (dlHttp == null) {
                dlHttp = DLHttp()
            } else {
                dlHttp!!.params.clear()
                dlHttp!!.files.clear()
            }
        }

        /**
         * 获取client
         */
        private fun getHttpClient() {
            if (dlHttpClient == null) {
                dlHttpClient = OkHttpClient()
            }
        }

        /**
         * post请求
         * @param url String? 地址
         * @return DLHttp
         */
        fun post(url: String?): DLHttp {
            instance()
            getHttpClient()
            REQUEST_METHOD = REQUEST_POST
            requestUrl = url
            return dlHttp!!
        }

        /**
         * get请求
         * @param url String?
         * @return DLHttp
         */
        fun get(url: String?): DLHttp {
            instance()
            getHttpClient()
            REQUEST_METHOD = REQUEST_GET
            requestUrl = url
            return dlHttp!!
        }


        /**
         * 下载文件
         * @param realURL String?
         * @param destFileDir String?
         * @return DLHttp
         */
        fun downFile(realURL: String?, destFileDir: String?): DLHttp {
            instance()
            getHttpClient()
            downUrl = realURL
            filrDir = destFileDir
            return dlHttp!!
        }
    }

    private val JSONType: MediaType =
        "application/json; charset=utf-8".toMediaTypeOrNull()!! //JSON的mediaType表头
    private var requestBody: RequestBody? = null
    private var request: Request? = null


    /**
     * 参数
     */
    private var params: MutableMap<String, Any?> = HashMap()

    /**
     * 文件参数
     */
    private val files: MutableList<File> = ArrayList()

    /**
     * 添加单一参数
     * @param key String
     * @param value Any
     * @return DLHttp
     */
    fun add(key: String, value: Any): DLHttp {
        params[key] = value.toString()
        return this
    }

    /**
     * 添加参数集合
     * @param map Map<String, Any?>
     * @return DLHttp
     */
    fun add(map: Map<String, Any?>): DLHttp {
        params.putAll(map)
        return this
    }


    /**
     * 添加文件参数
     * @param key String
     * @param file File
     * @return DLHttp
     */
    fun add(key: String, file: File): DLHttp {
        files.add(file)
        return this
    }

    /**
     * 以JSON的方式提交数据
     * @param dlhttpCallBack IDLHttpCallBack<T>
     */
    fun <T> buildByJson(dlhttpCallBack: IDLHttpCallBack<T>) {
        BUILD_TYPE = JSON_TYPE
        build<T>(dlhttpCallBack)
    }

    /**
     * 基于http的文件上传（传入文件数组和key）混合参数和文件请求
     * 通过addFormDataPart可以添加多个上传的文件
     */
    fun <T> buildByFile(myDataCallBack: IDLHttpCallBack<T>) {
        val multipartBody: MultipartBody.Builder = MultipartBody.Builder()
        multipartBody.setType(MultipartBody.FORM)
        for (key in params.keys) {
            multipartBody.addFormDataPart(key, params[key].toString())
        }
        var fileBody: RequestBody? = null
        for ((index, file) in files.withIndex()) {
            val fileName = file.name
            fileBody = file.asRequestBody(guessMimeType(fileName).toMediaTypeOrNull())
            multipartBody.addFormDataPart("file$index", fileName, fileBody)
        }
        requestBody = multipartBody.build()
        BUILD_TYPE = FILE_TYPE
        build<T>(myDataCallBack)
    }

    /**
     * 获取文件提交的guessMimeType
     * @param fileName String
     * @return String
     */
    private fun guessMimeType(fileName: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor = fileNameMap.getContentTypeFor(fileName)
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return contentTypeFor
    }


    fun <T> build(dlHttpCallBack: IDLHttpCallBack<T>) {
        //开始请求
        dlHttpCallBack.Builder().startH()
        //数据上传方式
        when (BUILD_TYPE) {
            JSON_TYPE -> requestBody = GsonBinder.toJsonStr(params).toRequestBody(JSONType)
            FILE_TYPE -> {
            }
            FORM_TYPE -> {
                //form表单提交
                val builder: FormBody.Builder = FormBody.Builder()
                for (key in params.keys) {
                    builder.add(key, params[key].toString())
                }
                requestBody = builder.build()
            }
        }
        //数据上传方式
        when (REQUEST_METHOD) {
            REQUEST_GET -> {
                val urlBuilder: HttpUrl.Builder = requestUrl!!.toHttpUrlOrNull()!!.newBuilder()
                for (key in params.keys) {
                    urlBuilder.addQueryParameter(key, params[key].toString())
                }
                request = Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build()
            }
            REQUEST_POST -> {
                request = Request.Builder()
                    .url(requestUrl!!)
                    .post(requestBody!!)
                    .build()
            }
        }
        val call: okhttp3.Call = dlHttpClient!!.newCall(request!!)
        call.enqueue(object : Callback {
            //请求错误
            override fun onFailure(call: Call, e: IOException) {
                dlHttpCallBack.Builder().errorH(e.message)
            }

            //请求结果
            override fun onResponse(call: Call, response: Response) {
                try {
                    var jsonStr: String = response.body!!.string()
                    val iBaseCallBack = IBaseCallBack<T>()
                    val baseObj = GsonBinder.toObj(jsonStr, iBaseCallBack.javaClass)
                    if (baseObj!=null){
                        when (baseObj!!.code) {
                            IBaseCallBack.SUCCESS_CODE -> {
                                dlHttpCallBack.Builder().successH(iBaseCallBack.data)
                            }
                            IBaseCallBack.ERROR_CODE -> {
                                dlHttpCallBack.Builder().errorH(iBaseCallBack.msg)
                            }
                        }
                    }else{
                        dlHttpCallBack.Builder().successH(jsonStr as T)
                    }
                    response.close()
                    dlHttpCallBack.Builder().endH()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    dlHttpCallBack.Builder().errorH("网络开小差,请稍后重试")
                    dlHttpCallBack.Builder().endH()
                }
            }
        })
    }

    /**
     * 下载文件******************************************************************************************************************************************
     */
    /**
     * 文件下载
     *
     * @param url path路径
     * @param destFileDir 本地存储的文件夹路径
     * @param myDataCallBack 自定义回调接口
     */
    private var downCall: Call? = null
    private val totalSize = 0L //APK总大小

    private val downloadSize = 0L // 下载的大小

    private val count = 0f //下载百分比


    fun down(httpFileCallBack: IDLHttpFileCallBack) {
        val request: Request = Request.Builder()
            .url(downUrl!!)
            .build()
        downCall = dlHttpClient!!.newCall(request)
        downCall!!.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> {
                        var `is`: InputStream? = null
                        val buf = ByteArray(2048)
                        var len = 0
                        var fos: FileOutputStream? = null
                        // 储存下载文件的目录
                        val dir: File = File(DLFileTool.getSDCardPath())
                        if (!dir.exists()) {
                            dir.mkdirs()
                        }
                        DLFileTool.createFileByDeleteOldFile(dir.toString() + "dltoolFile.apk")
                        var updateFile = DLFileTool.getFileByPath(dir.toString() + "dltoolFile.apk")
                        try {
                            `is` = response.body!!.byteStream()
                            val total: Long = response.body!!.contentLength()
                            httpFileCallBack.Builder().startH(total)
                            fos = FileOutputStream(updateFile)
                            var sum: Long = 0
                            while (`is`.read(buf).also { len = it } != -1) {
                                fos.write(buf, 0, len)
                                sum += len.toLong()
                                httpFileCallBack.Builder().progressH(sum)
                            }
                            fos.flush()
                            fos.close()
                            httpFileCallBack.Builder().successH(updateFile)
                        } catch (e: Exception) {
                            httpFileCallBack.Builder().errorH("下载失败")
                        } finally {
                            try {
                                `is`?.close()
                            } catch (e: IOException) {
                            }
                            try {
                                fos?.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    else -> {
                        httpFileCallBack.Builder().errorH("下载失败")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                httpFileCallBack.Builder().errorH(e.message)
            }
        })
    }

    private fun getFileName(url: String?): String? {
        val separatorIndex = url!!.lastIndexOf("/")
        return if (separatorIndex < 0) url else url.substring(separatorIndex + 1, url.length)
    }


    fun cancelDownload() {
        if (downCall != null) {
            downCall!!.cancel()
        }
    }

}