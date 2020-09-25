package com.example.common.download.task

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   :
 *     PackageName: com.example.common.download.task
 */
interface BaseTask {

    fun createThread() : Thread

    fun excute()

}