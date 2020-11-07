package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.NestedScrollView
import com.example.mystorehouse.R
import kotlinx.android.synthetic.main.view_scroll_to_collapse.view.*
import java.util.*


/**
 *     Author : 李勇
 *     Create Time   : 2020/10/23
 *     Desc   : 滑动可折叠的View
 *     PackageName: com.example.mystorehouse.date.widget
 */
class ScrollToCollapseView(context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {

    private val TAG: String = "ScrollToCollapseView"
    private var collapseViewHeight = 0
    private var expendViewHeight = 0
    var selectDate = Date()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_scroll_to_collapse, this)

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
            month_view.alpha = 1F
            month_view.scaleY = 1F
            selectDate = it
            collapseViewHeight = month_view.measuredHeight
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
        scroll_view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {

            }
            false
        }
        scroll_view.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            mScrollY += scrollY - oldScrollY
            Log.i(TAG, "mScrollY: $mScrollY")
            selectYear = instance.get(Calendar.YEAR)
            selectMonth = instance.get(Calendar.MONTH) + 1
            instance.clear()
            instance.time = selectDate
            selectWeek = instance.get(Calendar.DAY_OF_WEEK)
            selectWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
            if (selectWeek == 1) {
                //说明是周日
                selectWeekOfYear--
            }
            instance.set(Calendar.DAY_OF_MONTH, 1)
            startWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
            if (instance.get(Calendar.DAY_OF_WEEK) == 1) {
                //说明是周日
                startWeekOfYear--
            }
            instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH))
            lastWeekOfYear = instance.get(Calendar.WEEK_OF_YEAR)
            if (instance.get(Calendar.DAY_OF_WEEK) == 1) {
                //说明是周日
                lastWeekOfYear--
            }
            if ((curMonth == selectMonth && curYear == selectYear) || (lastWeekOfYear == selectWeekOfYear)) {
                //处理效果一 吸顶效果
                scrollCategoriesOne(
                    scrollY,
                    oldScrollY,
                    mScrollY,
                    selectWeekOfYear,
                    startWeekOfYear,
                    lastWeekOfYear
                )
            } else {
                //处理效果二 折叠缩放效果
                if (Math.abs(mScrollY) < collapseViewHeight) {
                    //分上滑还是下滑
                    if (scrollY > oldScrollY) {
                        //上滑 滑动到选择时间的周时是 移动 之后才是缩放
                        if (Math.abs(mScrollY) < (selectWeekOfYear - startWeekOfYear) * expendViewHeight) {
                            week_view.visibility = View.GONE
                            scrollCategoriesOne(
                                scrollY,
                                oldScrollY,
                                mScrollY,
                                selectWeekOfYear,
                                startWeekOfYear,
                                lastWeekOfYear
                            )
                        } else if (Math.abs(mScrollY) <= collapseViewHeight &&
                            Math.abs(mScrollY) >= (selectWeekOfYear - startWeekOfYear) * expendViewHeight
                        ) {
                            //缩放 缩小
                            week_view.visibility = View.VISIBLE
                            //开始缩放monthview
                            var scaleY =
                                (collapseViewHeight - Math.abs(mScrollY)).toFloat() / ((lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight)
                            if (scaleY > 1) {
                                scaleY = 1F
                            }
                            if (scaleY < 0) {
                                scaleY = 0F
                            }
                            Log.i(TAG, "dvsleY: $scaleY")
                            Log.i(TAG, "mScrollY: $mScrollY")
                            month_view.pivotX = 0F
                            month_view.pivotY =
                                (lastWeekOfYear - selectWeekOfYear) * expendViewHeight.toFloat()
//                                        (selectWeekOfYear - startWeekOfYear) * expendViewHeight.toFloat()
//                            month_view.animate().interpolator = LinearInterpolator()
//                            month_view.animate().scaleY(scaleY)
//                            month_view.animation.interpolator = LinearInterpolator()
                            month_view.scaleY = scaleY
                            month_view.alpha = scaleY
                            val layoutParams =
                                month_view.layoutParams as LinearLayout.LayoutParams
//                                layoutParams.height = (scaleY * collapseViewHeight).toInt()
//                                layoutParams.topMargin = Math.abs(mScrollY)
                            Log.i(
                                TAG,
                                "scaleY * collapseViewHeight : ${scaleY * collapseViewHeight}"
                            )
//                                month_view.bottom = (scaleY * collapseViewHeight).toInt() - month_view.top
                            month_view.layoutParams = layoutParams
//                                month_view.resetHeight()
                            val contentLayoutParams =
                                tv_content.layoutParams as LinearLayout.LayoutParams
//                                contentLayoutParams.topMargin = -Math.abs(mScrollY)
//                                tv_content.top = month_view.bottom
                            tv_content.layoutParams = contentLayoutParams
                        } else {
                            month_view.scaleY = 0F
                            month_view.alpha = 0F
                            val contentLayoutParams =
                                tv_content.layoutParams as LinearLayout.LayoutParams
                            contentLayoutParams.topMargin = 0
//                                tv_content.top = month_view.bottom
                            tv_content.layoutParams = contentLayoutParams
//                                week_view.visibility = View.VISIBLE
                        }
                    } else {
                        val contentLayoutParams =
                            tv_content.layoutParams as LinearLayout.LayoutParams
                        contentLayoutParams.topMargin = 0
//                                tv_content.top = month_view.bottom
                        tv_content.layoutParams = contentLayoutParams
                        //下滑
                        if (Math.abs(mScrollY) <= (selectWeekOfYear - startWeekOfYear) * expendViewHeight) {
                            week_view.visibility = View.GONE
                            scrollCategoriesOne(
                                scrollY,
                                oldScrollY,
                                mScrollY,
                                selectWeekOfYear,
                                startWeekOfYear,
                                lastWeekOfYear
                            )
                            month_view.scaleY = 1F
                            month_view.alpha = 1F
                            val layoutParams =
                                month_view.layoutParams as LinearLayout.LayoutParams
                            layoutParams.topMargin = Math.abs(mScrollY)
                            layoutParams.height = (1 * collapseViewHeight).toInt()
                            month_view.layoutParams = layoutParams
                            Log.i(
                                TAG,
                                "scaleY * collapseViewHeight : ${scaleY * collapseViewHeight}"
                            )
                        } else if (Math.abs(mScrollY) >= (selectWeekOfYear - startWeekOfYear) * expendViewHeight
                            && Math.abs(mScrollY) <= collapseViewHeight
                        ) {
                            week_view.visibility = View.VISIBLE
                            //开始缩放monthview 放大
                            var scaleY =
                                (collapseViewHeight - Math.abs(mScrollY)).toFloat() / ((lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight)
                            if (scaleY > 1) {
                                scaleY = 1F
                            }
                            if (scaleY < 0) {
                                scaleY = 0F
                            }
                            Log.i(TAG, "dvsleY: $scaleY")
                            month_view.pivotX = 0F
                            month_view.pivotY = 0F
//                                        (selectWeekOfYear - startWeekOfYear) * expendViewHeight.toFloat()
//                            month_view.animate().interpolator = LinearInterpolator()
//                            month_view.animate().scaleY(scaleY)
//                            month_view.animation.interpolator = LinearInterpolator()
                            month_view.scaleY = scaleY
                            month_view.alpha = scaleY
                            val layoutParams =
                                month_view.layoutParams as LinearLayout.LayoutParams
                            layoutParams.topMargin = Math.abs(mScrollY)
                            layoutParams.height = (scaleY * collapseViewHeight).toInt()
                            Log.i(
                                TAG,
                                "scaleY * collapseViewHeight : ${scaleY * collapseViewHeight}"
                            )

                            month_view.layoutParams = layoutParams
                        } else {
//                                week_view.visibility = View.VISIBLE
                        }
                    }

                }
            }


        }
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
//                week_view.alpha = Math.abs(mScrollY)
//                    .toFloat() / (lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight
            }
        } else {
            //下滑
            if (lastWeekOfYear == selectWeekOfYear) {
                if (Math.abs(mScrollY) < collapseViewHeight) {
                    week_view.visibility = View.GONE
                }
            } else {
                if (Math.abs(mScrollY) <= (selectWeekOfYear - startWeekOfYear) * expendViewHeight) {
//                    week_view.alpha = 1 - Math.abs(mScrollY)
//                        .toFloat() / (lastWeekOfYear - selectWeekOfYear + 1) * expendViewHeight
//                    if (week_view.alpha == 1F) {
                    week_view.visibility = View.GONE
//                    }
                }
            }

        }
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