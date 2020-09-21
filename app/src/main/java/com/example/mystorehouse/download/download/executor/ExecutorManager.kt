package com.example.common.download.executor

import com.blankj.utilcode.util.ToastUtils
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   :
 *     PackageName: com.example.common.download.executor
 */
object ExecutorManager {

    /**
     * 创建一个和cpu核心数相关的线程池
     */
    fun createExecutor(threadCount: Int): ThreadPoolExecutor {
        var cpuAvailableCore = Runtime.getRuntime().availableProcessors()
        if (threadCount > cpuAvailableCore*2 + 1){
            cpuAvailableCore = threadCount
        }
        return ThreadPoolExecutor(threadCount,cpuAvailableCore*2+1,0,
        TimeUnit.SECONDS, LinkedBlockingDeque<Runnable>(128))
    }

    /**
     * 创建一个和cpu核心数相关的线程池
     */
    fun createExecutor(): ThreadPoolExecutor {
        var cpuAvailableCore = Runtime.getRuntime().availableProcessors()
        return ThreadPoolExecutor(cpuAvailableCore,cpuAvailableCore*2+1,0,
        TimeUnit.SECONDS, LinkedBlockingDeque<Runnable>(128))
    }
}