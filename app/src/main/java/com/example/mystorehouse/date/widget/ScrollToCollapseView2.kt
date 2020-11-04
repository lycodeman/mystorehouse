package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.widget.NestedScrollView
import com.example.mystorehouse.R
import kotlinx.android.synthetic.main.view_scroll_to_collapse2.view.*
import java.util.*


/**
 *     Author : 李勇
 *     Create Time   : 2020/10/23
 *     Desc   : 滑动可折叠的View
 *     PackageName: com.example.mystorehouse.date.widget
 */
class ScrollToCollapseView2(context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {

    private val TAG: String = "ScrollToCollapseView"
    private var collapseViewHeight = 0
    private var expendViewHeight = 0
    var selectDate = Date()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_scroll_to_collapse2, this)

        var mScrollY = 0
        val instance = Calendar.getInstance()
        instance.time = java.util.Date()
        var curYear = instance.get(Calendar.YEAR)
        var curMonth = instance.get(Calendar.MONTH) + 1
        //获取是当年的第几周

        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH))
        //当月最后一天是当年第几周
        var lastWeekOfYear = 0


        year_view.setYear(curYear)
        year_view.setMonth(curMonth)
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
            instance.set(Calendar.DAY_OF_MONTH, 1)
            week_view.createWeekFg(instance.time)
            selectDate = it
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
            month_view.resetByDate(it)
            selectDate = instance.time
        }
        week_view.onSelectDate = {
            selectDate = it
            month_view.resetByDate(selectDate)
        }
        month_view.onSelectDate = {
            selectDate = it
            week_view.createWeekFg(selectDate)
        }
        year_view.onRefresh = { date ->
            month_view.resetByDate(date)
        }
        var selectWeekOfYear = 0
        var startWeekOfYear = 0
        var selectWeek = 0
        var selectMonth = 0
        var selectYear = 0
        scroll_view.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            mScrollY += scrollY - oldScrollY
            if (mScrollY == 0){
                isIntercept = true
            }
        }
