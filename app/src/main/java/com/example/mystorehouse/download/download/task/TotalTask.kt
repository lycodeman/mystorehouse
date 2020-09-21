package com.example.common.download.task

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.example.common.download.DownloadFileManager
import com.example.common.download.RequestManager
import com.example.common.download.callback.SingleTaskCallBack
import com.example.common.download.callback.TotalTaskCallBack
import com.example.common.download.callback.TotalTaskCallBackImp
import com.example.common.download.data.SingleTaskData
import com.example.common.download.data.TotalTaskData
import com.example.common.download.data.getTaskPath
import com.example.common.download.room.RoomManager
import com.example.common.download.switchToMainThread
import com.example.common.mvp.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 下载任务
 *     PackageName: com.example.common.download.task
 */
class TotalTask private constructor() : Parcelable {

    lateinit var taskData: TotalTaskData

    @Volatile
    var threadFinishCount = 0
    private val taskLock: Lock = ReentrantLock()
    private val progressLock: Lock = ReentrantLock()
    private var singleTaskCallBack: MutableList<SingleTaskCallBack> = mutableListOf()
    private var totalTaskCallBack: TotalTaskCallBack? = null

    fun singleTaskFinish() {
        taskLock.lock()
        try {
            threadFinishCount++
        } finally {
            taskLock.unlock()
        }
    }

    private val TAG: String = "TotalTask"

    var subTaskList: MutableList<SingleTask> = mutableListOf()
    var progressMap: MutableMap<Int, Float> = mutableMapOf<Int, Float>()

    constructor(parcel: Parcel) : this() {
        threadFinishCount = parcel.readInt()
    }


    fun refreshProgress(taskNum: Int, downloadSize: Float) {
        progressLock.lock()
        try {
            progressMap.put(taskNum, downloadSize)
            Log.i(TAG, "refreshProgress ${taskNum} 当前执行线程: ${Thread.currentThread().id}")
            switchToMainThread {
                var downloadSize = 0F
                progressMap.keys.forEach {
                    downloadSize += progressMap[it] ?: 0F
                }
                totalTaskCallBack?.downloading(downloadSize / taskData.fileContentLength)
            }
        } finally {
            progressLock.unlock()
        }
    }

    fun addSingleTaskCallBack(singleTaskCallBack: MutableList<SingleTaskCallBack>) {
        this.singleTaskCallBack = singleTaskCallBack
    }

    fun addTotalTaskCallBack(totalTaskCallBack: TotalTaskCallBackImp?) {
        this.totalTaskCallBack = totalTaskCallBack
    }

    /**
     * 在主线程回调
     * 获取下载文件的长度
     */
    fun getContentLength(onCallBack: (contentLength: Long) -> Unit) {
        if (RoomManager.checkHasCache(taskData.getTaskPath())){
            getContentLengthByCache(onCallBack)
        }else {
            getContentLengthByNetWork(onCallBack)
        }
    }

    private fun getContentLengthByCache(onCallBack: (contentLength: Long) -> Unit) {
        RoomManager.getCacheTaskData(taskData.getTaskPath())?.also { totalTaskData ->
            taskData.fileContentLength = totalTaskData.fileContentLength
            totalTaskCallBack?.run {
                this.onFileContentLength(totalTaskData.fileContentLength)
            }
            if (totalTaskData.fileContentLength <= 0) {
                totalTaskCallBack?.downloadFail(
                    "获取文件content-length异常，content-length: ${totalTaskData.fileContentLength}"
                )
            } else {
                onCallBack.invoke(totalTaskData.fileContentLength)
            }
        }
    }

    private fun getContentLengthByNetWork(onCallBack: (contentLength: Long) -> Unit) {
        val downloadApi = RequestManager.getDownloadApi()
        //提前获取文件的大小
        totalTaskCallBack?.run {
            switchToMainThread {
                this.startDownload()
            }
        }
        downloadApi.getFileContentLength(taskData.url).compose(RxUtils.defaultTransformer())
            .subscribe({
                val contentLength = it.contentLength()
                taskData.fileContentLength = contentLength
                totalTaskCallBack?.run {
                    this.onFileContentLength(contentLength)
                }
                if (contentLength <= 0) {
                    totalTaskCallBack?.downloadFail("获取文件content-length异常，content-length: $contentLength")
                } else {
                    onCallBack.invoke(contentLength)
                }

            }, {
                Log.e(TAG, "downlaodTask: ${it.message}}")
            })
    }

