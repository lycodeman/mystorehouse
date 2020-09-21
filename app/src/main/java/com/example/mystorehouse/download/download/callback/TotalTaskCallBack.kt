package com.example.common.download.callback

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 总任务下载回调监听
 *     PackageName: com.example.common.download
 */
interface TotalTaskCallBack {
    fun downloadSuccess(filePtah: String)
    fun downloading(progress: Float)
    fun downloadFail(errorMsg: String)
    fun downloadCancle()
    fun startDownload()
    fun onFileContentLength(contentLength: Long)
    fun onStartSubTask()
}