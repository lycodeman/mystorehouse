package com.example.mystorehouse

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView
import com.example.common.mvp.base.BaseEmptyActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_date_picker.*
import kotlinx.android.synthetic.main.view_scroll_to_collapse2.view.*
import mActivityComponent
import java.util.*

class DatePickerActivity : BaseEmptyActivity(){
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_date_picker
    }

    var measureHeight = 0
    var mScrollY = 0
//    lateinit var gestureDetector: GestureDetector

    override fun initView(): RxAppCompatActivity {
        val instance = Calendar.getInstance()
        instance.time = Date()
        var curYear = instance.get(Calendar.YEAR)
        var curMonth = instance.get(Calendar.MONTH) + 1
        year_view.setYear(curYear)
        year_view.setMonth(curMonth)
        custom_month_view.selectDayCallBack {date ->
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
            tv_date_content.text = year.toString()+"年"+month+"月"+instance.get(Calendar.DAY_OF_MONTH)+"日"
        }
        custom_month_view.selectMonthCallBack {date ->
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
        }
        custom_month_view.selectWeekCallBack {date ->
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
        }
        custom_month_view.post({
            measureHeight = custom_month_view.measuredHeight
        })
//        gestureDetector = GestureDetector(this, this)
        return this
    }

    override fun initData() {

    }

    override fun initListener() {
        super.initListener()

    }


}
