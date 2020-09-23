package com.example.common.download.data

import android.os.Parcelable
import com.example.common.download.callback.SingleTaskCallBack
import kotlinx.android.parcel.Parcelize
import java.io.File

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 单个任务下载描述 需要下载的大小 已经下载的大小 下载起始位置 下载结束位置
 *     PackageName: com.example.common.download.data
 */
@Parcelize
class SingleTaskData(var taskNum: Int = -1, var totalSize: Long = 0, var downloadSize: Float = 0F, var url: String = "",
                     var startPos: Long, var endPos: Long, var filePath: String = "", var fileName: String = ""
) : Parcelable

fun SingleTaskData.getTaskPath(): String {
    return this.filePath+ File.separator+this.fileName + "_sub_${taskNum}"
}