    fun createSubTasks(checkHasCache: Boolean) {
        //校验任务
        checkTaskData()

        //根据线程数创建单个任务集合
        var taskList = mutableListOf<SingleTaskData?>()
        if (checkHasCache){
            taskList.addAll(createSubTasksByCache())
        }else {
            taskList.addAll(createSubTasksByNew())
        }
        //为每个任务创建监听 并处理回调
        createSubTaskCallBacks(taskList)

    }

    private fun createSubTaskCallBacks(taskList: MutableList<SingleTaskData?>) {
        for (i in 0 until taskData.threadCount) {
            taskList[i]?.also {subTaskData ->
                val singleTask = SingleTask(subTaskData,subTaskData.fileName+"_" + subTaskData.taskNum)
                singleTask.addSingleTaskCallBack(object : SingleTaskCallBack{
                    override fun downloadSuccess(filePath: String) {
                        singleTaskFinish()
                        Log.e(TAG, "downloadSuccess: 执行任务：" +
                                "${singleTask.singleTaskData.taskNum} 当前执行线程: ${singleTask.thread.id}")
                        if (taskData.threadCount == threadFinishCount) {
                            switchToMainThread {
                                DownloadFileManager.getInstance().mTaskMap.remove(taskData.url)
                                totalTaskCallBack?.downloadSuccess(filePath)
                            }
                        }
                        switchToMainThread {
                            if (singleTaskCallBack.size > i) {
                                singleTaskCallBack[i].downloadSuccess(filePath)
                            }
                        }
                    }

                    override fun downloading(length: Float, singleTaskData: SingleTaskData) {
                        Log.i(TAG, "createSubTasks downloading  当前执行线程: ${Thread.currentThread().id}")
                        refreshProgress(singleTaskData.taskNum,singleTaskData.downloadSize)
                        if (singleTaskCallBack.size > i){
                            singleTaskCallBack[i].downloading(length, singleTaskData)
                        }
                    }

                    override fun downloadFail(errorMsg: String) {
                        Log.i(TAG, "createSubTasks downloading  当前执行线程: ${Thread.currentThread().id}")
                        switchToMainThread {
                            DownloadFileManager.getInstance().mTaskMap.remove(taskData.url)
                            totalTaskCallBack?.downloadFail(errorMsg)
                            if (singleTaskCallBack.size > i){
                                singleTaskCallBack[i].downloadFail(errorMsg)
                            }
                        }
                    }

                    override fun downloadCancle() {
                        Log.i(TAG, "createSubTasks downloadCancle  当前执行线程 ${singleTask.singleTaskData.taskNum}: ${Thread.currentThread().id}")
                    }

                })
                if (singleTaskCallBack.size - 1 >= i) {
                    singleTask.addSingleTaskCallBack(singleTaskCallBack[i])
                }
                subTaskList.add(singleTask)
            }

        }
    }

    private fun createSubTasksByCache(): MutableList<SingleTaskData?> {
        return RoomManager.getAllSubTaskData(taskData.getTaskPath())
    }

    private fun createSubTasksByNew(): MutableList<SingleTaskData> {
        var taskList = mutableListOf<SingleTaskData>()
        var singleTaskSize = taskData.fileContentLength / taskData.threadCount
        for (i in 0 until taskData.threadCount) {
            if (i == taskData.threadCount - 1) {
                taskList.add(
                    SingleTaskData(
                        i, totalSize = singleTaskSize, url = taskData.url,
                        filePath = taskData.filePath, fileName = taskData.fileName,
                        startPos = i * singleTaskSize, endPos = taskData.fileContentLength
                    )
                )
            } else {
                taskList.add(
                    SingleTaskData(
                        i,
                        totalSize = singleTaskSize,
                        url = taskData.url,
                        filePath = taskData.filePath,
                        fileName = taskData.fileName,
                        startPos = i * singleTaskSize,
                        endPos = (i + 1) * singleTaskSize
                    )
                )
            }
        }
        return taskList
    }

    private inline fun checkTaskData() {
        if (taskData.fileContentLength == -1L) {
            throw IllegalArgumentException("下载任务之前，请先获取文件总长！")
            return
        }

        if (taskData.threadCount <= 0) {
            throw IllegalArgumentException("至少有1个下载任务线程！")
            return
        }
        if (!singleTaskCallBack.isNullOrEmpty()) {
            if (singleTaskCallBack.size < taskData.threadCount) {
                Log.i(TAG, "createTasks: 某些任务下载进度可能无法监控回调！")
            }
        }
    }

