package com.example.common.download.task

import com.example.common.download.Constants
import com.example.common.download.RequestManager
import com.example.common.download.api.DownloadApi
import com.example.common.download.callback.SubTaskCallBack
import com.example.common.download.data.SubTaskData
import com.example.common.download.room.RoomManager
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 下载子任务
 *     PackageName: com.example.common.download.task
 */
class SubTask() : BaseTask {
    lateinit var subTaskData: SubTaskData
    private var subTaskCallBack: SubTaskCallBack? = null
    private var TAG = "SingleTask"

    fun addSubTaskCallBack(subTaskCallBack: SubTaskCallBack?) {
        this.subTaskCallBack = subTaskCallBack
    }

    constructor(singleTaskData: SubTaskData) : this() {
        this.subTaskData = singleTaskData
    }

    @Volatile
    var isPause = false

    @Volatile
    var isCancle = false

    @Volatile
    var isFinish = false

    override fun createThread(): Thread {
        var threadName = subTaskData.fileName + "_" + subTaskData.taskNum
        return Thread(Runnable {
            val downloadApi =
                RequestManager.createRetrofit(Constants.BASE_URL).create(DownloadApi::class.java)
            downloadApi.downLoadFile(
                Constants.MOBILE_ASSISTANT_PATH,
                "bytes=" + subTaskData.startPos.toString() + "-" + subTaskData.endPos.toString()
            )
                .subscribe({ response ->
                    if (response?.byteStream() != null) {
                        val inputStream: InputStream = response?.byteStream()
                        val file = File(subTaskData.filePath, subTaskData.fileName)
                        var raf = RandomAccessFile(file, "rwd")
                        //找到上一次的点
                        raf.seek(subTaskData.startPos)
                        val bytes = ByteArray(1024 * 2)
                        var len: Int
                        while (inputStream.read(bytes).also { len = it } != -1) {
                            raf.write(bytes, 0, len)
                            //刷新进度
                            subTaskData.downloadSize = subTaskData.downloadSize + len
                            subTaskCallBack?.downloading(
                                len.toFloat(),
                                subTaskData,
                                subTaskData.downloadSize / subTaskData.totalSize
                            )
                            //取消
                            if (isCancle) {
                                subTaskCallBack?.downloadCancle()
                                break
                            }
                            //暂停
                            if (isPause) {
                                subTaskCallBack?.downloadPause()
                                RoomManager.saveOrUpdateDownloadTask(subTaskData)
                                break
                            }
                        }
                        inputStream.close()
                        raf.close()
                        isFinish = true
                        if (!isCancle && !isPause) {
                            subTaskCallBack?.downloadSuccess(file.absolutePath)
                        }
                    } else {
                        throw RuntimeException("cannot get ResponseBody ")
                    }
                }, {
                    subTaskCallBack?.downloadFail(
                        if (it.message != null)
                            "任务${subTaskData.taskNum},下载出错：" + it.message else "任务${subTaskData.taskNum}下载失败！"
                    )
                })
        }, threadName)

    }

    override fun excute() {
        val downloadApi =
            RequestManager.createRetrofit(Constants.BASE_URL).create(DownloadApi::class.java)
        downloadApi.downLoadFile(
            Constants.MOBILE_ASSISTANT_PATH,
            "bytes=" + subTaskData.startPos.toString() + "-" + subTaskData.endPos.toString()
        )
            .subscribe({ response ->
                if (response?.byteStream() != null) {
                    val inputStream: InputStream = response?.byteStream()
                    val file = File(subTaskData.filePath, subTaskData.fileName)
                    var raf = RandomAccessFile(file, "rwd")
                    //找到上一次的点
                    raf.seek(subTaskData.startPos)
                    val bytes = ByteArray(1024 * 2)
                    var len: Int
                    while (inputStream.read(bytes).also { len = it } != -1) {
                        raf.write(bytes, 0, len)
                        //保存下载的数据
                        subTaskData.downloadSize = subTaskData.downloadSize + len
                        subTaskCallBack?.downloading(
                            len.toFloat(),
                            subTaskData,
                            subTaskData.downloadSize / subTaskData.totalSize
                        )

                        //暂停
                        if (isPause) {
                            subTaskCallBack?.downloadCancle()
                            RoomManager.saveOrUpdateDownloadTask(subTaskData)
                            break
                        }
                    }
                    inputStream.close()
                    raf.close()
                    isFinish = true
                    subTaskCallBack?.downloadSuccess(file.absolutePath)
                } else {
                    throw RuntimeException("cannot get ResponseBody ")
                }
            }, {
                subTaskCallBack?.downloadFail(
                    if (it.message != null)
                        "任务${subTaskData.taskNum},下载出错：" + it.message else "任务${subTaskData.taskNum}下载失败！"
                )
            })
    }

}