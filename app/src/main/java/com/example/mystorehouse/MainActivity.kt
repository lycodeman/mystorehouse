package com.example.mystorehouse

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.common.mvp.utils.ActivityManager
import com.example.mystorehouse.download.DownLoadActivity
import com.example.mystorehouse.mvp.activity.JokeListActivity
import com.example.mystorehouse.mvp.activity.WeatherActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_function.view.*
import mActivityComponent

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/27
 *     Desc   :
 *     PackageName: com.example.mystorehouse
 */

class MainActivity : BaseEmptyActivity() {

    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initContentId(): Int {
        return  R.layout.activity_main
    }

    override fun initView(): RxAppCompatActivity {
        return this
    }

    private val adapter =
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_function) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.itemView.tv_function.text = item
                holder.itemView.tv_function.setOnClickListener {
                    when(holder.absoluteAdapterPosition){
                        0 -> ActivityManager.jump(JokeListActivity::class.java)
                        1 -> ActivityManager.jump(WeatherActivity::class.java)
                        2 -> ActivityManager.jump(ScreenAdapterActivity::class.java)
                        3 -> ActivityManager.jump(DownLoadActivity::class.java)
                        4 -> ActivityManager.jump(DatePickerActivity::class.java)
                        5 -> ActivityManager.jump(DatePickerActivity2::class.java)
                        6 -> ActivityManager.jump(DatePickerActivity3::class.java)
                    }
                }
            }
        }

    override fun initData() {
        rv_main.layoutManager = LinearLayoutManager(this)
        rv_main.adapter = adapter
        val list = mutableListOf<String>()
        list.add("MVP 笑话列表展示")
        list.add("MVP 天气查询展示")
        list.add("屏幕适配")
        list.add("断点下载测试")
        list.add("自定义日历-时间选择")
        list.add("自定义日历-时间选择2")
        list.add("自定义日历-时间选择3")
        adapter.setList(list)
    }
}