    fun excuteTask() {
        progressMap.clear()
        subTaskList.clear()
        Observable.create(ObservableOnSubscribe<Any> {
            //子线程
            Log.i(TAG, "excuteTask 当前执行线程: ${Thread.currentThread().id}")
            taskData.run {
                val checkHasCache = RoomManager.checkHasCache(this.getTaskPath())
                getContentLength {
                    //主线程
                    createSubTasks(checkHasCache)
                    totalTaskCallBack?.run {
                        onStartSubTask()
                    }
                    //执行
                    subTaskList.forEachIndexed { index, singleTask ->
                        DownloadFileManager.getInstance().mExecutor.execute(singleTask.thread)
                    }
                }
            }

        }).subscribeOn(Schedulers.io()).subscribe({},{
            Log.e(TAG, "excuteTask: ",it )
            totalTaskCallBack?.run {
                downloadFail(it.message?:"下载异常！")
            }
        })
    }

    fun cancle() {
        synchronized (this){
            subTaskList.forEach {
                if ((!it.isFinish)){
                    it.isPause = true
                    RoomManager.getMMKVInstance()?.decodeBool(taskData.fileName,true)
                    Log.e(TAG, "cancle: true")
                }
            }
        }

    }

    fun pause() {
        synchronized (this) {
            subTaskList.forEach {
                if ((!it.isFinish)) {
                    it.isPause = true
                    RoomManager.getMMKVInstance()?.decodeBool(taskData.fileName,true)
                    Log.e(TAG, "cancle: true")
                }
            }
        }
    }


    class Builder {

        private var taskData: TotalTaskData = TotalTaskData()
        private var singleTaskCallBack: MutableList<SingleTaskCallBack> = mutableListOf()
        private var totalTaskCallBack: TotalTaskCallBackImp? = null
        private var downloadFileManager = DownloadFileManager.getInstance()

        fun build(): TotalTask? {
            if (TextUtils.isEmpty(taskData.filePath)) {
                throw java.lang.IllegalArgumentException("配置文件存储路径！")
                return null
            }
            if (TextUtils.isEmpty(taskData.fileName)) {
                throw java.lang.IllegalArgumentException("配置存储的文件名！")
                return null
            }
            if (TextUtils.isEmpty(taskData.url)) {
                throw java.lang.IllegalArgumentException("文件下载的url不能为空！")
                return null
            }
            val totalTask = TotalTask()
            totalTask.taskData = taskData
            if (RoomManager.checkHasCache(taskData.getTaskPath())){
                totalTask.taskData = RoomManager.getCacheTaskData(taskData.getTaskPath())!!
            }
            totalTask.addSingleTaskCallBack(singleTaskCallBack)
            totalTask.addTotalTaskCallBack(totalTaskCallBack)
            downloadFileManager.mTaskMap.put(taskData.url, totalTask)
            return totalTask
        }

        fun filePath(filePath: String): Builder {
            taskData.filePath = filePath
            return this
        }

        fun fileName(fileName: String): Builder {
            taskData.fileName = fileName
            return this
        }

        fun fileContentLength(fileContentLength: Long): Builder {
            taskData.fileContentLength = fileContentLength
            return this
        }

        fun url(url: String): Builder {
            taskData.url = url
            return this
        }

        fun threadCount(threadCount: Int): Builder {
            if (threadCount > 8){
                //线程不易太多
                taskData.threadCount = 8
            }else if (threadCount <= 0){
                throw java.lang.IllegalArgumentException("下载线程数必须大于0！")
            }else {
                taskData.threadCount = threadCount
            }
            return this
        }

        fun getFilePath(filePath: String): String {
            return taskData.filePath
        }

        fun getFileName(): String {
            return taskData.fileName
        }

        fun getFileContentLength(): Long {
            return taskData.fileContentLength
        }

        fun getUrl(): String {
            return taskData.url
        }

        fun addSingleTaskCallBack(vararg singleTaskCallBack: SingleTaskCallBack): Builder {
            this.singleTaskCallBack = singleTaskCallBack.toMutableList()
            return this
        }

        fun addTotalTaskCallBack(taskCallBack: TotalTaskCallBackImp): Builder {
            this.totalTaskCallBack = taskCallBack
            return this
        }

        fun getThreadCount(): Int {
            return taskData.threadCount
        }

    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<TotalTask> {
        override fun createFromParcel(parcel: Parcel): TotalTask {
            return TotalTask(parcel)
        }

        override fun newArray(size: Int): Array<TotalTask?> {
            return arrayOfNulls(size)
        }
    }


}