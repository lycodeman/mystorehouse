package com.example.mystorehouse.download

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.download.Constants
import com.example.common.download.callback.TotalTaskCallBackImp
import com.example.common.download.task.SingleTask
import com.example.common.download.task.TotalTask

import com.example.common.mvp.base.BaseEmptyActivity
import com.example.mystorehouse.R
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import hideProgress
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.item_rv_download.view.*
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
        object : BaseQuickAdapter<SingleTask, BaseViewHolder>(R.layout.item_rv_download) {
            override fun convert(holder: BaseViewHolder, item: SingleTask) {
                holder.itemView.tv_start.setOnClickListener { }
                holder.itemView.tv_pause.setOnClickListener { }
                holder.itemView.tv_cancle.setOnClickListener { }
                //展示单个任务的进度
                holder.itemView.pb_download.progress =
                    (item.singleTaskData.downloadSize / item.singleTaskData.totalSize).toInt()
            }
        }

    override fun initView(): RxAppCompatActivity {
        rv_download.layoutManager = LinearLayoutManager(this)
        rv_download.adapter = baseQuickAdapter
        return this
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
            totalTask?.excuteTask()
        }
        //所有进度
        bt_show_all_task.setOnClickListener { }
        //只展示每个子任务
        bt_show_every_single.setOnClickListener { }
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