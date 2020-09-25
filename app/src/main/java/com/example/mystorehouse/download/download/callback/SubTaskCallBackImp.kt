package com.example.common.download.callback

import com.example.common.download.data.SubTaskData

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 单个任务下载回调监听
 *     PackageName: com.example.common.download
 */
abstract class SubTaskCallBackImp : SubTaskCallBack{

    override fun downloadCancle() {
    }

    override fun downloadFail(errorMsg: String) {
    }

    override fun downloadPause() {
    }

    override fun downloadSuccess(filePath: String) {
    }

    override fun downloading(length: Float, subTaskData: SubTaskData,progress: Float) {

    }

}