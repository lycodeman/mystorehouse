package com.example.common.download.callback

import com.example.common.download.data.SubTaskData

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 单个任务下载回调监听
 *     PackageName: com.example.common.download
 */
interface SubTaskCallBack {
    fun downloadSuccess(filePath: String)
    fun downloading(
        length: Float,
        singleTaskData: SubTaskData,
        progress: Float
    )
    fun downloadFail(errorMsg: String)
    fun downloadCancle()
    fun downloadPause()
}