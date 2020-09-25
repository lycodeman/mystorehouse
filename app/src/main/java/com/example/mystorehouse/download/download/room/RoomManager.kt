package com.example.common.download.room

import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.example.common.download.data.SubTaskData
import com.example.common.download.data.TotalTaskData
import com.example.common.download.data.getTaskPath
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

    fun saveOrUpdateDownloadTask(singleTaskData: SubTaskData) {
        //将下载的参数 存入数据库
        Log.e(TAG, "saveOrUpdateDownloadTask: cancle thread" )
        //存储下载的进度
        savSubTaskData(singleTaskData)
    }

    /**
     * return 是否有缓存
     */
    fun checkHasCache(filePath: String): Boolean{
        val cacheTask = getCacheTaskData(filePath)
        var hasCache = false
        cacheTask?.run {
            hasCache = File(this.filePath, fileName).exists() && hasCacheTask(filePath)
            if (!hasCache){
                getMMKVInstance()?.decodeBool(filePath,false)
            }
        }
        return hasCache
    }

    fun hasCacheTask(filePath: String): Boolean{
        return getCacheTaskData(filePath) != null
    }

    /**
     * 存入对应任务数据
     */
    fun saveCacheTask(filePath: String,totalTaskData: TotalTaskData){
        getMMKVInstance()?.encode(FileUtils.getFileMD5ToString(filePath),totalTaskData)
    }

    /**
     * 移除缓存任务
     */
    fun removeCacheTask(taskPath: String) {
        getMMKVInstance()?.removeValueForKey(FileUtils.getFileMD5ToString(taskPath))
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
    fun getAllSubTaskData(filePath: String): MutableList<SubTaskData?> {
        var subTaskList = mutableListOf<SubTaskData?>()
        getCacheTaskData(filePath)?.run {
            if (threadCount>0){
                for (i in 0 until threadCount) {
                    var subTaskData = getMMKVInstance()?.decodeParcelable(
                        FileUtils.getFileMD5ToString(filePath+ "_sub_$i"),SubTaskData::class.java)
                    subTaskList.add(subTaskData)
                }

            }
        }
        return subTaskList
    }

    /**
     * 移除所有子任务
     */
    fun removeAllSubTaskData(filePath: String){
        getCacheTaskData(filePath)?.also { totalTaskData ->
            if (totalTaskData.threadCount>0){
                for (i in 0 until totalTaskData.threadCount) {
                    var subTaskData = getSubTaskData(FileUtils.getFileMD5ToString(filePath+ "_sub_$i"))
                    subTaskData?.run { removeSubTaskData(this) }
                }
            }
        }
    }

    /**
     * 缓存子任务数据
     */
    fun savSubTaskData(subTaskData:SubTaskData){
        getMMKVInstance()?.encode(
            FileUtils.getFileMD5ToString(subTaskData.getTaskPath()),subTaskData)
    }

    fun getSubTaskData(filePath:String):SubTaskData?{
        return getMMKVInstance()?.decodeParcelable(
            FileUtils.getFileMD5ToString(filePath),SubTaskData::class.java)
    }

    fun removeSubTaskData(subTaskData:SubTaskData){
        getMMKVInstance()?.removeValueForKey(
            FileUtils.getFileMD5ToString(subTaskData.filePath+ "_sub_${subTaskData.taskNum}"))
    }

    /**
     * 缓存所有子任务数据
     */
    fun savAllSubTaskData(subTaskDatas:MutableList<SubTaskData>){
        for (subTaskData in subTaskDatas) {
            savSubTaskData(subTaskData)
        }
    }


}