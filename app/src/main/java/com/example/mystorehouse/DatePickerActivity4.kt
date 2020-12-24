package com.example.mystorehouse

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.mystorehouse.date.newwidget.MyRecycleview
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_date_picker4.*
import kotlinx.android.synthetic.main.item_date.view.*
import mActivityComponent
import java.util.*

class DatePickerActivity4 : BaseEmptyActivity() {
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_date_picker4
    }

    var mScrollY = 0

    override fun initView(): RxAppCompatActivity {
        val instance = Calendar.getInstance()
        instance.time = Date()
        var curYear = instance.get(Calendar.YEAR)
        var curMonth = instance.get(Calendar.MONTH) + 1
        year_view.setYear(curYear)
        year_view.setMonth(curMonth)
        custom_month_view.selectDayCallBack { date ->
            val instance = Calendar.getInstance()
            instance.time = date
            var year = instance.get(Calendar.YEAR)
            var month = instance.get(Calendar.MONTH) + 1
            if (year.toString() != year_view.getYear()) {
                year_view.setYear(year)
            }
            if (month.toString() != year_view.getMonth()) {
                year_view.setMonth(month)
            }
            adapter.addData(
                year.toString() + "年" + month + "月" + instance.get(
                    Calendar.DAY_OF_MONTH
                ) + "日"
            )
            Log.e("TAG", "initView: " )
        }
        custom_month_view.selectMonthCallBack { date ->
            val instance = Calendar.getInstance()
            instance.time = date
            var year = instance.get(Calendar.YEAR)
            var month = instance.get(Calendar.MONTH) + 1
            if (year.toString() != year_view.getYear()) {
                year_view.setYear(year)
            }
            if (month.toString() != year_view.getMonth()) {
                year_view.setMonth(month)
            }
            adapter.addData(
                year.toString() + "年" + month + "月" + instance.get(
                    Calendar.DAY_OF_MONTH
                ) + "日"
            )
            Log.e("TAG", "initView: " )
        }
        custom_month_view.selectWeekCallBack { date ->
            val instance = Calendar.getInstance()
            instance.time = date
            var year = instance.get(Calendar.YEAR)
            var month = instance.get(Calendar.MONTH) + 1
            if (year.toString() != year_view.getYear()) {
                year_view.setYear(year)
            }
            if (month.toString() != year_view.getMonth()) {
                year_view.setMonth(month)
            }
            adapter.addData(
                year.toString() + "年" + month + "月" + instance.get(
                    Calendar.DAY_OF_MONTH
                ) + "日"
            )
            Log.e("TAG", "initView: " )
        }
        my_list
        return this
    }

    override fun initListener() {
        super.initListener()
        my_list.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.e("TAG", "onScrolled:dy= "+dy )
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    val adapter =
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_date) {
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.itemView.tv.text = item;
        }

    }

    override fun initData() {
        val myRecycleview = MyRecycleview(this)
        myRecycleview.setScrollEnabled(true)
        my_list.layoutManager = myRecycleview
        var list = mutableListOf<String>()
        for (i in 1..50) {
            list.add("请选中你的日期：$i")
        }
        adapter.setList(list)
        my_list.adapter = adapter
    }


}
