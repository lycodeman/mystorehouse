package com.example.common.download

import com.example.common.download.task.TotalTask

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 校验任务是否完成
 *     PackageName: com.example.common.download
 */
class CheckTaskImp(var totalTask: TotalTask) : CheckTask{

    override fun checkAllTaskFinish(): Boolean {
        var isFinish = true
        totalTask.subTaskList.forEachIndexed { index, singleTask ->
            if (!singleTask.isFinish){
                isFinish = false
            }
        }
        return isFinish
    }


}