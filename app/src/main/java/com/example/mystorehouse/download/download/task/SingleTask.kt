package com.example.common.download.task

import com.example.common.download.Constants
import com.example.common.download.RequestManager
import com.example.common.download.api.DownloadApi
import com.example.common.download.callback.SingleTaskCallBack
import com.example.common.download.data.SingleTaskData
import com.example.common.download.room.RoomManager
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 下载任务
 *     PackageName: com.example.common.download.task
 */
class SingleTask() : BaseTask {
    lateinit var singleTaskData: SingleTaskData
    private var singleTaskCallBack: SingleTaskCallBack? = null
    private var TAG = "SingleTask"

    fun addSingleTaskCallBack(singleTaskCallBack: SingleTaskCallBack?) {
        this.singleTaskCallBack = singleTaskCallBack
    }

    constructor(singleTaskData: SingleTaskData) : this() {
        thread = createThread()
        this.singleTaskData = singleTaskData
    }

    constructor(singleTaskData: SingleTaskData, threadName: String) : this() {
        thread = createThread(threadName)
        this.singleTaskData = singleTaskData
    }

    @Volatile
    var isPause = false
    @Volatile
    var isFinish = false
    lateinit var thread: Thread

    override fun createThread(): Thread {
        return object : Thread() {
            override fun run() {
                super.run()
                //执行具体的下载任务
                val downloadApi = RequestManager.getDownloadApi()
                downloadApi.downLoadFile(
                    singleTaskData.url,
                    "bytes=" + singleTaskData.startPos.toString() + "-" + singleTaskData.endPos.toString()
                )
                    .subscribe({ response ->
                        if (response?.byteStream() != null) {
                            val inputStream: InputStream = response?.byteStream()
                            val file = File(singleTaskData.filePath, singleTaskData.fileName)
                            if (!file.exists()) {
                                if (file.isFile) {
                                    file.createNewFile()
                                }
                            }
                            var raf = RandomAccessFile(file, "rwd")
                            //找到上一次的点
                            raf.seek(singleTaskData.startPos)
                            val bytes = ByteArray(1024 * 2)
                            var len: Int
                            while (inputStream.read(bytes).also { len = it } != -1) {
                                raf.write(bytes, 0, len)
                                //保存下载的数据
                                singleTaskData.downloadSize = singleTaskData.downloadSize + len
                                singleTaskCallBack?.downloading(
                                    singleTaskData.downloadSize,
                                    singleTaskData
                                )

                                //暂停
                                if (isPause) {
                                    singleTaskCallBack?.downloadCancle()
                                    RoomManager.saveOrUpdateDownloadTask(singleTaskData)
                                    break
                                }
                            }
                            isFinish = true
                            singleTaskCallBack?.downloadSuccess(file.absolutePath)
                        } else {
                            throw RuntimeException("cannot get ResponseBody ")
                        }
                    }, {
                        singleTaskCallBack?.downloadFail(if (it.message != null)
                            "任务${singleTaskData.taskNum},下载出错：" + it.message else "任务${singleTaskData.taskNum}下载失败！")
                    })
            }
        }

    }

    override fun createThread(threadName: String): Thread {
        return Thread(Runnable {
            val downloadApi =
                RequestManager.createRetrofit(Constants.BASE_URL).create(DownloadApi::class.java)
            downloadApi.downLoadFile(
                Constants.MOBILE_ASSISTANT_PATH,
                "bytes=" + singleTaskData.startPos.toString() + "-" + singleTaskData.endPos.toString()
            )
                .subscribe({ response ->
                    if (response?.byteStream() != null) {
                        val inputStream: InputStream = response?.byteStream()
                        val file = File(singleTaskData.filePath, singleTaskData.fileName)
                        var raf = RandomAccessFile(file, "rwd")
                        //找到上一次的点
                        raf.seek(singleTaskData.startPos)
                        val bytes = ByteArray(1024 * 2)
                        var len: Int
                        while (inputStream.read(bytes).also { len = it } != -1) {
                            raf.write(bytes, 0, len)
                            //保存下载的数据
                            singleTaskData.downloadSize = singleTaskData.downloadSize + len
                            singleTaskCallBack?.downloading(
                                len.toFloat(),
                                singleTaskData
                            )

                            //暂停
                            if (isPause) {
                                singleTaskCallBack?.downloadCancle()
                                RoomManager.saveOrUpdateDownloadTask(singleTaskData)
                                break
                            }
                        }
                        inputStream.close()
                        raf.close()
                        isFinish = true
                        singleTaskCallBack?.downloadSuccess(file.absolutePath)
                    } else {
                        throw RuntimeException("cannot get ResponseBody ")
                    }
                }, {
                    singleTaskCallBack?.downloadFail(
                        if (it.message != null)
                            "任务${singleTaskData.taskNum},下载出错：" + it.message else "任务${singleTaskData.taskNum}下载失败！"
                    )
                })
        }, threadName)
    }

}