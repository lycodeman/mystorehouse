package com.example.mystorehouse.download

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.FileUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.download.Constants
import com.example.common.download.callback.SubTaskCallBack
import com.example.common.download.callback.SubTaskCallBackImp
import com.example.common.download.callback.TotalTaskCallBackImp
import com.example.common.download.data.SubTaskData
import com.example.common.download.data.getTaskPath
import com.example.common.download.room.RoomManager
import com.example.common.download.task.TotalTask
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.mystorehouse.R
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import hideProgress
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.activity_download.tv_cancle
import kotlinx.android.synthetic.main.activity_download.tv_pause
import kotlinx.android.synthetic.main.activity_download.tv_start
import kotlinx.android.synthetic.main.item_rv_download.view.*
import kotlinx.android.synthetic.main.item_rv_download_control.view.*
import mActivityComponent
import showProgress

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/18
 *     Desc   :
 *     PackageName: com.example.mystorehouse.download
 */
class DownLoadActivity : BaseEmptyActivity() {

    private var TAG = "DownLoadActivity"
    private var totalTask: TotalTask? = null

    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_download
    }

    private val baseQuickAdapter =
        object : BaseQuickAdapter<SubTaskData, BaseViewHolder>(R.layout.item_rv_download) {

            override fun convert(holder: BaseViewHolder, item: SubTaskData) {
                //展示单个任务的进度
                if (item.totalSize != 0L) {
                    holder.itemView.pb_download.progress =
                        (item.downloadSize.toFloat() / item.totalSize * 100).toInt()
                }
                Log.i(
                    TAG, "convert: ${holder.absoluteAdapterPosition.toString() + "==="
                            + item.totalSize + "===" + item.downloadSize + "====" + item.toString()} "
                )

                holder.itemView.tv_task_name.text = "子任务" + item.taskNum
            }
        }

    private val controlQuickAdapter =
        object : BaseQuickAdapter<SubTaskData, BaseViewHolder>(R.layout.item_rv_download_control) {
            override fun convert(holder: BaseViewHolder, item: SubTaskData) {
                holder.itemView.tv_task_name_control.text = "子任务" + item.taskNum
            }
        }

    override fun initView(): RxAppCompatActivity {
        rv_download.layoutManager = LinearLayoutManager(this)
        rv_download.adapter = baseQuickAdapter
        rv_download_control.layoutManager = LinearLayoutManager(this)
        rv_download_control.adapter = controlQuickAdapter
        return this
    }

    override fun initListener() {
        super.initListener()
        controlQuickAdapter.addChildClickViewIds(R.id.tv_start,R.id.tv_pause,R.id.tv_cancle)
        controlQuickAdapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                var item = baseQuickAdapter.data[position]

                if (view.id == R.id.tv_start) {
                    totalTask?.taskData?.run {
                        totalTask?.excuteSubTask(RoomManager.getSubTaskData(item.md5Value))
                    }

                } else if (view.id == R.id.tv_pause) {
                    totalTask?.pauseSubTask(item.taskNum)
                } else if (view.id == R.id.tv_cancle) {
                    totalTask?.cancleSubTask(item.taskNum)
                }
            }
        })
    }

    override fun initData() {
        totalTask = TotalTask.Builder()
            .filePath(this.externalCacheDir?.absolutePath ?: "")
            .fileName("腾讯app.apk")
            .url(Constants.BASE_URL + Constants.MOBILE_ASSISTANT_PATH)
            .threadCount(et_thread_count.text.toString().toInt())
            .build()
        //只有总进度
        bt_show_only_total.setOnClickListener {
            val taskCallBack = object : TotalTaskCallBackImp() {
                override fun downloadSuccess(filePtah: String) {
                    showToast("文件${filePtah}下载完成！")
                    hideProgress()
                }

                override fun downloading(progress: Float) {
                    pb_total_download.progress = (progress * 100).toInt()
                    Log.e(TAG, "downloading: progress: $progress")
                }

                override fun downloadFail(errorMsg: String) {
                    showToast(errorMsg)
                }

                override fun startDownload() {
                    showProgress("开始下载")
                }
            }
            totalTask = TotalTask.Builder()
                .filePath(this.externalCacheDir?.absolutePath ?: "")
                .fileName("腾讯app.apk")
                .url(Constants.BASE_URL + Constants.MOBILE_ASSISTANT_PATH)
                .threadCount(et_thread_count.text.toString().toInt())
                .build()
            totalTask?.addTotalTaskCallBack(taskCallBack)
            totalTask?.excuteTask(true)
        }
        //所有进度
        bt_show_all_task.setOnClickListener { }
        //只展示每个子任务
        bt_show_every_single.setOnClickListener {
            var threadCount = et_thread_count.text.toString().toInt()
            baseQuickAdapter.data.clear()
            baseQuickAdapter.notifyDataSetChanged()
            controlQuickAdapter.data.clear()
            controlQuickAdapter.notifyDataSetChanged()

            val taskCallBack = object : TotalTaskCallBackImp() {
                override fun downloadSuccess(filePtah: String) {
                    showToast("文件${filePtah}下载完成！")
                    hideProgress()
                }

                override fun downloading(progress: Float) {
                    pb_total_download.progress = (progress * 100).toInt()
                    Log.e(TAG, "downloading: progress: $progress")
                }

                override fun downloadFail(errorMsg: String) {
                    showToast(errorMsg)
                }

                override fun startDownload() {
                    showProgress("开始下载")
                }

                override fun onFileContentLength(contentLength: Long) {
                    super.onFileContentLength(contentLength)


                }
            }
            var subTaskCalls = mutableListOf<SubTaskCallBack>()
            for (i in 1..threadCount) {
                baseQuickAdapter.addData(SubTaskData(startPos = 0, endPos = 0, taskNum = i - 1))
                controlQuickAdapter.addData(SubTaskData(startPos = 0, endPos = 0, taskNum = i - 1))
                val subCallBack = object : SubTaskCallBackImp() {
                    override fun downloadSuccess(filePath: String) {
                        super.downloadSuccess(filePath)

                    }

                    override fun downloadCancle() {
                        super.downloadCancle()

                    }

                    override fun downloading(
                        length: Float,
                        subTaskData: SubTaskData,
                        progress: Float
                    ) {
                        super.downloading(length, subTaskData, progress)
                        baseQuickAdapter.setData(i - 1, subTaskData)
                        Log.i(TAG, "downloading: ${length / subTaskData.totalSize}")
                        Log.i(TAG,
                            "downloading: ${length} + ${subTaskData.downloadSize} + ${subTaskData.totalSize}"
                        )
                        Log.i(TAG, "downloading: $progress  ==taskNum=== ${subTaskData.taskNum}")
                    }
                }
                subTaskCalls.add(subCallBack)
            }
            totalTask = TotalTask.Builder()
                .filePath(this.externalCacheDir?.absolutePath ?: "")
                .threadCount(threadCount)
                .fileName("腾讯app.apk")
                .url(Constants.BASE_URL + Constants.MOBILE_ASSISTANT_PATH)
                .threadCount(et_thread_count.text.toString().toInt())
                .build()?.addSingleTaskCallBack(subTaskCalls)?.addTotalTaskCallBack(taskCallBack)
            totalTask?.excuteTask(true)
        }
        //不完全展示所有任务
        bt_show_not_all_single.setOnClickListener {

        }
        //取消
        tv_cancle.setOnClickListener {
            totalTask?.cancle()
        }
        //暂停
        tv_pause.setOnClickListener {
            totalTask?.pause()
        }
        //开始
        tv_start.setOnClickListener {
            bt_show_only_total.performClick()
        }

    }
}