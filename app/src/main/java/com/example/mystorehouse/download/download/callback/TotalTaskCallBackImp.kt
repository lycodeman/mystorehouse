package com.example.common.download.callback

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 总任务下载回调监听
 *     PackageName: com.example.common.download
 */
abstract class TotalTaskCallBackImp() : TotalTaskCallBack {
    override fun downloadSuccess(filePtah: String) {

    }

    override fun downloading(progress: Float) {
    }

    override fun downloadFail(errorMsg: String) {
    }

    override fun downloadCancle() {
    }

    override fun startDownload() {
    }

    override fun onFileContentLength(contentLength: Long) {
    }

    override fun onStartSubTask() {
    }

}