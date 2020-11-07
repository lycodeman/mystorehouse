package com.example.mystorehouse.date.newwidget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.example.mystorehouse.R
import com.example.mystorehouse.date.Utils
import com.example.mystorehouse.date.entity.MonthEntity
import com.example.mystorehouse.dpToPx
import com.example.mystorehouse.getColorFromResource
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/11/05
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.newwidget
 */
class CustomMonthView(context: Context?, attrs: AttributeSet?) : View(context, attrs),
    GestureDetector.OnGestureListener {

    var monthStartDate = Date()
    var weekStartDate = Date()
    var curDate = Date()
    var screenWidth = 0
    var curMonthDays = mutableListOf<MonthEntity>()
    var lastMonthDays = mutableListOf<MonthEntity>()
    var nextMonthDays = mutableListOf<MonthEntity>()
    var curWeekDays = mutableListOf<MonthEntity>()
    var lastWeekDays = mutableListOf<MonthEntity>()
    var nextWeekDays = mutableListOf<MonthEntity>()
    var curWeekNum = 0
    var lastWeekNum = 0
    var nextWeekNum = 0
    var lineHeight = dpToPx(50)
    var drawX = 0
    var drawY = 0
    var dayWidth = 0
    var monthEntity: MonthEntity? = null
    var selectMonthEntity: MonthEntity? = null
    var solarDay = ""
    var lunarDay = ""
    var solarDayPaint = TextPaint()
    var lunarDayPaint = TextPaint()
    var selectDayBcPaint = Paint()
    var solarTextWidth = 0F
    var lunarTextWidth = 0F
    var padding = dpToPx(4)
    var padding6 = dpToPx(6)
    var height14 = dpToPx(14)
    var height10 = dpToPx(10)
    var dy = 0f
    var raduis = dpToPx(4)
    var solarDayFontMetrics: Paint.FontMetrics
    var lunarDayFontMetrics: Paint.FontMetrics
    var selectDate: Date? = null
    var gestureDetector: GestureDetector
    var mScrollY = 0F
    var mScrollX = 0F
    var mScrollDistance = 0F
    var selectLine = 0
    var isScroll = false
    var isCollapse = false
    var isExpand = false
    var isDrawWeek = false
    var isHorizontal: Boolean? = null
    var collapseAnimator: ValueAnimator? = null
    var horizontalAnimator: ValueAnimator? = null

    init {
        curMonthDays.addAll(Utils().getMonthDays(monthStartDate))
        lastMonthDays.addAll(Utils().getLastMonthDays(monthStartDate))
        nextMonthDays.addAll(Utils().getNextMonthDays(monthStartDate))
        curWeekNum = curMonthDays.size / 7
        lastWeekNum = lastMonthDays.size / 7
        nextWeekNum = nextMonthDays.size / 7
        screenWidth = ScreenUtils.getScreenWidth()
        dayWidth = screenWidth / 7
        solarDayPaint.textSize = dpToPx(14).toFloat()
        solarDayPaint.color = getColorFromResource(R.color.color_1C1C1C)
        solarDayPaint.isFakeBoldText = true
        solarDayPaint.isAntiAlias = true
        lunarDayPaint.textSize = dpToPx(10).toFloat()
        lunarDayPaint.isFakeBoldText = true
        lunarDayPaint.isAntiAlias = true
        lunarDayPaint.color = getColorFromResource(R.color.color_9A9A9A)
        solarDayFontMetrics = solarDayPaint.fontMetrics
        lunarDayFontMetrics = lunarDayPaint.fontMetrics
        selectDayBcPaint.color = getColorFromResource(R.color.DA9E9E)
        gestureDetector = GestureDetector(context, this)
        selectDate = monthStartDate
        setWillNotDraw(false)
    }

    var onDaySelectCallBack: ((Date?) -> Unit)? = null
    var onMonthSelectCallBack: ((Date?) -> Unit)? = null
    var onWeekSelectCallBack: ((Date?) -> Unit)? = null

    fun selectDayCallBack(callBack: (Date?) -> Unit) {
        this.onDaySelectCallBack = callBack
    }

    fun selectMonthCallBack(callBack: (Date?) -> Unit) {
        this.onMonthSelectCallBack = callBack
    }

    fun selectWeekCallBack(callBack: (Date?) -> Unit) {
        this.onWeekSelectCallBack = callBack
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isDrawWeek) {
            //绘制当前月
            drawMonth(canvas, curWeekNum, 0 + mScrollX.toInt(), curMonthDays)
            //绘制上个月
            drawMonth(canvas, lastWeekNum, -screenWidth + mScrollX.toInt(), lastMonthDays)
            //绘制下个月
            drawMonth(canvas, nextWeekNum, screenWidth + mScrollX.toInt(), nextMonthDays)
        } else {
            drawWeek(canvas, curWeekDays, 0 + mScrollX.toInt())
            drawWeek(canvas, lastWeekDays, -screenWidth + mScrollX.toInt())
            drawWeek(canvas, nextWeekDays, screenWidth + mScrollX.toInt())
        }

    }

    private fun drawMonth(
        canvas: Canvas?,
        weekNum: Int,
        offsetX: Int,
        monthDays: MutableList<MonthEntity>
    ) {
        for (column in weekNum downTo 1) {
            drawY = (lineHeight / 2 + (column - 1) * lineHeight)
            if (column > 1) {
                drawY += (mScrollDistance * (column - 1)).toInt()
            }
            for (row in 1..7) {
                drawX = dayWidth / 2 + dayWidth * (row - 1) + offsetX
                monthEntity = monthDays[(column - 1) * 7 + row - 1]
                solarDay = monthEntity?.solarDay ?: ""
                lunarDay = monthEntity?.lunarDay ?: ""
                solarTextWidth = solarDayPaint.measureText(solarDay)
                lunarTextWidth = lunarDayPaint.measureText(lunarDay)
                if (Utils().isCurrentDay(monthEntity?.date)) {
                    selectLine = column
                    var rectF = RectF(
                        drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                        (drawY - padding / 2 - padding6 - height14).toFloat(),
                        drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                        (drawY + padding / 2 + padding6 + height10).toFloat()
                    )
                    if (column > 1 && Math.abs(mScrollDistance) > 0) {
                        solarDayPaint.setColor(
                            Color.argb(
                                ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                255,
                                255,
                                255
                            )
                        )
                        lunarDayPaint.setColor(
                            Color.argb(
                                ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                255,
                                255,
                                255
                            )
                        )
                        selectDayBcPaint.setColor(
                            Color.argb(
                                ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                218,
                                158,
                                158
                            )
                        )
                    } else {
                        solarDayPaint.color = getColorFromResource(R.color.white)
                        lunarDayPaint.color = getColorFromResource(R.color.white)
                        selectDayBcPaint.color = getColorFromResource(R.color.color_BC0202)
                    }
                    canvas?.drawRoundRect(
                        rectF,
                        raduis.toFloat(),
                        raduis.toFloat(),
                        selectDayBcPaint
                    )
                } else if (Utils().isSelectDay(monthEntity?.date, selectDate)) {
                    selectLine = column
                    var rectF = RectF(
                        drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                        (drawY - padding / 2 - padding6 - height14).toFloat(),
                        drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                        (drawY + padding / 2 + padding6 + height10).toFloat()
                    )
                    if (column > 1 && Math.abs(mScrollDistance) > 0) {
                        solarDayPaint.setColor(
                            Color.argb(
                                ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                227,
                                92,
                                92
                            )
                        )
                        lunarDayPaint.setColor(
                            Color.argb(
                                ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                227,
                                92,
                                92
                            )
                        )
                        selectDayBcPaint.setColor(
                            Color.argb(
                                ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                188,
                                2,
                                2
                            )
                        )
                    } else {
                        solarDayPaint.color = getColorFromResource(R.color.color_E35C5C)
                        lunarDayPaint.color = getColorFromResource(R.color.color_E35C5C)
                        selectDayBcPaint.color = getColorFromResource(R.color.DA9E9E)
                    }
                    canvas?.drawRoundRect(
                        rectF,
                        raduis.toFloat(),
                        raduis.toFloat(),
                        selectDayBcPaint
                    )
                } else {
                    if (monthEntity?.onTheMonth == true) {
                        if (column > 1 && Math.abs(mScrollDistance) > 0) {
                            solarDayPaint.setColor(
                                Color.argb(
                                    ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                    28,
                                    28,
                                    28
                                )
                            )
                            lunarDayPaint.setColor(
                                Color.argb(
                                    ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                    154,
                                    154,
                                    154
                                )
                            )
                        } else {
                            solarDayPaint.color = getColorFromResource(R.color.color_1C1C1C)
                            lunarDayPaint.color = getColorFromResource(R.color.color_9A9A9A)
                        }
                    } else {
                        if (column > 1 && Math.abs(mScrollDistance) > 0) {
                            solarDayPaint.setColor(
                                Color.argb(
                                    ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                    190,
                                    190,
                                    190
                                )
                            )
                            lunarDayPaint.setColor(
                                Color.argb(
                                    ((1 + mScrollDistance / lineHeight) * 155).toInt(),
                                    190,
                                    190,
                                    190
                                )
                            )
                        } else {
                            solarDayPaint.color = getColorFromResource(R.color.color_BEBEBE)
                            lunarDayPaint.color = getColorFromResource(R.color.color_BEBEBE)
                        }
                    }
                }

                canvas?.drawText(
                    solarDay,
                    drawX - solarTextWidth / 2,
                    drawY.toFloat() - padding / 2,
                    solarDayPaint
                )
                dy =
                    (lunarDayFontMetrics.descent - lunarDayFontMetrics.ascent) / 2 - lunarDayFontMetrics.descent
                canvas?.drawText(
                    lunarDay,
                    drawX - lunarTextWidth / 2,
                    drawY.toFloat() + padding / 2 + dpToPx(10),
                    lunarDayPaint
                )
            }
        }
    }

    var widthMeasureSpec: Int = 0
    var heightMeasureSpec: Int = 0
    var tempHeight: Int = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        this.widthMeasureSpec = widthMeasureSpec
        if (!isDrawWeek) {
            tempHeight = lineHeight * curWeekNum - mScrollY.toInt()
            this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                if(tempHeight<lineHeight)lineHeight else tempHeight,
                MeasureSpec.getMode(heightMeasureSpec)
            )
        } else {
            this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                lineHeight,
                MeasureSpec.getMode(heightMeasureSpec)
            )
        }
        Log.e("TAG", "onMeasure: "+MeasureSpec.getSize(this.heightMeasureSpec))
        super.onMeasure(widthMeasureSpec, this.heightMeasureSpec)
    }

    fun onResetMeasure() {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
            lineHeight * curWeekNum,
            MeasureSpec.UNSPECIFIED
        )
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //在滑动范围内进行折叠和缩放
        if (isHorizontal == false) {
            if (event?.action == MotionEvent.ACTION_UP) {
                if (isScroll) {
                    if (isCollapse) {
                        collapseAnimator =
                            ValueAnimator.ofFloat(mScrollDistance, -lineHeight.toFloat())
                        collapseAnimator?.addUpdateListener {
                            mScrollDistance = it.animatedValue as Float
                            requestLayout()
                            invalidate()
                        }
                        mScrollY = lineHeight * curWeekNum.toFloat()
                        isCollapse = false
                        collapseAnimator?.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                startDrawWeek()
//                                requestLayout()
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                            }
                        })
                        collapseAnimator?.setDuration(500)
                        collapseAnimator?.start()
                    }
                    if (isExpand) {
                        collapseAnimator = ValueAnimator.ofFloat(mScrollDistance, 0F)
                        collapseAnimator?.addUpdateListener {
                            mScrollDistance = it.animatedValue as Float
                            requestLayout()
                            invalidate()
                        }
                        collapseAnimator?.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
//                                startDrawWeek()
//                                requestLayout()
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                            }
                        })
                        mScrollY = 0F
                        isExpand = false
                        collapseAnimator?.setDuration(500)
                        collapseAnimator?.start()
                    }
                }
            }
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        } else if (isHorizontal == true) {
            if (event?.action == MotionEvent.ACTION_UP) {
                if (mScrollX > 0 && mScrollX <= screenWidth) {
                    horizontalAnimator = ValueAnimator.ofFloat(mScrollX, screenWidth.toFloat())
                    horizontalAnimator?.addUpdateListener {
                        mScrollX = it.animatedValue as Float
                        invalidate()
                    }
                    horizontalAnimator?.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            monthStartDate = Utils().getLastMonth(monthStartDate)
                            curMonthDays = Utils().getMonthDays(monthStartDate)
                            lastMonthDays = Utils().getLastMonthDays(monthStartDate)
                            nextMonthDays = Utils().getNextMonthDays(monthStartDate)

                            if (isDrawWeek) {
                                weekStartDate = Utils().getLastWeek(weekStartDate)
                                curWeekDays = Utils().getWeekDays(weekStartDate)
                                selectDate = curWeekDays[Utils().getWeekNum(selectDate)].date
                                nextWeekDays = Utils().getNextWeekDays(weekStartDate)
                                lastWeekDays = Utils().getLastWeekDays(weekStartDate)
                                onWeekSelectCallBack?.invoke(weekStartDate)
                            } else {
                                selectDate = Utils().getFirstDayOfmonth(monthStartDate)
                                weekStartDate = Utils().getFirstWeek(monthStartDate)
                                curWeekDays = Utils().getWeekDays(weekStartDate)
                                nextWeekDays = Utils().getNextWeekDays(weekStartDate)
                                lastWeekDays = Utils().getLastWeekDays(weekStartDate)
                                onMonthSelectCallBack?.invoke(monthStartDate)
                            }
                            curWeekNum = curMonthDays.size / 7
                            lastWeekNum = lastMonthDays.size / 7
                            nextWeekNum = nextMonthDays.size / 7
                            isHorizontal = null
                            mScrollX = 0F
                            requestLayout()
                            postInvalidate()
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }
                    })
                    horizontalAnimator?.setDuration(500)
                    horizontalAnimator?.start()
                } else if (mScrollX < 0 && -mScrollX <= screenWidth) {
                    horizontalAnimator = ValueAnimator.ofFloat(mScrollX, -screenWidth.toFloat())
                    horizontalAnimator?.addUpdateListener {
                        mScrollX = it.animatedValue as Float
                        invalidate()
                    }
                    horizontalAnimator?.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            monthStartDate = Utils().getNextMonth(monthStartDate)
                            curMonthDays = Utils().getMonthDays(monthStartDate)
                            lastMonthDays = Utils().getLastMonthDays(monthStartDate)
                            nextMonthDays = Utils().getNextMonthDays(monthStartDate)
                            if (isDrawWeek) {
                                weekStartDate = Utils().getNextWeek(weekStartDate)
                                curWeekDays = Utils().getWeekDays(weekStartDate)
                                selectDate = curWeekDays[Utils().getWeekNum(selectDate)].date
                                nextWeekDays = Utils().getNextWeekDays(weekStartDate)
                                lastWeekDays = Utils().getLastWeekDays(weekStartDate)
                                onWeekSelectCallBack?.invoke(weekStartDate)
                            } else {
                                selectDate = Utils().getFirstDayOfmonth(monthStartDate)
                                weekStartDate = Utils().getFirstWeek(monthStartDate)
                                curWeekDays = Utils().getWeekDays(weekStartDate)
                                nextWeekDays = Utils().getNextWeekDays(weekStartDate)
                                lastWeekDays = Utils().getLastWeekDays(weekStartDate)
                                onMonthSelectCallBack?.invoke(monthStartDate)
                            }
                            curWeekNum = curMonthDays.size / 7
                            lastWeekNum = lastMonthDays.size / 7
                            nextWeekNum = nextMonthDays.size / 7
                            isHorizontal = null
                            mScrollX = 0F

                            requestLayout()
                            postInvalidate()
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }
                    })
                    horizontalAnimator?.setDuration(500)
                    horizontalAnimator?.start()
                }
            }
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        } else {
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        }
    }

    override fun onShowPress(e: MotionEvent?) {
        isScroll = false
    }

    override fun onSingleTapUp(event: MotionEvent?): Boolean {
        isScroll = false
        Log.e("TAG", "onSingleTapUp: " + event?.y)
        //计算出点击区域
        if (isDrawWeek) {
            if (((event?.x ?: 0F) <= screenWidth) && (event?.y ?: 0F) < lineHeight) {
                var row = (((event?.x ?: 0F) / dayWidth)).toInt()
                selectMonthEntity =
                    curWeekDays[row]
                if (selectMonthEntity?.onTheMonth == true) {
                    selectDate = selectMonthEntity?.date
                    invalidate()
                }
                if (selectMonthEntity?.onTheMonth == true) {
                    onDaySelectCallBack?.invoke(selectMonthEntity?.date)
                }
                return true
            }
        } else {
            if (((event?.x ?: 0F) <= screenWidth) && (event?.y ?: 0F) < lineHeight * curWeekNum) {
                var col = (((event?.y ?: 0F) / lineHeight) + 1).toInt()
                var row = (((event?.x ?: 0F) / dayWidth)).toInt()
                selectLine = col
                selectMonthEntity =
                    curMonthDays[((col - 1) * 7) + row]
                if (selectMonthEntity?.onTheMonth == true) {
                    selectDate = selectMonthEntity?.date
                    invalidate()
                }
                if (selectMonthEntity?.onTheMonth == true) {
                    onDaySelectCallBack?.invoke(selectMonthEntity?.date)
                }
                return true
            }
        }
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.e("TAG", "onFling: ${(e2?.y ?: 0F) - (e1?.y ?: 0F)}")
        isScroll = false
        return true
    }

    var isInterceptScroll = false

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        isInterceptScroll = true
        if (isDrawWeek && Math.abs(distanceX) > Math.abs(distanceY)) {
            isHorizontal = true
        }
        if ((Math.abs(distanceX) < Math.abs(distanceY) && isHorizontal == null) || isHorizontal == false) {
            isHorizontal = false
            isScroll = true
            isDrawWeek = false
            mScrollY += distanceY

            if ((e2?.y ?: 0F) > (e1?.y ?: 0F)) {
                //下滑
                if (mScrollY >= 0 && Math.abs(mScrollY) <= lineHeight * curWeekNum) {
                    isExpand = true
                    mScrollDistance = -mScrollY / curWeekNum
                    invalidate()
                }
            } else {
                if (Math.abs(mScrollY) <= lineHeight * curWeekNum) {
                    isCollapse = true
                    mScrollDistance = -mScrollY / curWeekNum
                    invalidate()
                }
            }
            if (mScrollY <= 0) {
                mScrollY = 0F
                isHorizontal = null
                isScroll = false
                if (mScrollDistance != 0F) {
                    mScrollDistance = 0F
                    invalidate()
                }
                isInterceptScroll = false
            }
            if (mScrollY >= lineHeight * curWeekNum) {
                isScroll = false
                isInterceptScroll = false
//                    isHorizontal = null
                mScrollY = lineHeight * curWeekNum.toFloat()
                mScrollDistance = -mScrollY / curWeekNum
            }
            Log.e("TAG", "isScroll: $mScrollY")
            Log.e("TAG", "isCollapse: $isCollapse")
            Log.e("TAG", "isExpand: $isExpand")
            requestLayout()
        } else if ((Math.abs(distanceX) > Math.abs(distanceY) && isHorizontal == null) || isHorizontal == true) {
            isHorizontal = true
            //左为负，右为正
            mScrollX += -distanceX
            if (mScrollX < -screenWidth) {

                mScrollX = -screenWidth.toFloat()
            }
            if (mScrollX > screenWidth) {
//                isHorizontal = null
                mScrollX = screenWidth.toFloat()
            }
            Log.e("TAG", "mScrollX: $mScrollX")
            requestLayout()
            invalidate()
        }

        return isInterceptScroll
    }

    override fun onLongPress(e: MotionEvent?) {
        isScroll = false
    }

    //绘制三屏weekview 参照之前month的绘制
    fun startDrawWeek() {
        isDrawWeek = true
        curWeekDays = Utils().getWeekDays(weekStartDate)
        nextWeekDays = Utils().getNextWeekDays(weekStartDate)
        lastWeekDays = Utils().getLastWeekDays(weekStartDate)
        invalidate()
    }

    fun drawWeek(canvas: Canvas?, dayList: MutableList<MonthEntity>, offsetX: Int) {
        drawY = lineHeight / 2
        for (row in 1..dayList.size) {
            drawX = dayWidth / 2 + dayWidth * (row - 1) + offsetX
            monthEntity = dayList[row - 1]
            solarDay = monthEntity?.solarDay ?: ""
            lunarDay = monthEntity?.lunarDay ?: ""
            solarTextWidth = solarDayPaint.measureText(solarDay)
            lunarTextWidth = lunarDayPaint.measureText(lunarDay)
            if (Utils().isCurrentDay(monthEntity?.date)) {
                var rectF = RectF(
                    drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                    (drawY - padding / 2 - padding6 - height14).toFloat(),
                    drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                    (drawY + padding / 2 + padding6 + height10).toFloat()
                )

                solarDayPaint.color = getColorFromResource(R.color.white)
                lunarDayPaint.color = getColorFromResource(R.color.white)
                selectDayBcPaint.color = getColorFromResource(R.color.color_BC0202)
                canvas?.drawRoundRect(
                    rectF,
                    raduis.toFloat(),
                    raduis.toFloat(),
                    selectDayBcPaint
                )
            } else if (Utils().isSelectDay(monthEntity?.date, selectDate)) {
                var rectF = RectF(
                    drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                    (drawY - padding / 2 - padding6 - height14).toFloat(),
                    drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                    (drawY + padding / 2 + padding6 + height10).toFloat()
                )

                solarDayPaint.color = getColorFromResource(R.color.color_E35C5C)
                lunarDayPaint.color = getColorFromResource(R.color.color_E35C5C)
                selectDayBcPaint.color = getColorFromResource(R.color.DA9E9E)
                canvas?.drawRoundRect(
                    rectF,
                    raduis.toFloat(),
                    raduis.toFloat(),
                    selectDayBcPaint
                )
            } else {
                if (monthEntity?.onTheMonth == true) {
                    solarDayPaint.color = getColorFromResource(R.color.color_1C1C1C)
                    lunarDayPaint.color = getColorFromResource(R.color.color_9A9A9A)
                } else {
                    solarDayPaint.color = getColorFromResource(R.color.color_BEBEBE)
                    lunarDayPaint.color = getColorFromResource(R.color.color_BEBEBE)
                }
            }

            canvas?.drawText(
                solarDay,
                drawX - solarTextWidth / 2,
                drawY.toFloat() - padding / 2,
                solarDayPaint
            )
            dy =
                (lunarDayFontMetrics.descent - lunarDayFontMetrics.ascent) / 2 - lunarDayFontMetrics.descent
            canvas?.drawText(
                lunarDay,
                drawX - lunarTextWidth / 2,
                drawY.toFloat() + padding / 2 + dpToPx(10),
                lunarDayPaint
            )
        }
    }

}