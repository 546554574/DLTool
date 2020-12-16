package com.toune.dltools

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.toune.dltools.DLConstTool.KB
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.*

/**
 * @Author Dong Lei
 * @Date 2020/12/11 0011-上午 8:55
 * @Info 描述：
 */
object DLFileTool {
    val UTF8code = "UTF-8"
    val GBKcode = "GBK"
    const val BUFSIZE = 1024 * 8
    private const val TAG = "RxFileTool"
    /**
     * 得到SD卡根目录.
     */
    fun getRootPath(): File? {
        var path: File? = null
        path = if (sdCardIsAvailable()) {
           Environment.getExternalStorageDirectory() // 取得sdcard文件路径
        } else {
            Environment.getDataDirectory()
        }
        return path
    }

    /**
     * 获取的目录默认没有最后的”/”,需要自己加上
     * 获取本应用图片缓存目录
     *
     * @return
     */
    fun getCacheFolder(context: Context): File? {
        val folder = File(context.cacheDir, "IMAGECACHE")
        if (!folder.exists()) {
            folder.mkdir()
        }
        return folder
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br></br>false : 不可用
     */
    fun isSDCardEnable(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    /**
     * 获取SD卡路径
     *
     * 一般是/storage/emulated/0/
     *
     * @return SD卡路径
     */
    fun getSDCardPath(): String? {
        return if (!isSDCardEnable()) {
            "sdcard unable!"
        } else Environment.getExternalStorageDirectory().path + File.separator
    }

    /**
     * 获取SD卡Data路径
     *
     * @return SD卡Data路径
     */
    fun getDataPath(): String? {
        return if (!isSDCardEnable()) {
            "sdcard unable!"
        } else Environment.getDataDirectory().path
    }

    /**
     * SD卡是否可用.
     */
    fun sdCardIsAvailable(): Boolean {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val sd = File(Environment.getExternalStorageDirectory().path)
            sd.canWrite()
        } else {
            false
        }
    }

    /**
     * 文件或者文件夹是否存在.
     */
    fun fileExists(filePath: String?): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    /**
     * 删除指定文件夹下所有文件, 不保留文件夹.
     */
    fun delAllFile(path: String?): Boolean {
        val flag = false
        val file = File(path)
        if (!file.exists()) {
            return flag
        }
        if (file.isFile) {
            file.delete()
            return true
        }
        val files = file.listFiles()
        for (i in files.indices) {
            val exeFile = files[i]
            if (exeFile.isDirectory) {
                delAllFile(exeFile.absolutePath)
            } else {
                exeFile.delete()
            }
        }
        file.delete()
        return flag
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFilesInDir(dirPath: String?): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        if (dir == null) {
            return false
        }
        // 目录不存在返回true
        if (!dir.exists()) {
            return true
        }
        // 不是目录返回false
        if (!dir.isDirectory) {
            return false
        }
        // 现在文件存在且是文件夹
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!deleteFile(file)) {
                        return false
                    }
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * 清除内部缓存
     *
     * /data/data/com.xxx.xxx/cache
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalCache(context: Context): Boolean {
        return deleteFilesInDir(context.cacheDir)
    }

    /**
     * 清除内部文件
     *
     * /data/data/com.xxx.xxx/files
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalFiles(context: Context): Boolean {
        return deleteFilesInDir(context.filesDir)
    }

    /**
     * 清除内部数据库
     *
     * /data/data/com.xxx.xxx/databases
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalDbs(context: Context): Boolean {
        return deleteFilesInDir(context.filesDir.parent + File.separator + "databases")
    }

    /**
     * 根据名称清除数据库
     *
     * /data/data/com.xxx.xxx/databases/dbName
     *
     * @param dbName 数据库名称
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalDbByName(context: Context, dbName: String?): Boolean {
        return context.deleteDatabase(dbName)
    }

    /**
     * 清除内部SP
     *
     * /data/data/com.xxx.xxx/shared_prefs
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanInternalSP(context: Context): Boolean {
        return deleteFilesInDir(context.filesDir.parent + File.separator + "shared_prefs")
    }

    /**
     * 清除外部缓存
     *
     * /storage/emulated/0/android/data/com.xxx.xxx/cache
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanExternalCache(context: Context): Boolean {
        return isSDCardEnable() && deleteFilesInDir(
            context.externalCacheDir
        )
    }

    /**
     * 清除自定义目录下的文件
     *
     * @param dirPath 目录路径
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanCustomCache(dirPath: String?): Boolean {
        return deleteFilesInDir(dirPath)
    }

    /**
     * 清除自定义目录下的文件
     *
     * @param dir 目录
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    fun cleanCustomCache(dir: File?): Boolean {
        return deleteFilesInDir(dir)
    }

    /**
     * 文件复制.
     */
    fun copy(srcFile: String?, destFile: String?): Boolean {
        try {
            val `in` = FileInputStream(srcFile)
            val out = FileOutputStream(destFile)
            val bytes = ByteArray(1024)
            var c: Int
            while (`in`.read(bytes).also { c = it } != -1) {
                out.write(bytes, 0, c)
            }
            `in`.close()
            out.flush()
            out.close()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * 复制整个文件夹内.
     *
     * @param oldPath string 原文件路径如：c:/fqf.
     * @param newPath string 复制后路径如：f:/fqf/ff.
     */
    fun copyFolder(oldPath: String, newPath: String) {
        try {
            File(newPath).mkdirs() // 如果文件夹不存在 则建立新文件夹
            val a = File(oldPath)
            val file = a.list()
            var temp: File? = null
            for (i in file.indices) {
                temp = if (oldPath.endsWith(File.separator)) {
                    File(oldPath + file[i])
                } else {
                    File(oldPath + File.separator + file[i])
                }
                if (temp.isFile) {
                    val input = FileInputStream(temp)
                    val output = FileOutputStream(newPath + "/" + temp.name.toString())
                    val b = ByteArray(1024 * 5)
                    var len: Int
                    while (input.read(b).also { len = it } != -1) {
                        output.write(b, 0, len)
                    }
                    output.flush()
                    output.close()
                    input.close()
                }
                if (temp.isDirectory) { // 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i])
                }
            }
        } catch (e: NullPointerException) {
        } catch (e: Exception) {
        }
    }

    /**
     * 重命名文件.
     */
    fun renameFile(resFilePath: String?, newFilePath: String?): Boolean {
        val resFile = File(resFilePath)
        val newFile = File(newFilePath)
        return resFile.renameTo(newFile)
    }


    /**
     * 获取磁盘可用空间.
     */
    @SuppressLint("NewApi")
    fun getSDCardAvailaleSize(): Long {
        val path: File = getRootPath()!!
        val stat = StatFs(path.path)
        val blockSize: Long
        val availableBlocks: Long
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.blockSizeLong
            availableBlocks = stat.availableBlocksLong
        } else {
            blockSize = stat.blockSize.toLong()
            availableBlocks = stat.availableBlocks.toLong()
        }
        return availableBlocks * blockSize
    }

    /**
     * 获取某个目录可用大小.
     */
    @SuppressLint("NewApi")
    fun getDirSize(path: String?): Long {
        val stat = StatFs(path)
        val blockSize: Long
        val availableBlocks: Long
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.blockSizeLong
            availableBlocks = stat.availableBlocksLong
        } else {
            blockSize = stat.blockSize.toLong()
            availableBlocks = stat.availableBlocks.toLong()
        }
        return availableBlocks * blockSize
    }

    /**
     * 获取文件或者文件夹大小.
     */
    fun getFileAllSize(path: String?): Long {
        val file = File(path)
        return if (file.exists()) {
            if (file.isDirectory) {
                val childrens = file.listFiles()
                var size: Long = 0
                for (f in childrens) {
                    size += getFileAllSize(f.path)
                }
                size
            } else {
                file.length()
            }
        } else {
            0
        }
    }

    /**
     * 创建一个文件.
     */
    fun initFile(path: String?): Boolean {
        var result = false
        try {
            val file = File(path)
            if (!file.exists()) {
                result = file.createNewFile()
            } else if (file.isDirectory) {
                file.delete()
                result = file.createNewFile()
            } else if (file.exists()) {
                result = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 创建一个文件夹.
     */
    fun initDirectory(path: String?): Boolean {
        var result = false
        val file = File(path)
        if (!file.exists()) {
            result = file.mkdir()
        } else if (!file.isDirectory) {
            file.delete()
            result = file.mkdir()
        } else if (file.exists()) {
            result = true
        }
        return result
    }

    /**
     * 复制文件.
     */
    @Throws(IOException::class)
    fun copyFile(from: File, to: File?) {
        if (!from.exists()) {
            throw IOException("The source file not exist: " + from.absolutePath)
        }
        val fis = FileInputStream(from)
        try {
            copyFile(fis, to)
        } finally {
            fis.close()
        }
    }

    /**
     * 从InputStream流复制文件.
     */
    @Throws(IOException::class)
    fun copyFile(from: InputStream, to: File?): Long {
        var totalBytes: Long = 0
        val fos = FileOutputStream(to, false)
        try {
            val data = ByteArray(1024)
            var len: Int
            while (from.read(data).also { len = it } > -1) {
                fos.write(data, 0, len)
                totalBytes += len.toLong()
            }
            fos.flush()
        } finally {
            fos.close()
        }
        return totalBytes
    }

    /**
     * 保存InputStream流到文件.
     */
    fun saveFile(inputStream: InputStream, filePath: String?) {
        try {
            val outputStream: OutputStream = FileOutputStream(File(filePath), false)
            var len: Int
            val buffer = ByteArray(1024)
            while (inputStream.read(buffer).also { len = it } != -1) {
                outputStream.write(buffer, 0, len)
            }
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 用UTF8保存一个文件.
     */
    @Throws(IOException::class)
    fun saveFileUTF8(path: String?, content: String?, append: Boolean?) {
        val fos = FileOutputStream(path, append!!)
        val out: Writer = OutputStreamWriter(fos, "UTF-8")
        out.write(content)
        out.flush()
        out.close()
        fos.flush()
        fos.close()
    }

    /**
     * 用UTF8读取一个文件.
     */
    fun getFileUTF8(path: String?): String? {
        var result = ""
        var fin: InputStream? = null
        try {
            fin = FileInputStream(path)
            val length = fin.available()
            val buffer = ByteArray(length)
            fin.read(buffer)
            fin.close()
            result = String(buffer, Charsets.UTF_8)
        } catch (e: java.lang.Exception) {
        }
        return result
    }

    /**
     * 得到一个文件Intent.
     */
    fun getFileIntent(path: String?, mimeType: String?): Intent? {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File(path)), mimeType)
        return intent
    }

    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    fun getDiskCacheDir(context: Context): String? {
        var cachePath: String? = null
        cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                context.externalCacheDir!!.path
            } else {
                context.cacheDir.path
            }
        return cachePath
    }

    /**
     * 获取缓存视频文件目录
     *
     * @param context
     * @return
     */
    fun getDiskFileDir(context: Context): String? {
        var cachePath: String? = null
        cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.path
            } else {
                context.filesDir.path
            }
        return cachePath
    }

    /**
     * 多个文件合并
     *
     * @param outFile
     * @param files
     */
    fun mergeFiles(context: Context?, outFile: File?, files: List<File?>) {
        var outChannel: FileChannel? = null
        try {
            outChannel = FileOutputStream(outFile).channel
            for (f in files) {
                val fc = FileInputStream(f).channel
                val bb = ByteBuffer.allocate(BUFSIZE)
                while (fc.read(bb) != -1) {
                    bb.flip()
                    outChannel.write(bb)
                    bb.clear()
                }
                fc.close()
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        } finally {
            try {
                outChannel?.close()
            } catch (ignore: IOException) {
            }
        }
    }

    /**
     * 将在线的m3u8替换成本地的m3u8
     *
     * @param context  实体
     * @param file     在线的m3u8
     * @param pathList 本地的ts文件
     * @return
     */
    fun getNativeM3u(context: Context?, file: File?, pathList: List<File>): String? {
        var `in`: InputStream? = null
        var num = 0
        //需要生成的目标buff
        val buf = StringBuffer()
        try {
            if (file != null) {
                `in` = FileInputStream(file)
            }
            val reader = BufferedReader(InputStreamReader(`in`))
            var line = ""
            while (reader.readLine().also { line = it } != null) {
                if (line.length > 0 && line.startsWith("http://")) {
                    //replce 这行的内容
//                    Log.d("ts替换", line + "  replce  " + pathList.get(num).getAbsolutePath());
                    buf.append(
                        """
                    file:${pathList[num].absolutePath}
                    
                    """.trimIndent()
                    )
                    num++
                } else {
                    buf.append(
                        """
                        $line
                        
                        """.trimIndent()
                    )
                }
            }
            `in`!!.close()
            write(file!!.absolutePath, buf.toString())
            Log.d("ts替换", "ts替换完成")
        } catch (e1: FileNotFoundException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return buf.toString()
    }

    /**
     * 将字符串 保存成 文件
     *
     * @param filePath
     * @param content
     */
    fun write(filePath: String?, content: String?) {
        var bw: BufferedWriter? = null
        try {
            //根据文件路径创建缓冲输出流
            bw = BufferedWriter(FileWriter(filePath))
            // 将内容写入文件中
            bw.write(content)
            //            Log.d("M3U8替换", "替换完成");
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close()
                } catch (e: IOException) {
                    bw = null
                }
            }
        }
    }

    /**
     * 获取 搜索的路径 下的 所有 后缀 的文件
     *
     * @param fileAbsolutePath 搜索的路径
     * @param suffix           文件后缀
     * @return
     */
    fun GetAllFileName(fileAbsolutePath: String?, suffix: String?): Vector<String>? {
        val vecFile = Vector<String>()
        val file = File(fileAbsolutePath)
        val subFile = file.listFiles()
        for (iFileLength in subFile.indices) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory) {
                val filename = subFile[iFileLength].name
                // 判断是否为suffix结尾
                if (filename.trim { it <= ' ' }.toLowerCase().endsWith(suffix!!)) {
                    vecFile.add(filename)
                }
            }
        }
        return vecFile
    }


    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    fun getFileByPath(filePath: String?): File? {
        return if (filePath.isNullOrEmpty()) null else File(filePath)
    }
    //==============================================================================================