//        scroll_view.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
//            mScrollY += scrollY - oldScrollY
//            Log.i(TAG, "mScrollY: $mScrollY")
//            selectYear = instance.get(Calendar.YEAR)
//            selectMonth = instance.get(Calendar.MONTH) + 1
//            instance.time = selectDate
//            selectWeek = instance.get(Calendar.DAY_OF_WEEK)
//            selectWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
//            if (selectWeek == 1) {
//                //说明是周日
//                selectWeekOfYear--
//            }
//            instance.set(Calendar.DAY_OF_MONTH, 1)
//            startWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
//            instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH))
//            lastWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
//            if (curMonth == selectMonth && curYear == selectYear) {
//                //处理效果一 吸顶效果
//                scrollCategoriesOne(
//                    scrollY,
//                    oldScrollY,
//                    mScrollY,
//                    selectWeekOfYear,
//                    startWeekOfYear,
//                    lastWeekOfYear
//                )
//            } else {
//                if (lastWeekOfYear == selectWeekOfYear) {
//                    scrollCategoriesOne(
//                        scrollY,
//                        oldScrollY,
//                        mScrollY,
//                        selectWeekOfYear,
//                        startWeekOfYear,
//                        lastWeekOfYear
//                    )
//                } else {
//                    //处理效果二 折叠缩放效果
//                    if (Math.abs(mScrollY) < collapseViewHeight) {
//                        //分上滑还是下滑
//                        if (scrollY > oldScrollY) {
//                            //上滑 滑动到选择时间的周时是 移动 之后才是缩放
//                            if (Math.abs(mScrollY) < (selectWeekOfYear - startWeekOfYear) * expendViewHeight) {
//                                week_view.visibility = View.GONE
//                                scrollCategoriesOne(
//                                    scrollY,
//                                    oldScrollY,
//                                    mScrollY,
//                                    selectWeekOfYear,
//                                    startWeekOfYear,
//                                    lastWeekOfYear
//                                )
//                            } else if (Math.abs(mScrollY) < collapseViewHeight &&
//                                Math.abs(mScrollY) > (selectWeekOfYear - startWeekOfYear) * expendViewHeight
//                            ) {
//                                //缩放 缩小
//                                week_view.visibility = View.VISIBLE
//                                //开始缩放monthview
//                                var scaleY =
//                                    (collapseViewHeight - Math.abs(mScrollY)).toFloat() / ((lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight)
//                                if (scaleY > 1) {
//                                    scaleY = 1F
//                                }
//                                if (scaleY < 0) {
//                                    scaleY = 0F
//                                }
//                                Log.i(TAG, "dvsleY: $scaleY")
//                                month_view.pivotX = 0F
//                                month_view.pivotY = 0F
////                                        (selectWeekOfYear - startWeekOfYear) * expendViewHeight.toFloat()
//                                month_view.scaleY = scaleY
//                                month_view.alpha = scaleY
//                                val layoutParams =
//                                    month_view.layoutParams as LinearLayout.LayoutParams
//                                layoutParams.height = (scaleY * collapseViewHeight).toInt()
//                                layoutParams.topMargin = Math.abs(mScrollY)
//                                Log.i(TAG, "scaleY * collapseViewHeight : ${scaleY * collapseViewHeight}")
//                                month_view.layoutParams = layoutParams
//                            } else {
//                                week_view.visibility = View.VISIBLE
//                            }
//                        } else {
//                            //下滑
//                            if (Math.abs(mScrollY) <= (selectWeekOfYear - startWeekOfYear) * expendViewHeight) {
//                                week_view.visibility = View.GONE
//                                scrollCategoriesOne(
//                                    scrollY,
//                                    oldScrollY,
//                                    mScrollY,
//                                    selectWeekOfYear,
//                                    startWeekOfYear,
//                                    lastWeekOfYear
//                                )
//                                Log.i(TAG, "scaleY * collapseViewHeight : ${scaleY * collapseViewHeight}")
//                            } else if (Math.abs(mScrollY) > (selectWeekOfYear - startWeekOfYear) * expendViewHeight
//                                && Math.abs(mScrollY) < collapseViewHeight
//                            ) {
//                                week_view.visibility = View.VISIBLE
//                                //开始缩放monthview 放大
//                                var scaleY =
//                                    (collapseViewHeight - Math.abs(mScrollY)).toFloat() / ((lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight)
//                                if (scaleY > 1) {
//                                    scaleY = 1F
//                                }
//                                if (scaleY < 0) {
//                                    scaleY = 0F
//                                }
//                                Log.i(TAG, "dvsleY: $scaleY")
//                                month_view.pivotX = 0F
//                                month_view.pivotY = 0F
////                                        (selectWeekOfYear - startWeekOfYear) * expendViewHeight.toFloat()
//                                month_view.scaleY = scaleY
//                                month_view.alpha = scaleY
//                                val layoutParams =
//                                    month_view.layoutParams as LinearLayout.LayoutParams
//                                layoutParams.topMargin = Math.abs(mScrollY)
//                                layoutParams.height = (scaleY * collapseViewHeight).toInt()
//                                Log.i(TAG, "scaleY * collapseViewHeight : ${scaleY * collapseViewHeight}")
//                                month_view.layoutParams = layoutParams
//                            } else {
////                                week_view.visibility = View.VISIBLE
//                            }
//                        }
//
//                    }
//                }
//
//
//            }
//        }
    }

    //处理滑动效果一，悬浮吸顶效果
    private fun scrollCategoriesOne(
        scrollY: Int,
        oldScrollY: Int,
        mScrollY: Int,
        selectWeekOfYear: Int,
        startWeekOfYear: Int,
        lastWeekOfYear: Int
    ) {
        if (scrollY - oldScrollY > 0) {
            //上滑
            if (Math.abs(mScrollY) > (selectWeekOfYear - startWeekOfYear) * expendViewHeight) {
                week_view.visibility = View.VISIBLE
                week_view.alpha = Math.abs(mScrollY)
                    .toFloat() / (lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight
            }
        } else {
            //下滑
            if (lastWeekOfYear == selectWeekOfYear) {
                if (Math.abs(mScrollY) < collapseViewHeight) {
                    week_view.visibility = View.GONE
                }
            } else {
                if (Math.abs(mScrollY) <= (lastWeekOfYear - selectWeekOfYear) * expendViewHeight) {
                    week_view.alpha = 1 - Math.abs(mScrollY)
                        .toFloat() / (lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight
                    if (week_view.alpha == 1F) {
                        week_view.visibility = View.GONE
                    }
                }
            }

        }
    }

    private var startX = 0F
    private var startY = 0F
    private var offsetX = 0F
    private var offsetY = 0F
    private var mScrollY = 0F
    var selectWeekOfYear = 0
    var startWeekOfYear = 0
    var endWeekOfYear = 0
    var selectWeek = 0
    var selectMonth = 0
    var selectYear = 0
    var instance: Calendar = Calendar.getInstance()
    private var isIntercept = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            instance = Calendar.getInstance()
            instance.time = selectDate
            startY = event?.y
            startX = event?.x
            selectYear = instance.get(Calendar.YEAR)
            selectMonth = instance.get(Calendar.MONTH) + 1
            instance.time = selectDate
            selectWeek = instance.get(Calendar.DAY_OF_WEEK)
            selectWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
            if (selectWeek == 1) {
                //说明是周日
                selectWeekOfYear--
            }
            instance.set(Calendar.DAY_OF_MONTH, 1)
            startWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
            instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH))
            endWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
            return true
        }else if (event?.action == MotionEvent.ACTION_MOVE){
            offsetX = event?.x - startX
            offsetY = event?.y - startY
            startY = event?.y
            startX = event?.x
            mScrollY +=offsetY
            Log.i(TAG, "onTouchEvent: $mScrollY")
            //竖直方向滑动
            if (Math.abs(offsetX) < Math.abs(offsetY)){

                //在折叠高度内
                if (Math.abs(mScrollY) <= collapseViewHeight){

                    if (offsetY < 0){
                        //上滑
                        if (Math.abs(mScrollY) <= expendViewHeight*(selectWeekOfYear - startWeekOfYear)){
                            //直接上滑
                            val monthLayoutParams = month_view.layoutParams as RelativeLayout.LayoutParams
                            monthLayoutParams.topMargin = -Math.abs(mScrollY).toInt()
                            month_view.layoutParams = monthLayoutParams
                            week_view.visibility = View.GONE
                        }else {
                            if (Math.abs(mScrollY) <= collapseViewHeight - expendViewHeight){
                                week_view.visibility = View.VISIBLE
                                val monthLayoutParams = month_view.layoutParams as RelativeLayout.LayoutParams
                                monthLayoutParams.topMargin = -Math.abs(mScrollY).toInt()
                                month_view.layoutParams = monthLayoutParams
                            }
                        }
                    }else {
                        //下滑
                        if (Math.abs(mScrollY) <= expendViewHeight*(selectWeekOfYear - startWeekOfYear)){
                            //直接上滑
                            val monthLayoutParams = month_view.layoutParams as RelativeLayout.LayoutParams
                            monthLayoutParams.topMargin = -Math.abs(mScrollY).toInt()
                            month_view.layoutParams = monthLayoutParams
                            week_view.visibility = View.GONE
                        }else {
                            if (Math.abs(mScrollY) <= collapseViewHeight - expendViewHeight){
                                week_view.visibility = View.VISIBLE
                                val monthLayoutParams = month_view.layoutParams as RelativeLayout.LayoutParams
                                monthLayoutParams.topMargin = -Math.abs(mScrollY).toInt()
                                month_view.layoutParams = monthLayoutParams
                            }
                        }
                    }

                    isIntercept = true
                    return true
                }else {
                    isIntercept = false
                    return false
                }

            }else {
                isIntercept = false
                return false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isIntercept
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        month_view.post {
            collapseViewHeight = month_view.measuredHeight
        }
        week_view.post {
            expendViewHeight = week_view.measuredHeight
            week_view.visibility = View.GONE
        }

    }


}