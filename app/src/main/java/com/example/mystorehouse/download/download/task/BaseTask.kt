package com.example.common.download.task

import com.example.common.download.data.TotalTaskData
import com.example.common.download.data.SingleTaskData

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   :
 *     PackageName: com.example.common.download.task
 */
interface BaseTask {

    fun createThread() : Thread

    fun createThread(threadName: String) : Thread

}