    //==============================================================================================
    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun isFileExists(filePath: String?): Boolean {
        return isFileExists(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isDir(dirPath: String?): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isDir(file: File?): Boolean {
        return isFileExists(file) && file!!.isDirectory
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isFile(filePath: String?): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isFile(file: File?): Boolean {
        return isFileExists(file) && file!!.isFile
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 文件路径
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(file: File?): Boolean {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsFile(filePath: String?): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) {
            return file.isFile
        }
        return if (!createOrExistsDir(file.parentFile)) {
            false
        } else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return `true`: 创建成功<br></br>`false`: 创建失败
     */
    fun createFileByDeleteOldFile(filePath: String?): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return `true`: 创建成功<br></br>`false`: 创建失败
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile && !file.delete()) {
            return false
        }
        // 创建目录失败返回false
        return if (!createOrExistsDir(file.parentFile)) {
            false
        } else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 复制或移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param isMove      是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    fun copyOrMoveDir(srcDirPath: String?, destDirPath: String?, isMove: Boolean): Boolean {
        return copyOrMoveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), isMove)
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @param isMove  是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    fun copyOrMoveDir(srcDir: File?, destDir: File?, isMove: Boolean): Boolean {
        if (srcDir == null || destDir == null) {
            return false
        }
        // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) {
            return false
        }
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory) {
            return false
        }
        // 目标目录不存在返回false
        if (!createOrExistsDir(destDir)) {
            return false
        }
        val files = srcDir.listFiles()
        for (file in files) {
            val oneDestFile = File(destPath + file.name)
            if (file.isFile) {
                // 如果操作失败返回false
                if (!copyOrMoveFile(file, oneDestFile, isMove)) {
                    return false
                }
            } else if (file.isDirectory) {
                // 如果操作失败返回false
                if (!copyOrMoveDir(file, oneDestFile, isMove)) {
                    return false
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param isMove       是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    fun copyOrMoveFile(srcFilePath: String?, destFilePath: String?, isMove: Boolean): Boolean {
        return copyOrMoveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), isMove)
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param isMove   是否移动
     * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
     */
    fun copyOrMoveFile(srcFile: File?, destFile: File?, isMove: Boolean): Boolean {
        if (srcFile == null || destFile == null) {
            return false
        }
        // 源文件不存在或者不是文件则返回false
        if (!srcFile.exists() || !srcFile.isFile) {
            return false
        }
        // 目标文件存在且是文件则返回false
        if (destFile.exists() && destFile.isFile) {
            return false
        }
        // 目标目录不存在返回false
        return if (!createOrExistsDir(destFile.parentFile)) {
            false
        } else try {
            (writeFileFromIS(destFile, FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyDir(srcDirPath: String?, destDirPath: String?): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath))
    }

    /**
     * 复制目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyDir(srcDir: File?, destDir: File?): Boolean {
        return copyOrMoveDir(srcDir, destDir, false)
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyFile(srcFilePath: String?, destFilePath: String?): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), false)
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return `true`: 复制成功<br></br>`false`: 复制失败
     */
    fun copyFile(srcFile: File?, destFile: File?, isCopy: Boolean): Boolean {
        return copyOrMoveFile(srcFile, destFile, false)
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveDir(srcDirPath: String?, destDirPath: String?): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath))
    }

    /**
     * 移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveDir(srcDir: File?, destDir: File?): Boolean {
        return copyOrMoveDir(srcDir, destDir, true)
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveFile(srcFilePath: String?, destFilePath: String?): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath))
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return `true`: 移动成功<br></br>`false`: 移动失败
     */
    fun moveFile(srcFile: File?, destFile: File?): Boolean {
        return copyOrMoveFile(srcFile, destFile, true)
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteDir(dirPath: String?): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) {
            return false
        }
        // 目录不存在返回true
        if (!dir.exists()) {
            return true
        }
        // 不是目录返回false
        if (!dir.isDirectory) {
            return false
        }
        // 现在文件存在且是文件夹
        val files = dir.listFiles()
        for (file in files) {
            if (file.isFile) {
                if (!deleteFile(file)) {
                    return false
                }
            } else if (file.isDirectory) {
                if (!deleteDir(file)) {
                    return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFile(srcFilePath: String?): Boolean {
        return deleteFile(getFileByPath(srcFilePath))
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDir(dirPath: String?, isRecursive: Boolean): List<File?>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDir(dir: File?, isRecursive: Boolean): List<File?>? {
        if (isRecursive) {
            return listFilesInDir(dir)
        }
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        Collections.addAll(list, *dir.listFiles())
        return list
    }

    /**
     * 获取目录下所有文件包括子目录
     *
     * @param dirPath 目录路径
     * @return 文件链表
     */
    fun listFilesInDir(dirPath: String?): List<File?>? {
        return listFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 获取目录下所有文件包括子目录
     *
     * @param dir 目录
     * @return 文件链表
     */
    fun listFilesInDir(dir: File?): List<File?>? {
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        val files = dir.listFiles()
        for (file in files) {
            list.add(file)
            if (file.isDirectory) {
                list.addAll(listFilesInDir(file)!!)
            }
        }
        return list
    }

    /**
     * 获取目录下所有后缀名为suffix的文件
     *
     * 大小写忽略
     *
     * @param dirPath     目录路径
     * @param suffix      后缀名
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        suffix: String,
        isRecursive: Boolean
    ): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), suffix, isRecursive)
    }

    /**
     * 获取目录下所有后缀名为suffix的文件
     *
     * 大小写忽略
     *
     * @param dir         目录
     * @param suffix      后缀名
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, suffix: String, isRecursive: Boolean): List<File?>? {
        if (isRecursive) {
            return listFilesInDirWithFilter(dir, suffix)
        }
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        val files = dir.listFiles()
        for (file in files) {
            if (file.name.toUpperCase().endsWith(suffix.toUpperCase())) {
                list.add(file)
            }
        }
        return list
    }

    /**
     * 获取目录下所有后缀名为suffix的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dirPath 目录路径
     * @param suffix  后缀名
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String?, suffix: String): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), suffix)
    }

    /**
     * 获取目录下所有后缀名为suffix的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dir    目录
     * @param suffix 后缀名
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, suffix: String): List<File?>? {
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        val files = dir.listFiles()
        for (file in files) {
            if (file.name.toUpperCase().endsWith(suffix.toUpperCase())) {
                list.add(file)
            }
            if (file.isDirectory) {
                list.addAll(listFilesInDirWithFilter(file, suffix)!!)
            }
        }
        return list
    }

    /**
     * 获取目录下所有符合filter的文件
     *
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FilenameFilter,
        isRecursive: Boolean
    ): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * 获取目录下所有符合filter的文件
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FilenameFilter,
        isRecursive: Boolean
    ): List<File?>? {
        if (isRecursive) {
            return listFilesInDirWithFilter(dir, filter)
        }
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        val files = dir.listFiles()
        for (file in files) {
            if (filter.accept(file.parentFile, file.name)) {
                list.add(file)
            }
        }
        return list
    }

    /**
     * 获取目录下所有符合filter的文件包括子目录
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String?, filter: FilenameFilter): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 获取目录下所有符合filter的文件包括子目录
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, filter: FilenameFilter): List<File?>? {
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        val files = dir.listFiles()
        for (file in files) {
            if (filter.accept(file.parentFile, file.name)) {
                list.add(file)
            }
            if (file.isDirectory) {
                list.addAll(listFilesInDirWithFilter(file, filter)!!)
            }
        }
        return list
    }

    /**
     * 获取目录下指定文件名的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dirPath  目录路径
     * @param fileName 文件名
     * @return 文件链表
     */
    fun searchFileInDir(dirPath: String?, fileName: String): List<File?>? {
        return searchFileInDir(getFileByPath(dirPath), fileName)
    }

    /**
     * 获取目录下指定文件名的文件包括子目录
     *
     * 大小写忽略
     *
     * @param dir      目录
     * @param fileName 文件名
     * @return 文件链表
     */
    fun searchFileInDir(dir: File?, fileName: String): List<File?>? {
        if (dir == null || !isDir(dir)) {
            return null
        }
        val list: MutableList<File?> = ArrayList()
        val files = dir.listFiles()
        for (file in files) {
            if (file.name.toUpperCase() == fileName.toUpperCase()) {
                list.add(file)
            }
            if (file.isDirectory) {
                list.addAll(listFilesInDirWithFilter(file, fileName)!!)
            }
        }
        return list
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @param append   是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromIS(filePath: String?, `is`: InputStream?, append: Boolean): Boolean {
        return writeFileFromIS(getFileByPath(filePath), `is`, append)
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromIS(file: File?, `is`: InputStream?, append: Boolean): Boolean {
        if (file == null || `is` == null) return false
        if (!createOrExistsFile(file)) return false
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append))
            val data = ByteArray(KB)
            var len: Int
            while (`is`.read(data, 0, KB).also { len = it } != -1) {
                os.write(data, 0, len)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            closeIO(`is`, os)
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromString(filePath: String?, content: String?, append: Boolean): Boolean {
        return writeFileFromString(getFileByPath(filePath), content, append)
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return `true`: 写入成功<br></br>`false`: 写入失败
     */
    fun writeFileFromString(file: File?, content: String?, append: Boolean): Boolean {
        if (file == null || content == null) return false
        if (!createOrExistsFile(file)) return false
        var fileWriter: FileWriter? = null
        return try {
            fileWriter = FileWriter(file, append)
            fileWriter.write(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            closeIO(fileWriter)
        }
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    fun readFile2List(filePath: String?, charsetName: String?): List<String?>? {
        return readFile2List(getFileByPath(filePath), charsetName)
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    fun readFile2List(file: File?, charsetName: String?): List<String?>? {
        return readFile2List(file, 0, 0x7FFFFFFF, charsetName)
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param ins        文件
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    fun readFile2List(ins: InputStream?, charsetName: String?): List<String?>? {
        return readFile2List(ins, 0, 0x7FFFFFFF, charsetName)
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param filePath    文件路径
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含制定行的list
     */
    fun readFile2List(filePath: String?, st: Int, end: Int, charsetName: String?): List<String?>? {
        return readFile2List(getFileByPath(filePath), st, end, charsetName)
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param file        文件
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含从start行到end行的list
     */
    fun readFile2List(file: File?, st: Int, end: Int, charsetName: String?): List<String?>? {
        if (file == null) {
            return null
        }
        if (st > end) {
            return null
        }
        var reader: BufferedReader? = null
        return try {
            var line: String?
            var curLine = 1
            val list: MutableList<String?> = ArrayList()
            reader = if (charsetName.isNullOrEmpty()) {
                BufferedReader(FileReader(file))
            } else {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
            while (reader.readLine().also { line = it } != null) {
                if (curLine > end) {
                    break
                }
                if (st <= curLine && curLine <= end) {
                    list.add(line)
                }
                ++curLine
            }
            list
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            closeIO(reader)
        }
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param file        文件
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含从start行到end行的list
     */
    fun readFile2List(ins: InputStream?, st: Int, end: Int, charsetName: String?): List<String?>? {
        if (st > end) {
            return null
        }
        var reader: BufferedReader? = null
        return try {
            var line: String?
            var curLine = 1
            val list: MutableList<String?> = ArrayList()
            reader = if (charsetName.isNullOrEmpty()) {
                BufferedReader(InputStreamReader(ins))
            } else {
                BufferedReader(InputStreamReader(ins, charsetName))
            }
            while (reader.readLine().also { line = it } != null) {
                if (curLine > end) {
                    break
                }
                if (st <= curLine && curLine <= end) {
                    list.add(line)
                }
                ++curLine
            }
            list
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            closeIO(reader)
        }
    }
    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串
     */
    fun readFile2String(filePath: String?, charsetName: String?): String? {
        return readFile2String(getFileByPath(filePath), charsetName)
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串
     */
    fun readFile2String(file: File?, charsetName: String?): String? {
        if (file == null) {
            return null
        }
        var reader: BufferedReader? = null
        return try {
            val sb = java.lang.StringBuilder()
            reader = if (charsetName.isNullOrEmpty()) {
                BufferedReader(InputStreamReader(FileInputStream(file)))
            } else {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            }
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\r\n") // windows系统换行为\r\n，Linux为\n
            }
            // 要去除最后的换行符
            sb.delete(sb.length - 2, sb.length).toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            closeIO(reader)
        }
    }

    /**
     * 指定编码按行读取文件到字符数组中
     *
     * @param filePath 文件路径
     * @return StringBuilder对象
     */
    fun readFile2Bytes(filePath: String?): ByteArray? {
        return readFile2Bytes(getFileByPath(filePath))
    }

    /**
     * 指定编码按行读取文件到字符数组中
     *
     * @param file 文件
     * @return StringBuilder对象
     */
    fun readFile2Bytes(file: File?): ByteArray? {
        return if (file == null) {
            null
        } else try {
            DLDataTool.inputStream2Bytes(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码
     */
    fun getFileCharsetSimple(filePath: String?): String? {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    fun getFileCharsetSimple(file: File?): String? {
        var p = 0
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            p = (`is`.read() shl 8) + `is`.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeIO(`is`)
        }
        return when (p) {
            0xefbb -> "UTF-8"
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * 获取文件行数
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    fun getFileLines(filePath: String?): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * 获取文件行数
     *
     * @param file 文件
     * @return 文件行数
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(KB)
            var readChars: Int
            while (`is`.read(buffer, 0, KB).also { readChars = it } != -1) {
                for (i in 0 until readChars) {
                    if (buffer[i] == '\n'.toByte()) {
                        ++count
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeIO(`is`)
        }
        return count
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileSize(filePath: String?): String? {
        return getFileSize(getFileByPath(filePath))
    }

    /**
     * 获取文件大小
     *
     * 例如：getFileSize(file, RxConstTool.MB); 返回文件大小单位为MB
     *
     * @param file 文件
     * @return 文件大小
     */
    fun getFileSize(file: File?): String? {
        return if (!isFileExists(file)) {
            ""
        } else DLDataTool.byte2FitSize(file!!.length())
    }


    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    fun closeIO(vararg closeables: Closeable?) {
        if (closeables == null) {
            return
        }
        try {
            for (closeable in closeables) {
                closeable?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param file 文件
     * @return filePath最长目录
     */
    fun getDirName(file: File?): String? {
        return if (file == null) {
            null
        } else getDirName(file.path)
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param filePath 文件路径
     * @return filePath最长目录
     */
    fun getDirName(filePath: String): String? {
        if (filePath.isNullOrEmpty()) {
            return filePath
        }
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    fun getFileName(file: File?): String? {
        return if (file == null) {
            null
        } else getFileName(file.path)
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    fun getFileName(filePath: String): String? {
        if (filePath.isNullOrEmpty()) {
            return filePath
        }
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(file: File?): String? {
        return if (file == null) {
            null
        } else getFileNameNoExtension(file.path)
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(filePath: String): String? {
        if (filePath.isNullOrEmpty()) {
            return filePath
        }
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    fun getFileExtension(file: File?): String? {
        return if (file == null) {
            null
        } else getFileExtension(file.path)
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    fun getFileExtension(filePath: String): String? {
        if (filePath.isNullOrEmpty()) {
            return filePath
        }
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) {
            ""
        } else filePath.substring(lastPoi)
    }

    /**
     * 将文件转换成uri(支持7.0)
     *
     * @param mContext
     * @param file
     * @return
     */
    fun getUriForFile(mContext: Context, file: File?): Uri? {
        var fileUri: Uri? = null
        fileUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                mContext, mContext.packageName + ".fileprovider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
        return fileUri
    }

    /**
     * 将图片文件转换成uri
     *
     * @param context
     * @param imageFile
     * @return
     */
    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(filePath),
            null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
        }
    }


    @TargetApi(19)
    fun getPathFromUri(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return ""
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeable
     */
    fun closeIOQuietly(vararg closeables: Closeable?) {
        if (closeables == null) {
            return
        }
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }

    fun file2Base64(filePath: String?): String? {
        var fis: FileInputStream? = null
        var base64String: String? = ""
        val bos = ByteArrayOutputStream()
        try {
            fis = FileInputStream(filePath)
            val buffer = ByteArray(1024 * 100)
            var count = 0
            while (fis.read(buffer).also { count = it } != -1) {
                bos.write(buffer, 0, count)
            }
            fis.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        base64String = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
        return base64String
    }

    /**
     * 传入文件名以及字符串, 将字符串信息保存到文件中
     *
     * @param strFilePath
     * @param strBuffer
     */
    fun TextToFile(strFilePath: String?, strBuffer: String?) {
        var fileWriter: FileWriter? = null
        try {
            // 创建文件对象
            val fileText = File(strFilePath)
            // 向文件写入对象写入信息
            fileWriter = FileWriter(fileText)
            // 写文件
            fileWriter.write(strBuffer)
            // 关闭
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    fun readFileByLines(fileName: String?) {
        val file = File(fileName)
        var reader: BufferedReader? = null
        try {
            println("以行为单位读取文件内容，一次读一整行：")
            reader = BufferedReader(FileReader(file))
            var tempString: String? = null
            var line = 1
            // 一次读入一行，直到读入null为文件结束
            while (reader.readLine().also { tempString = it } != null) {
                // 显示行号
                println("line?????????????????????????????????? $line: $tempString")
                val content = tempString
                line++
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e1: IOException) {
                }
            }
        }
    }


    fun exportDb2Sdcard(context: Context, path: String, realDBName: String?, exportDBName: String) {
        val filePath = context.getDatabasePath(realDBName).absolutePath
        val buffer = ByteArray(1024)
        try {
            val input = FileInputStream(File(filePath))
            val output = FileOutputStream(path + File.separator + exportDBName)
            var length: Int
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            output.flush()
            output.close()
            input.close()
            Log.i("TAG", "mv success!")
        } catch (var8: IOException) {
            Log.e("TAG", var8.toString())
        }
    }


    fun readFileFromAssets(context: Context, fileName: String):String {
        val open = context.assets.open(fileName)
        val br = BufferedReader(InputStreamReader(open, UTF8code))
        val readLine = br.readLine()
        var i = 0
        val stringBuilder = StringBuilder()
        while (readLine != null) {
            stringBuilder.append(readLine)
        }
        return stringBuilder.toString()
    }

    /**
     * 获取assets的绝对路径
     * @param fileName String
     * @return String
     */
    fun getAssetsPath(fileName: String): String {
        return "file:///android_asset/$fileName"
    }
}