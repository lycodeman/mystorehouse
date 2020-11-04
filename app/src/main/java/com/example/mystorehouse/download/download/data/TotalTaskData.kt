package com.example.common.download.data

import android.os.Parcelable
import com.blankj.utilcode.util.FileUtils
import kotlinx.android.parcel.Parcelize
import java.io.File

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 下载文件参数 默认一个线程下载
 *     PackageName: com.example.common.download.data
 */
@Parcelize
class TotalTaskData(var filePath: String = "", var fileName: String = "", var url: String = "", var fileContentLength: Long = -1,
                    var threadCount: Int = 1,var md5Value: String = "") : Parcelable

fun TotalTaskData.getTaskPath(): String {
    return this.filePath+File.separator+this.fileName
}

fun TotalTaskData.  getMD5Value(): String{
    return FileUtils.getFileMD5ToString(filePath)
}