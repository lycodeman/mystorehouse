package com.example.mystorehouse.date.widget

import android.widget.LinearLayout
import android.widget.TextView
import com.example.common.mvp.base.BaseEmptyFragment
import com.example.mystorehouse.R
import com.example.mystorehouse.date.Lunar
import com.example.mystorehouse.getColorFromResource
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_week.*
import mFragmentComponent
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   : 展示月份
 *     PackageName: com.example.mystorehouse.date.widget
 */
class WeekFragment(var date: Date, var clearSelectDay: (position: Int) -> Unit) :
    BaseEmptyFragment() {
    private var dateList = mutableListOf<Date>()
    private var lastSelectSolarView: TextView? = null
    private var lastSelectLunarView: TextView? = null
    private var lastSelectLLView: LinearLayout? = null
    private var lastDay = 0
    var onSelectDate: (Date) -> Unit = {}
    var tag = -1


    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        var calendar = Calendar.getInstance()
        calendar.time = date
        var month = calendar.get(Calendar.MONTH)
        ll_date_monday.setOnClickListener { ll ->
            if (month == dateList[0].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                tv_solar_date_monday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_monday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_monday.setBackgroundResource(R.drawable.shape_month_date)
                lastSelectSolarView = tv_solar_date_monday
                lastSelectLunarView = tv_lunar_date_monday
                onSelectDate.invoke(dateList[0])
            }
        }
        ll_date_tuesday.setOnClickListener { ll ->
            if (month == dateList[1].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                lastSelectSolarView = tv_solar_date_tuesday
                lastSelectLunarView = tv_lunar_date_tuesday
                onSelectDate.invoke(dateList[1])
                tv_solar_date_tuesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_tuesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_tuesday.setBackgroundResource(R.drawable.shape_month_date)
            }
        }
        ll_date_wednesday.setOnClickListener { ll ->
            if (month == dateList[2].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                lastSelectSolarView = tv_solar_date_wednesday
                lastSelectLunarView = tv_lunar_date_wednesday
                onSelectDate.invoke(dateList[2])
                tv_solar_date_wednesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_wednesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_wednesday.setBackgroundResource(R.drawable.shape_month_date)
            }
        }
        ll_date_thursday.setOnClickListener { ll ->
            if (month == dateList[3].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                lastSelectSolarView = tv_solar_date_thursday
                lastSelectLunarView = tv_lunar_date_thursday
                onSelectDate.invoke(dateList[3])
                tv_solar_date_thursday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_thursday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_thursday.setBackgroundResource(R.drawable.shape_month_date)
            }

        }
        ll_date_friday.setOnClickListener { ll ->
            if (month == dateList[4].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                lastSelectSolarView = tv_solar_date_friday
                lastSelectLunarView = tv_lunar_date_friday
                onSelectDate.invoke(dateList[4])
                tv_solar_date_friday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_friday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_friday.setBackgroundResource(R.drawable.shape_month_date)
            }
        }
        ll_date_saturday.setOnClickListener { ll ->
            if (month == dateList[5].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                lastSelectSolarView = tv_solar_date_saturday
                lastSelectLunarView = tv_lunar_date_saturday
                onSelectDate.invoke(dateList[5])
                tv_solar_date_saturday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_saturday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_saturday.setBackgroundResource(R.drawable.shape_month_date)
            }
        }
        ll_date_sunday.setOnClickListener { ll ->
            if (month == dateList[6].month) {
                lastSelectSolarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                }
                lastSelectLunarView?.run {
                    this.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                }
                lastSelectLLView?.run {
                    this.setBackgroundColor(getColorFromResource(R.color.transparent))
                }
                lastSelectLLView = ll as LinearLayout
                lastSelectSolarView = tv_solar_date_sunday
                lastSelectLunarView = tv_lunar_date_sunday
                onSelectDate.invoke(dateList[6])
                tv_solar_date_sunday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                tv_lunar_date_sunday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                ll_date_sunday.setBackgroundResource(R.drawable.shape_month_date)
            }
        }
    }

    fun initDate(date: Date) {
        dateList.clear()
        var calendar = Calendar.getInstance()
        calendar.time = date
        var day = calendar.get(Calendar.DAY_OF_YEAR)
        var month = calendar.get(Calendar.MONTH)
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        for (i in 1..7) {
            calendar.set(Calendar.DAY_OF_YEAR, i - dayOfWeek + day)
            dateList.add(calendar.time)
        }

        dateList.forEachIndexed { index, date ->
            calendar.time = date
            var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val lunar = Lunar(calendar)
            if (index == 0) {
                tv_solar_date_monday.text = dayOfMonth
                tv_lunar_date_monday.text = lunar.chinaDay
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_monday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_monday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_monday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_monday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_monday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_monday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_monday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_monday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_monday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            } else if (index == 1) {
                tv_solar_date_tuesday.text = dayOfMonth
                tv_lunar_date_tuesday.text = lunar.chinaDay
//                    SpanUtils.with(tv_solar_date_tuesday).append(dayOfMonth + "\n").appendSpace(14).append(lunar.chinaDay).appendSpace(10).create()
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_tuesday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_tuesday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_tuesday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_tuesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_tuesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_tuesday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_tuesday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_tuesday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_tuesday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            } else if (index == 2) {
                tv_solar_date_wednesday.text = dayOfMonth
                tv_lunar_date_wednesday.text = lunar.chinaDay
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_wednesday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_wednesday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_wednesday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_wednesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_wednesday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_wednesday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_wednesday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_wednesday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_wednesday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            } else if (index == 3) {
                tv_solar_date_thursday.text = dayOfMonth
                tv_lunar_date_thursday.text = lunar.chinaDay
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_thursday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_thursday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_thursday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_thursday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_thursday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_thursday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_thursday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_thursday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_thursday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            } else if (index == 4) {
                tv_solar_date_friday.text = dayOfMonth
                tv_lunar_date_friday.text = lunar.chinaDay
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_friday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_friday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_friday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_friday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_friday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_friday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_friday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_friday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_friday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            } else if (index == 5) {
                tv_solar_date_saturday.text = dayOfMonth
                tv_lunar_date_saturday.text = lunar.chinaDay
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_saturday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_saturday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_saturday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_saturday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_saturday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_saturday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_saturday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_saturday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_saturday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            } else if (index == 6) {
                tv_solar_date_sunday.text = dayOfMonth
                tv_lunar_date_sunday.text = lunar.chinaDay
                if (calendar.get(Calendar.MONTH) != month) {
                    //上个月
                    tv_solar_date_sunday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    tv_lunar_date_sunday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                    ll_date_sunday.setBackgroundColor(getColorFromResource(R.color.transparent))
                } else {
                    if (index + 1 == dayOfWeek) {
                        tv_solar_date_sunday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        tv_lunar_date_sunday.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        ll_date_sunday.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        tv_solar_date_sunday.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        tv_lunar_date_sunday.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        ll_date_sunday.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }
                }
            }

        }
        calendar.time = date
        calendar.get(Calendar.DAY_OF_WEEK) - 1


    }


    override fun initContentId(): Int {
        return R.layout.fragment_week
    }

    override fun initView(): RxFragment {
        var calendar = Calendar.getInstance()
        calendar.time = date
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        lastDay = dayOfWeek

        arrayListOf(
            tv_solar_date_monday,
            tv_solar_date_tuesday,
            tv_solar_date_wednesday,
            tv_solar_date_thursday
            ,
            tv_solar_date_friday,
            tv_solar_date_saturday,
            tv_solar_date_sunday
        ).forEachIndexed { index, textView ->
            if (index + 1 == dayOfWeek) {
                lastSelectSolarView = textView
            }
        }
        arrayListOf(
            ll_date_monday, ll_date_tuesday, ll_date_wednesday, ll_date_thursday
            , ll_date_friday, ll_date_saturday, ll_date_sunday
        ).forEachIndexed { index, ll ->
            if (index + 1 == dayOfWeek) {
                lastSelectLLView = ll
            }
        }
        arrayListOf(
            tv_lunar_date_monday,
            tv_lunar_date_tuesday,
            tv_lunar_date_wednesday,
            tv_lunar_date_thursday,
            tv_lunar_date_friday,
            tv_lunar_date_saturday,
            tv_lunar_date_sunday
        ).forEachIndexed { index, textView ->
            if (index + 1 == dayOfWeek) {
                lastSelectLunarView = textView
            }
        }
        initDate(date)
        return this
    }

    fun clearSelectDate() {

    }


}