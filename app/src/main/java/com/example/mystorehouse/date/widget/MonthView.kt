package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mystorehouse.R
import kotlinx.android.synthetic.main.view_date_month.view.*
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   : 支持选择某一天的view
 *     PackageName: com.example.mystorehouse.date.widget
 */
class MonthView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    var mFragmentLsit = mutableListOf<MonthFragment>()
    var onPageSelect: (date: Date) -> Unit = {}
    var onSelectDate: (date: Date) -> Unit = {}

    private val adapter = object : FragmentStateAdapter(context as FragmentActivity) {
        override fun getItemCount(): Int {
            return mFragmentLsit.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragmentLsit[position]
        }
    }

    var curMonth = 0
    var curYear = 0
    var currentPosition = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_date_month, this)
        var calendar = Calendar.getInstance()
        calendar.time = Date()
        curMonth = calendar.get(Calendar.MONTH)
        curYear = calendar.get(Calendar.YEAR)
        resetByDate(Date())
    }

    fun createMonthFg(
        date: Date,
        position: Int,
        clearSelectDay: (position: Int) -> Unit
    ): MonthFragment {
        val monthFragment = MonthFragment(date, clearSelectDay)
        monthFragment.tag = position
        return monthFragment
    }

    fun resetByDate(date: Date) {
        var calendar = Calendar.getInstance()
        calendar.time = date
        var month = calendar.get(Calendar.MONTH)
        //默认加载4条数据，方便左右侧滑
        mFragmentLsit.clear()
        for (i in 1..5) {
            calendar.set(Calendar.MONTH, month - 3 + i)

            mFragmentLsit.add(createMonthFg(calendar.time, i - 1) { position ->
                mFragmentLsit.forEachIndexed { index, monthFragment ->
                    if (index != position) {
                        monthFragment.clearSelectDate()
                    }
                    monthFragment.onSelectDate = { date ->
                        onSelectDate.invoke(date)
                    }
                }
            })
        }
        vp_month.adapter = adapter
        vp_month.offscreenPageLimit = 3

        vp_month.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val instance = Calendar.getInstance()
                    if (currentPosition == 0) {
                        //右滑
                        mFragmentLsit.mapIndexed { index, monthFragment ->
                            instance.time = monthFragment.date
                            instance.set(Calendar.MONTH, instance.get(Calendar.MONTH) - 3)
                            monthFragment.date = instance.time
                            monthFragment.initDate()
                        }
                        vp_month.setCurrentItem(mFragmentLsit.size - 2, false);//切换，不要动画效果
                    } else if (currentPosition == mFragmentLsit.size - 1) {
                        //左滑
                        mFragmentLsit.mapIndexed { index, monthFragment ->
                            instance.time = monthFragment.date
                            instance.set(Calendar.MONTH, instance.get(Calendar.MONTH) + 3)
                            monthFragment.date = instance.time
                            monthFragment.initDate()
                        }
                        vp_month.setCurrentItem(1, false);//切换，不要动画效果
                    }
                    //页面切换的时候，如果不是当前时间的月份，主动设置选择当月第一天 ，否则使用当前月份
                    val selectDate = mFragmentLsit[currentPosition].date
                    instance.time = selectDate
                    instance.set(Calendar.DAY_OF_MONTH, 1)
                    var selectMonth = instance.get(Calendar.MONTH)
                    var selectYear = instance.get(Calendar.YEAR)
                    instance.time = selectDate
                    instance.set(Calendar.DAY_OF_MONTH, 1)
                    if (curMonth == selectMonth && curYear == selectYear){
                        onPageSelect.invoke(Date())
                    }else {
                        onPageSelect.invoke(instance.time)
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                //页面切换 重新测量下高度
                resetHeight()

            }


        })
        vp_month.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp_month.setCurrentItem(2, false)
    }

    fun resetHeight(){
        var recyclerView = mFragmentLsit.get(currentPosition).getRv()
        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            recyclerView?.width ?: 0,
            View.MeasureSpec.EXACTLY
        )
        val hMeasureSpec = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        recyclerView?.measure(wMeasureSpec, hMeasureSpec)
        var measureHeight = recyclerView?.measuredHeight ?: 0
        vp_month.layoutParams =
            (vp_month.layoutParams as LinearLayout.LayoutParams)
                .also { lp ->
                    if (measureHeight != 0) {
                        lp.height = measureHeight
                    }
                }
    }


}