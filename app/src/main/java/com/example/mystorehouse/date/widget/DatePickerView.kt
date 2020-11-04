package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.widget.NestedScrollView
import com.example.mystorehouse.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.view_date_picker.view.*
import kotlinx.android.synthetic.main.view_date_picker.view.month_view
import kotlinx.android.synthetic.main.view_date_picker.view.week_view
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   : 支持选择某一天的view
 *     PackageName: com.example.mystorehouse.date.widget
 */
class DatePickerView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    var selectDate = Date()
    var weekHeight = 0
    var monthHeight = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_date_picker, this)
        val instance = Calendar.getInstance()
        instance.time = Date()
        var year = instance.get(Calendar.YEAR)
        var month = instance.get(Calendar.MONTH) + 1
        year_view.setYear(year)
        year_view.setMonth(month)
        month_view.onPageSelect = {
            val instance = Calendar.getInstance()
            instance.time = it
            var year = instance.get(Calendar.YEAR)
            var month = instance.get(Calendar.MONTH) + 1
            if (year.toString() != year_view.getYear()) {
                year_view.setYear(year)
            }
            if (month.toString() != year_view.getMonth()) {
                year_view.setMonth(month)
            }
        }
        week_view.onPageSelect = {
            val instance = Calendar.getInstance()
            instance.time = it
            var year = instance.get(Calendar.YEAR)
            var month = instance.get(Calendar.MONTH) + 1
            if (year.toString() != year_view.getYear()) {
                year_view.setYear(year)
            }
            if (month.toString() != year_view.getMonth()) {
                year_view.setMonth(month)
            }
        }
        week_view.onSelectDate = {
            selectDate = it
        }
        month_view.onSelectDate = {
            selectDate = it
        }
        year_view.onRefresh = { date ->
            month_view.resetByDate(date)
        }

        week_view.visibility = View.VISIBLE
        week_view.post {
            weekHeight = week_view.measuredHeight
            week_view.visibility = View.GONE
            week_view.alpha = 0F
            collapse_tab.minimumHeight = weekHeight
        }
        month_view.post {
            monthHeight = month_view.measuredHeight
        }
        var mScrollY = 0

        scroll_view.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            mScrollY += scrollY - oldScrollY
            Log.i("TAG", "mScrollY: $mScrollY")
        }
        appbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                Log.i("TAG", "onOffsetChanged: $p1")
                if (Math.abs(p1) >= monthHeight - weekHeight) {
                    //可以显示weekview
                    showWeekViewAnimator(Math.abs(p1) - (monthHeight - weekHeight))
                } else if (Math.abs(p1) < monthHeight) {
                    hideWeekViewAnimator(Math.abs(p1) - (monthHeight - weekHeight))
                }
            }

        })
    }

    // 0  --- weekHeight
    private fun showWeekViewAnimator(height: Int) {
        week_view.visibility = View.VISIBLE
        week_view.alpha = 2 * height.toFloat() / weekHeight
    }


    //weekHeight --- 0
    private fun hideWeekViewAnimator(height: Int) {
        week_view.alpha = height.toFloat() / weekHeight / 2
        if (week_view.alpha <= 0.01) {
            week_view.visibility = View.GONE
        }
    }

    var startX = 0F
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            startX = event.x
        }else if (event?.action == MotionEvent.ACTION_MOVE){
            if (event.x - startX > 0 && Math.abs(event.x - startX.toInt()) > 30) {
                //向下
//                getParent().requestDisallowInterceptTouchEvent(false)
//                false
                coordinator_layout.isEnabled = false
                false
            }
            if (event.x - startX < 0 && Math.abs(event.x - startX.toInt()) > 30) {
                //向下
//                getParent().requestDisallowInterceptTouchEvent(false)
//                false
                coordinator_layout.isEnabled = false
                false
            }


        }else if (event?.action == MotionEvent.ACTION_UP){

        }
        coordinator_layout.isEnabled = true
        Log.i("TAG", "onTouchEvent: "+(event?.x?:0 - startX))
        return super.onTouchEvent(event)
    }

}