package com.example.common.download.task

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.example.common.download.DownloadFileManager
import com.example.common.download.RequestManager
import com.example.common.download.callback.SubTaskCallBack
import com.example.common.download.callback.TotalTaskCallBack
import com.example.common.download.callback.TotalTaskCallBackImp
import com.example.common.download.data.SubTaskData
import com.example.common.download.data.TotalTaskData
import com.example.common.download.data.getMD5Value
import com.example.common.download.data.getTaskPath
import com.example.common.download.room.RoomManager
import com.example.common.download.switchToMainThread
import com.example.mystorehouse.ex.showKeyBord
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
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

    //记录任务 完成数量
    @Volatile
    var threadFinishCount = 0

    //任务完成数量锁
    private val taskFinishLock: Lock = ReentrantLock()

    //进度锁
    private val progressLock: Lock = ReentrantLock()

    //子任务回调集合
    private var subTaskCallBacks: MutableList<SubTaskCallBack> = mutableListOf()

    //总任务回调集合
    private var totalTaskCallBack: TotalTaskCallBack? = null

    fun downloadSuccess(filePath: String, callBackPosition: Int) {
        taskFinishLock.lock()
        try {
            threadFinishCount++
            switchToMainThread {
                //单个回调
                if (subTaskCallBacks.size > callBackPosition) {
                    subTaskCallBacks[callBackPosition].downloadSuccess(filePath)
                }
                //总回调
                if (taskData.threadCount == threadFinishCount) {
                    DownloadFileManager.getInstance().mTaskMap.remove(taskData.url)
                    RoomManager.removeCacheTask(taskData.getTaskPath())
                    totalTaskCallBack?.downloadSuccess(filePath)
                }
            }
        } finally {
            taskFinishLock.unlock()
        }

    }

    private val TAG: String = "TotalTask"

    var subTaskList: MutableList<SubTask> = mutableListOf()
    var progressMap: MutableMap<Int, Long> = mutableMapOf<Int, Long>()

    constructor(parcel: Parcel) : this() {
        threadFinishCount = parcel.readInt()
    }


    fun refreshProgress(
        taskNum: Int, downloadSize: Long, callBackPosition: Int,
        length: Float,
        singleTaskData: SubTaskData,
        progress: Float
    ) {
        progressLock.lock()
        try {
            progressMap.put(taskNum, downloadSize)
            Log.i(TAG, "refreshProgress ${taskNum} 当前执行线程: ${Thread.currentThread().id}")
            var downloadSize = 0F
            progressMap.keys.forEach {
                downloadSize += progressMap[it] ?: 0
            }
            switchToMainThread {
                totalTaskCallBack?.downloading(downloadSize / taskData.fileContentLength)
                if (subTaskCallBacks.size > callBackPosition) {
                    subTaskCallBacks[callBackPosition].downloading(length, singleTaskData, progress)
                }
            }
        } finally {
            progressLock.unlock()
        }
    }

    fun addSingleTaskCallBack(singleTaskCallBack: MutableList<SubTaskCallBack>): TotalTask {
        this.subTaskCallBacks = singleTaskCallBack
        return this
    }

    fun addTotalTaskCallBack(totalTaskCallBack: TotalTaskCallBack?): TotalTask {
        this.totalTaskCallBack = totalTaskCallBack
        return this
    }

    /**
     * 在主线程回调
     * 获取下载文件的长度
     */
    fun getContentLength(checkHasCache: Boolean, onCallBack: (contentLength: Long) -> Unit) {
        if (checkHasCache) {
            getContentLengthByCache(onCallBack)
        } else {
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
        downloadApi.getFileContentLength(taskData.url)
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
        var taskList = mutableListOf<SubTaskData?>()
        if (checkHasCache) {
            val elements = createSubTasksByCache()
            if (elements.isEmpty()) {
                taskList.addAll(createSubTasksByNew())
            } else {
                taskList.addAll(elements)
            }
        } else {
            taskList.addAll(createSubTasksByNew())
        }
        //为每个任务创建监听 并处理回调
        createSubTaskCallBacks(taskList)

    }

    private fun createSubTaskCallBacks(taskList: MutableList<SubTaskData?>) {
        for (i in 0 until taskData.threadCount) {
            taskList[i]?.also { subTaskData ->
                val subTask = SubTask(subTaskData)
                subTask.addSubTaskCallBack(object : SubTaskCallBack {
                    override fun downloadSuccess(filePath: String) {
                        Log.e(
                            TAG,
                            "downloadSuccess: 执行任务：" + "${subTask.subTaskData.taskNum} 当前执行线程: " +
                                    "${Thread.currentThread().name} + ${Thread.currentThread().id}"
                        )
                        downloadSuccess(filePath, i)
                    }

                    override fun downloading(
                        length: Float,
                        singleTaskData: SubTaskData,
                        progress: Float
                    ) {
                        Log.i(TAG, "createSubTasks downloading  当前执行线程:" +
                                    " ${Thread.currentThread().name} + ${Thread.currentThread().id}"
                        )
                        refreshProgress(singleTaskData.taskNum, singleTaskData.downloadSize,
                            i, length, singleTaskData, progress
                        )
                    }

                    override fun downloadFail(errorMsg: String) {
                        Log.i(TAG, "createSubTasks downloadFail  当前执行线程:" +
                                    " ${Thread.currentThread().name} + ${Thread.currentThread().id}")
                        switchToMainThread {
                            DownloadFileManager.getInstance().mTaskMap.remove(taskData.url)
                            totalTaskCallBack?.downloadFail(errorMsg)
                            if (subTaskCallBacks.size > i) {
                                subTaskCallBacks[i].downloadFail(errorMsg)
                            }
                        }
                    }

                    override fun downloadCancle() {
                        Log.i(TAG,
                            "createSubTasks downloadCancle  当前执行线程 ${subTask.subTaskData.taskNum}: " +
                                    "${Thread.currentThread().id}"
                        )
                    }

                    override fun downloadPause() {
                        Log.i(
                            TAG,
                            "createSubTasks downloadPause  当前执行线程 ${subTask.subTaskData.taskNum}: " +
                                    "${Thread.currentThread().id}"
                        )
                    }

                })
                subTaskList.add(subTask)
            }

        }
    }

    private fun createSubTasksByCache(): MutableList<SubTaskData?> {
        return RoomManager.getAllSubTaskData(taskData.getTaskPath())
    }

    private fun createSubTasksByNew(): MutableList<SubTaskData> {
        var taskList = mutableListOf<SubTaskData>()
        var singleTaskSize = taskData.fileContentLength / taskData.threadCount
        for (i in 0 until taskData.threadCount) {
            if (i == taskData.threadCount - 1) {
                val element = SubTaskData(
                    i, totalSize = singleTaskSize, url = taskData.url,
                    filePath = taskData.filePath, fileName = taskData.fileName,
                    startPos = i * singleTaskSize, endPos = taskData.fileContentLength
                )
                element.md5Value = element.getMD5Value()
                taskList.add(
                    element
                )
            } else {
                val element = SubTaskData(
                    i,
                    totalSize = singleTaskSize,
                    url = taskData.url,
                    filePath = taskData.filePath,
                    fileName = taskData.fileName,
                    startPos = i * singleTaskSize,
                    endPos = (i + 1) * singleTaskSize
                )
                element.md5Value = element.getMD5Value()
                taskList.add(
                    element
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
        if (!subTaskCallBacks.isNullOrEmpty()) {
            if (subTaskCallBacks.size < taskData.threadCount) {
                Log.i(TAG, "createTasks: 某些任务下载进度可能无法监控回调！")
            }
        }
    }

    fun excuteTask(useSelfExcutor: Boolean = false) {
        progressMap.clear()
        subTaskList.clear()
        Observable.create(ObservableOnSubscribe<Any> {
            //子线程
            Log.i(TAG, "excuteTask 当前执行线程: ${Thread.currentThread().id}")
            taskData.run {
                val checkHasCache = RoomManager.checkHasCache(this.getTaskPath())
                val file = File(filePath, fileName)
                if (!checkHasCache && file.exists()) {
                    FileUtils.delete(file)
                }
                getContentLength(checkHasCache) {
                    createSubTasks(checkHasCache)
                    totalTaskCallBack?.run {
                        onStartSubTask()
                    }
                    //执行
                    subTaskList.forEachIndexed { index, subTask ->
                        if (useSelfExcutor) {
                            //此处笔者配置可以使用自定义的线程池的原因是参照之前一些框架架构
                            DownloadFileManager.getInstance().mExecutor.execute(subTask.createThread())
                        } else {
                            //不使用线程池是因为，okhttp内部已经在使用线程池，没必要在加线程池进行管理
                            subTask.excute()
                        }
                    }
                }
            }

        }).subscribeOn(Schedulers.io()).subscribe({}, {
            Log.e(TAG, "excuteTask: ", it)
            totalTaskCallBack?.run {
                downloadFail(it.message ?: "下载异常！")
            }
        })
    }

    fun excuteSubTask(subTaskData: SubTaskData?) {
        subTaskData?.also { sub ->
            Observable.create(ObservableOnSubscribe<Any> {
                //子线程
                Log.i(TAG, "excuteTask 当前执行线程: ${Thread.currentThread().id}")
                taskData.run {
                    val checkHasCache = RoomManager.checkHasCache(this.getTaskPath())
                    val file = File(filePath, fileName)
                    if (!checkHasCache && file.exists()) {
                        //没有缓存  但是文件存在 则删除
                        FileUtils.delete(file)
                    }
                    //执行
                    subTaskList.filter {
                        it.subTaskData.taskNum == sub.taskNum
                    }.forEachIndexed { index, subTask ->
                        DownloadFileManager.getInstance().mExecutor.execute(subTask.createThread())
                    }
                }

            }).subscribeOn(Schedulers.io()).subscribe({}, {
                Log.e(TAG, "excuteTask: ", it)
                totalTaskCallBack?.run {
                    downloadFail(it.message ?: "下载异常！")
                }
            })
        }

    }

    fun cancle() {
        subTaskList.forEach {
            if (!it.isFinish) {
                it.isCancle = true
                Log.e(TAG, "total cancle: true")
            }
        }
        RoomManager.removeCacheTask(taskData.getTaskPath())
        RoomManager.removeAllSubTaskData(taskData.getTaskPath())
    }

    fun pause() {
        subTaskList.forEach {
            if ((!it.isFinish)) {
                it.isPause = true
                Log.e(TAG, "total pause: true")
            }
        }
        RoomManager.saveCacheTask(taskData.getTaskPath(), taskData)
    }


    fun cancleSubTask(subNum: Int) {
        subTaskList.filter {
            it.subTaskData.taskNum == subNum
        }.forEachIndexed { index, subTask ->
            if (!subTask.isFinish) {
                subTask.isCancle = true
                Log.e(TAG, "sub cancle: true")
            }
        }
    }

    fun pauseSubTask(subNum: Int) {
        subTaskList.filter {
            it.subTaskData.taskNum == subNum
        }.forEachIndexed { index, subTask ->
            if (!subTask.isFinish) {
                subTask.isPause = true
                Log.e(TAG, "sub isPause: true")
            }
        }
        RoomManager.saveCacheTask(taskData.getTaskPath(), taskData)
    }

    class Builder {

        private var taskData: TotalTaskData = TotalTaskData()
        private var singleTaskCallBack: MutableList<SubTaskCallBack> = mutableListOf()
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
            taskData.md5Value = taskData.getMD5Value()
            totalTask.taskData = taskData
            if (RoomManager.checkHasCache(taskData.getTaskPath())) {
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
            if (threadCount > 8) {
                //线程不易太多
                taskData.threadCount = 8
            } else if (threadCount <= 0) {
                throw java.lang.IllegalArgumentException("下载线程数必须大于0！")
            } else {
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

        fun addSingleTaskCallBack(vararg singleTaskCallBack: SubTaskCallBack): Builder {
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