package com.example.common.download.callback

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 总任务下载回调监听 均是在主线程进行回调
 *     PackageName: com.example.common.download
 */
interface TotalTaskCallBack {
    //下载成功 一次回调
    fun downloadSuccess(filePtah: String)
    //正在下载
    fun downloading(progress: Float)
    //下载失败 可能会多次回调
    fun downloadFail(errorMsg: String)
    //取消下载
    fun downloadCancle()
    //开始下载 点执行excute的时候的回调
    fun startDownload()
    //获取到content length的回调
    fun onFileContentLength(contentLength: Long)
    //开始执行子任务
    fun onStartSubTask()
}