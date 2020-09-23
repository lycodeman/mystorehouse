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
        this.singleTaskData = singleTaskData
    }

    @Volatile
    var isPause = false
    @Volatile
    var isCancle = false
    @Volatile
    var isFinish = false

    override fun createThread(): Thread {
        var threadName = singleTaskData.fileName + "_" + singleTaskData.taskNum
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
                            //刷新进度
                            singleTaskData.downloadSize = singleTaskData.downloadSize + len
                            singleTaskCallBack?.downloading(
                                len.toFloat(),
                                singleTaskData
                            )
                            //取消
                            if (isCancle){
                                singleTaskCallBack?.downloadCancle()
                                break
                            }
                            //暂停
                            if (isPause) {
                                singleTaskCallBack?.downloadPause()
                                RoomManager.saveOrUpdateDownloadTask(singleTaskData)
                                break
                            }
                        }
                        inputStream.close()
                        raf.close()
                        isFinish = true
                        if (!isCancle && !isPause){
                            singleTaskCallBack?.downloadSuccess(file.absolutePath)
                        }
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

    override fun excute() {
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
    }

}