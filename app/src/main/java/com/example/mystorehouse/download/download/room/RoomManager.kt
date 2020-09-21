package com.example.common.download.room

import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.example.common.download.data.SingleTaskData
import com.example.common.download.data.TotalTaskData
import com.example.common.download.task.TotalTask
import com.tencent.mmkv.MMKV
import java.io.File

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 数据库管理类
 *     PackageName: com.example.common.download.room
 */
object RoomManager {

    private var TAG = "RoomManager"

    fun getMMKVInstance(): MMKV? {
        return MMKV.mmkvWithID("download_file")
    }

    fun saveOrUpdateDownloadTask(singleTaskData: SingleTaskData) {
        //将下载的参数 存入数据库
        Log.e(TAG, "saveOrUpdateDownloadTask: cancle thread" )
        //存储下载的进度
        getMMKVInstance()?.encode(singleTaskData.fileName +'_'+ singleTaskData.taskNum,singleTaskData.downloadSize)
    }

    /**
     * return 是否有缓存
     */
    fun checkHasCache(filePath: String): Boolean{
        val cacheTask = getCacheTaskData(filePath)
        var hasCache = false
        cacheTask?.run {
            hasCache = File(this.filePath, fileName).exists() && MMKV.defaultMMKV()
                .decodeBool(fileName)
            if (!hasCache){
                getMMKVInstance()?.decodeBool(fileName,false)
            }
        }
        return hasCache
    }

    /**
     * 存入对应任务数据
     */
    fun saveCacheTask(filePath: String,totalTaskData: TotalTaskData){
        getMMKVInstance()?.encode(FileUtils.getFileMD5ToString(filePath),totalTaskData)
    }

    /**
     * 获取任务数据
     */
    fun getCacheTaskData(filePath: String): TotalTaskData? {
        return getMMKVInstance()?.decodeParcelable(FileUtils.getFileMD5ToString(filePath),TotalTaskData::class.java)
    }

    /**
     * 获取所有子任务数据
     */
    fun getAllSubTaskData(filePath: String): MutableList<SingleTaskData?> {
        var subTaskList = mutableListOf<SingleTaskData?>()
        val cacheTaskData = getCacheTaskData(filePath)?.run {
            if (threadCount>0){
                for (i in 0 until threadCount) {
                    var subTaskData = getMMKVInstance()?.decodeParcelable(
                        FileUtils.getFileMD5ToString(filePath+ "_sub_$i"),SingleTaskData::class.java)
                    subTaskList.add(subTaskData)
                }

            }
        }
        return subTaskList
    }

    /**
     * 缓存子任务数据
     */
    fun savSubTaskData(subTaskData:SingleTaskData){
        getMMKVInstance()?.encode(
            FileUtils.getFileMD5ToString(subTaskData.filePath+ "_sub_${subTaskData.taskNum}"),subTaskData)
    }

    /**
     * 缓存所有子任务数据
     */
    fun savAllSubTaskData(subTaskDatas:MutableList<SingleTaskData>){
        for (subTaskData in subTaskDatas) {
            savSubTaskData(subTaskData)
        }
    }

}