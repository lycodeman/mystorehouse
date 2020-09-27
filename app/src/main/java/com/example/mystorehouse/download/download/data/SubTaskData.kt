package com.example.common.download.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 单个任务下载描述 需要下载的大小 已经下载的大小 下载起始位置 下载结束位置
 *     PackageName: com.example.common.download.data
 */
@Parcelize
class SubTaskData(var taskNum: Int = -1, var totalSize: Long = 0, var downloadSize: Long = 0, var url: String = "",
                  var startPos: Long, var endPos: Long, var filePath: String = "", var fileName: String = ""
) : Parcelable {
    override fun toString(): String {
        return "var taskNum: Int = $taskNum, var totalSize: Long =${totalSize}0, var downloadSize: Long = $downloadSize, var url: String = $url" +
                "                  var startPos = $startPos , var endPos=$endPos, var filePath: String = $filePath, var fileName: String = $fileName"
    }
}

fun SubTaskData.getTaskPath(): String {
    return this.filePath+ File.separator+this.fileName
}