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
import android.util.TimeUtils
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
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
class CustomMonthView3(context: Context?, attrs: AttributeSet?) : View(context, attrs),
    GestureDetector.OnGestureListener {

    private var gestureDetector: GestureDetector = GestureDetector(context, this)
    private var selectLine: Int = 1

    //在1-selectLine之间的距离
    private var translateY: Float = 0F
    private var offsetY: Double = 0.0
    private var offsetX: Double = 0.0
    private var minTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    //当月天数集合
    var curMonthDays = mutableListOf<MonthEntity>()

    //上月天数集合
    var lastMonthDays = mutableListOf<MonthEntity>()

    //下月天数集合
    var nextMonthDays = mutableListOf<MonthEntity>()

    //当前周
    var curWeekDays = mutableListOf<MonthEntity>()

    //上周
    var lastWeekDays = mutableListOf<MonthEntity>()

    //下周
    var nextWeekDays = mutableListOf<MonthEntity>()
    var padding4 = dpToPx(4)
    var padding6 = dpToPx(6)
    var height14 = dpToPx(14)
    var height10 = dpToPx(10)
    var raduis4 = dpToPx(4)

    //阳历
    var solarDayFontMetrics: Paint.FontMetrics
    var lunarDayFontMetrics: Paint.FontMetrics

    //当月起始
    var selectDate = Date()
    var screenWidth = 0
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
    var widthMeasureSpec: Int = 0
    var heightMeasureSpec: Int = 0
    var tempHeight: Int = 0

    var dy = 0f
    var raduis = dpToPx(4)

    //上滑为正 下滑为负
    var mScrollY = 0F
    //左向右滑为正，右向左滑为负
    var mScrollX = 0F

    //变化范围0-lineheight
    var mScrollDistance = 0F
    var isScroll = false
    var isCollapse = false
    var isExpand = false
    var isDrawWeek = false
    var isTranslate = false

    //是否是水平方向滑动
    var isHorizontal: Boolean? = null
    var collapseAnimator: ValueAnimator? = null
    var horizontalAnimator: ValueAnimator? = null
    var translateAnimator: ValueAnimator? = null
    var expandAnimator: ValueAnimator? = null

    //点击天回调
    var onDaySelectCallBack: ((Date?) -> Unit)? = null

    //月份切换回调
    var onMonthSelectCallBack: ((Date?) -> Unit)? = null

    //周切换回调
    var onWeekSelectCallBack: ((Date?) -> Unit)? = null

    init {
        //初始化月
        initMonthDays();
        //初始化周
        initWeekDays()
        //获取其屏幕宽度
        screenWidth = ScreenUtils.getScreenWidth()
        //单位天的宽度
        dayWidth = screenWidth / 7
        //阳历配置
        solarDayPaint.textSize = dpToPx(14).toFloat()
        solarDayPaint.color = getColorFromResource(R.color.color_1C1C1C)
        solarDayPaint.isFakeBoldText = true
        solarDayPaint.isAntiAlias = true
        solarDayFontMetrics = solarDayPaint.fontMetrics
        //阴历配置
        lunarDayPaint.textSize = dpToPx(10).toFloat()
        lunarDayPaint.isFakeBoldText = true
        lunarDayPaint.isAntiAlias = true
        lunarDayPaint.color = getColorFromResource(R.color.color_9A9A9A)
        lunarDayFontMetrics = lunarDayPaint.fontMetrics
        //选中日期时的画笔
        selectDayBcPaint.color = getColorFromResource(R.color.DA9E9E)
        //手势监听
        gestureDetector = GestureDetector(context, this)
        resetSelectLine()
    }

    private fun resetSelectLine() {
        val instance = Calendar.getInstance()
        instance.time = Date()
        var curDay = instance.get(Calendar.DAY_OF_YEAR)
        instance.set(Calendar.DAY_OF_MONTH,1)
        var firstDay = instance.get(Calendar.DAY_OF_YEAR)
        instance.set(Calendar.DAY_OF_MONTH,instance.getActualMaximum(Calendar.DAY_OF_MONTH))
        var lastDay = instance.get(Calendar.DAY_OF_YEAR)
        if (curDay in firstDay..lastDay) {
            curMonthDays.forEachIndexed { index, monthEntity ->
                if (Utils().isCurrentDay(monthEntity.date)) {
                    selectLine = index / 7 + 1
                    Log.e("TAG", "resetSelectLine: "+selectLine)
                }
            }
        } else {
            selectLine = 1
        }
        Log.e("TAG", "resetSelectLine: "+curDay )
        Log.e("TAG", "resetSelectLine: "+firstDay )
        Log.e("TAG", "resetSelectLine: "+lastDay )
        Log.e("TAG", "resetSelectLine: "+selectLine )
    }

    private fun initWeekDays() {
        curWeekDays = Utils().getWeekDays(selectDate)
        nextWeekDays = Utils().getNextWeekDays(selectDate)
        lastWeekDays = Utils().getLastWeekDays(selectDate)
    }

    private fun initMonthDays() {
        curMonthDays.clear()
        lastMonthDays.clear()
        nextMonthDays.clear()
        curMonthDays.addAll(Utils().getMonthDays(this.selectDate))
        lastMonthDays.addAll(Utils().getLastMonthDays(this.selectDate))
        nextMonthDays.addAll(Utils().getNextMonthDays(this.selectDate))
        curWeekNum = curMonthDays.size / 7
        lastWeekNum = lastMonthDays.size / 7
        nextWeekNum = nextMonthDays.size / 7
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //在滑动范围内进行折叠和缩放
        if (isHorizontal == false) {
            if (event?.action == MotionEvent.ACTION_UP) {
                if (isScroll) {
                    if (isCollapse) {
                        startCollapseAnimator()
                    }
                    if (isExpand) {
                        startExpandAnimator()
                    }
                }
            }
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        } else if (isHorizontal == true) {
            if (event?.action == MotionEvent.ACTION_UP) {
                if (mScrollX > 0 && mScrollX <= screenWidth) {
                    startLeftScrollToRight()
                } else if (mScrollX < 0 && -mScrollX <= screenWidth) {
                    startRightScrollToLeft()
                }
            }
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        } else {
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        }
    }

    private fun startLeftScrollToRight() {
        horizontalAnimator = ValueAnimator.ofFloat(mScrollX, screenWidth.toFloat())
        horizontalAnimator?.addUpdateListener {
            mScrollX = it.animatedValue as Float
            invalidate()
        }
        horizontalAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {

                if (isDrawWeek) {
                    //获取周
                    selectDate = Utils().getLastWeek(selectDate)
                    curWeekDays = Utils().getWeekDays(selectDate)
                    selectDate = curWeekDays[Utils().getWeekNum(selectDate)].date
                    selectLine = Utils().getSelectLine(selectDate)
                    nextWeekDays = Utils().getNextWeekDays(selectDate)
                    lastWeekDays = Utils().getLastWeekDays(selectDate)
                    //获取月
                    curMonthDays = Utils().getMonthDays(selectDate)
                    lastMonthDays = Utils().getLastMonthDays(selectDate)
                    nextMonthDays = Utils().getNextMonthDays(selectDate)

                    onWeekSelectCallBack?.invoke(selectDate)
                } else {
                    selectLine = 1
                    //获取周
                    selectDate = Utils().getLastMonth(selectDate)
                    selectDate = Utils().getFirstDayOfmonth(selectDate)
                    if (Utils().isInCurrentMonth(selectDate)) {
                        selectDate = Date()
                    }
                    curWeekDays = Utils().getWeekDays(selectDate)
                    nextWeekDays = Utils().getNextWeekDays(selectDate)
                    lastWeekDays = Utils().getLastWeekDays(selectDate)
                    //获取月
                    curMonthDays = Utils().getMonthDays(selectDate)
                    lastMonthDays = Utils().getLastMonthDays(selectDate)
                    nextMonthDays = Utils().getNextMonthDays(selectDate)

                    onMonthSelectCallBack?.invoke(selectDate)
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

    private fun startRightScrollToLeft() {
        horizontalAnimator = ValueAnimator.ofFloat(mScrollX, -screenWidth.toFloat())
        horizontalAnimator?.addUpdateListener {
            mScrollX = it.animatedValue as Float
            invalidate()
        }
        horizontalAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (isDrawWeek) {
                    selectDate = Utils().getNextWeek(selectDate)
                    curWeekDays = Utils().getWeekDays(selectDate)
                    selectDate = curWeekDays[Utils().getWeekNum(selectDate)].date
                    nextWeekDays = Utils().getNextWeekDays(selectDate)
                    lastWeekDays = Utils().getLastWeekDays(selectDate)
                    selectLine = Utils().getSelectLine(selectDate)
                    curMonthDays = Utils().getMonthDays(selectDate)
                    lastMonthDays = Utils().getLastMonthDays(selectDate)
                    nextMonthDays = Utils().getNextMonthDays(selectDate)
                    onWeekSelectCallBack?.invoke(selectDate)
                } else {
                    selectDate = Utils().getNextMonth(selectDate)
                    selectDate = Utils().getFirstDayOfmonth(selectDate)
                    if (Utils().isInCurrentMonth(selectDate)) {
                        selectDate = Date()
                    }
                    curMonthDays = Utils().getMonthDays(selectDate)
                    lastMonthDays = Utils().getLastMonthDays(selectDate)
                    nextMonthDays = Utils().getNextMonthDays(selectDate)
                    selectLine = 1
                    if (Utils().isInCurrentMonth(curMonthDays[curMonthDays.size/2].date)){
                        selectLine = Utils().getSelectLine(Date())
                    }
                    curWeekDays = Utils().getWeekDays(selectDate)
                    nextWeekDays = Utils().getNextWeekDays(selectDate)
                    lastWeekDays = Utils().getLastWeekDays(selectDate)
                    onMonthSelectCallBack?.invoke(selectDate)
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

    private fun startExpandAnimator() {
//        selectLine = Utils().getSelectLine(selectDate)
        expandAnimator = ValueAnimator.ofFloat(mScrollDistance, 0F)
        expandAnimator?.addUpdateListener {
            mScrollDistance = it.animatedValue as Float
            requestLayout()
            invalidate()
        }
        expandAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mScrollDistance = 0F
                translateAnimator?.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        isExpand = false
        expandAnimator?.setDuration(300)
        translateAnimator = ValueAnimator.ofFloat(translateY, 0F)
        translateAnimator?.setDuration(300)
        translateAnimator?.addUpdateListener {
            translateY =  (it.animatedValue as Float)
            requestLayout()
            invalidate()
        }
        translateAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
//                expandAnimator?.start()
                mScrollY = 0F
//                selectLine = Utils().getSelectLine(selectDate)
                translateY = 0F
                invalidate()
                requestLayout()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        if (!isTranslate){
            translateAnimator?.start()
        }else {
            expandAnimator?.start()

        }
    }

    private fun startCollapseAnimator() {
        collapseAnimator =
            ValueAnimator.ofFloat(mScrollDistance, -(lineHeight).toFloat())
        collapseAnimator?.addUpdateListener {
            mScrollDistance = it.animatedValue as Float
            requestLayout()
            invalidate()
        }

        isCollapse = false
        collapseAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isDrawWeek = true
                mScrollY = lineHeight * (curWeekNum).toFloat()
                translateY = -lineHeight * (selectLine - 1).toFloat()
                mScrollDistance =  -(lineHeight).toFloat()
                invalidate()
                requestLayout()
//                startDrawWeek()
//                mScrollY = lineHeight * curWeekNum.toFloat()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        collapseAnimator?.setDuration(300)
        if (isTranslate){
            translateAnimator = ValueAnimator.ofFloat(translateY, -(lineHeight*(selectLine - 1)).toFloat())
            translateAnimator?.setDuration(300)
            translateAnimator?.addUpdateListener {
                translateY =  (it.animatedValue as Float)
                requestLayout()
                invalidate()
            }
            translateAnimator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    collapseAnimator?.start()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            translateAnimator?.start()
        }else {
            collapseAnimator?.start()
        }

    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        this.widthMeasureSpec = MeasureSpec.makeMeasureSpec(
            screenWidth,
            MeasureSpec.AT_MOST
        )
        tempHeight = 0
//        tempHeight = measureViewGroupHeight(this)
        if (!isDrawWeek) {
            tempHeight = lineHeight * curWeekNum + translateY.toInt() + mScrollDistance.toInt()*(curWeekNum - selectLine+1).toInt()
            this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                if (tempHeight < lineHeight) lineHeight else tempHeight,
                MeasureSpec.AT_MOST
            )
        } else {
            this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                lineHeight + tempHeight,
                MeasureSpec.AT_MOST
            )
        }
        Log.e("TAG", "onMeasure: " + MeasureSpec.getSize(this.heightMeasureSpec))
        super.onMeasure(this.widthMeasureSpec, this.heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        if (!isDrawWeek) {
            //绘制当前月
            onDrawMonth(canvas, curWeekNum, 0 + mScrollX.toInt(), curMonthDays)
            //绘制上个月
            onDrawMonth(canvas, lastWeekNum, -screenWidth + mScrollX.toInt(), lastMonthDays)
            //绘制下个月
            onDrawMonth(canvas, nextWeekNum, screenWidth + mScrollX.toInt(), nextMonthDays)
        } else {
            onDrawWeek(canvas, curWeekDays, 0 + mScrollX.toInt())
            onDrawWeek(canvas, lastWeekDays, -screenWidth + mScrollX.toInt())
            onDrawWeek(canvas, nextWeekDays, screenWidth + mScrollX.toInt())
        }
        super.onDraw(canvas)
    }

    private fun onDrawMonth(
        canvas: Canvas?,
        weekNum: Int,
        offsetX: Int,
        monthDays: MutableList<MonthEntity>
    ) {
        for (column in weekNum downTo 1) {
            //倒序画周
            drawY = (lineHeight / 2 + (column - 1) * lineHeight) + translateY.toInt()
            if (column > selectLine) {
                drawY += (mScrollDistance * (column - selectLine)).toInt()
            }
            for (row in 1..7) {
                //顺序画日
                drawX = dayWidth / 2 + dayWidth * (row - 1) + offsetX
                monthEntity = monthDays[(column - 1) * 7 + row - 1]
                solarDay = monthEntity?.solarDay ?: ""
                lunarDay = monthEntity?.lunarDay ?: ""
                solarTextWidth = solarDayPaint.measureText(solarDay)
                lunarTextWidth = lunarDayPaint.measureText(lunarDay)
                //column*row >=6不在月初那几天
                if (Utils().isCurrentDay(monthEntity?.date) &&
                    Utils().isSameMonth(selectDate,monthEntity?.date)) {
                    //绘制当前天
                    var rectF = RectF(
                        drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                        (drawY - padding4 / 2 - padding6 - height14).toFloat(),
                        drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                        (drawY + padding4 / 2 + padding6 + height10).toFloat()
                    )
                    if (column > selectLine && Math.abs(mScrollDistance) > 0) {
                        solarDayPaint.setColor(
                            Color.argb(
                                ((1 - (Math.abs(mScrollDistance) / lineHeight)) * 255).toInt(),
                                255,
                                255,
                                255
                            )
                        )
                        lunarDayPaint.setColor(
                            Color.argb(
                                ((1 - (Math.abs(mScrollDistance) / lineHeight)) * 255).toInt(),
                                255,
                                255,
                                255
                            )
                        )
                        selectDayBcPaint.setColor(
                            Color.argb(
                                ((1 - (Math.abs(mScrollDistance) / lineHeight)) * 255).toInt(),
                                188,
                                2,
                                2
                            )
                        )
                        Log.e("TAG", "onDrawMonth: "+(1 - (Math.abs(mScrollDistance) / lineHeight)) * 255)
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
                } else if (Utils().isSelectDay(monthEntity?.date, this.selectDate)) {
                    //绘制选中的天
                    var rectF = RectF(
                        drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                        (drawY - padding4 / 2 - padding6 - height14).toFloat(),
                        drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                        (drawY + padding4 / 2 + padding6 + height10).toFloat()
                    )
                    if (column > selectLine && Math.abs(mScrollDistance) > 0) {
                        solarDayPaint.setColor(
                            Color.argb(
                                ((1 - (Math.abs(mScrollDistance) / lineHeight)) * 255).toInt(),
                                227,
                                92,
                                92
                            )
                        )
                        lunarDayPaint.setColor(
                            Color.argb(
                                ((1 - (Math.abs(mScrollDistance) / lineHeight)) * 255).toInt(),
                                227,
                                92,
                                92
                            )
                        )
                        selectDayBcPaint.setColor(
                            Color.argb(
                                ((1 - (Math.abs(mScrollDistance) / lineHeight)) * 255).toInt(),
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
                    //绘制其他天
                    if (monthEntity?.onTheMonth == true) {
                        //展示范围数与当前月的
                        if (column > selectLine && Math.abs(mScrollDistance) > 0) {
                            solarDayPaint.setColor(
                                Color.argb(
                                    ((1 - Math.abs(mScrollDistance) / lineHeight) * 255).toInt(),
                                    28,
                                    28,
                                    28
                                )
                            )
                            lunarDayPaint.setColor(
                                Color.argb(
                                    ((1 - Math.abs(mScrollDistance) / lineHeight) * 255).toInt(),
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
                        //不是当前月的天
                        if (column > selectLine && Math.abs(mScrollDistance) > 0) {
                            solarDayPaint.setColor(
                                Color.argb(
                                    ((1 - Math.abs(mScrollDistance) / lineHeight) * 155).toInt(),
                                    190,
                                    190,
                                    190
                                )
                            )
                            lunarDayPaint.setColor(
                                Color.argb(
                                    ((1 - Math.abs(mScrollDistance) / lineHeight) * 155).toInt(),
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
                    drawY.toFloat() - padding4 / 2,
                    solarDayPaint
                )
                dy =
                    (lunarDayFontMetrics.descent - lunarDayFontMetrics.ascent) / 2 - lunarDayFontMetrics.descent
                canvas?.drawText(
                    lunarDay,
                    drawX - lunarTextWidth / 2,
                    drawY.toFloat() + padding4 / 2 + dpToPx(10),
                    lunarDayPaint
                )
            }
        }
    }

    //绘制三屏weekview 参照之前month的绘制
    fun startDrawWeek() {
        isDrawWeek = true
        curWeekDays = Utils().getWeekDays(selectDate)
        nextWeekDays = Utils().getNextWeekDays(selectDate)
        lastWeekDays = Utils().getLastWeekDays(selectDate)
        invalidate()
    }

    fun onDrawWeek(canvas: Canvas?, dayList: MutableList<MonthEntity>, offsetX: Int) {
        drawY = lineHeight / 2
        for (row in 1..dayList.size) {
            drawX = dayWidth / 2 + dayWidth * (row - 1) + offsetX
            monthEntity = dayList[row - 1]
            solarDay = monthEntity?.solarDay ?: ""
            lunarDay = monthEntity?.lunarDay ?: ""
            solarTextWidth = solarDayPaint.measureText(solarDay)
            lunarTextWidth = lunarDayPaint.measureText(lunarDay)
            if (Utils().isCurrentDay(monthEntity?.date) &&
                Utils().isSameMonth(selectDate,monthEntity?.date)) {
                var rectF = RectF(
                    drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                    (drawY - padding4 / 2 - padding6 - height14).toFloat(),
                    drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                    (drawY + padding4 / 2 + padding6 + height10).toFloat()
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
            } else if (Utils().isSelectDay(monthEntity?.date, this.selectDate)) {
                var rectF = RectF(
                    drawX - Math.max(solarTextWidth, lunarTextWidth) / 2 - padding6,
                    (drawY - padding4 / 2 - padding6 - height14).toFloat(),
                    drawX + padding6 + Math.max(solarTextWidth, lunarTextWidth) / 2,
                    (drawY + padding4 / 2 + padding6 + height10).toFloat()
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
                drawY.toFloat() - padding4 / 2,
                solarDayPaint
            )
            dy =
                (lunarDayFontMetrics.descent - lunarDayFontMetrics.ascent) / 2 - lunarDayFontMetrics.descent
            canvas?.drawText(
                lunarDay,
                drawX - lunarTextWidth / 2,
                drawY.toFloat() + padding4 / 2 + dpToPx(10),
                lunarDayPaint
            )
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

    }


    override fun onShowPress(e: MotionEvent?) {
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
                    this.selectDate = selectMonthEntity!!.date
                    invalidate()
                    initWeekDays()
                }
                Log.e("TAG", "onSingleTapUp: "+selectMonthEntity?.onTheMonth )
                if (selectMonthEntity?.onTheMonth == true) {
                    onDaySelectCallBack?.invoke(selectMonthEntity?.date)
                }
                return true
            }
        } else {
            if (((event?.x ?: 0F) <= screenWidth) && (event?.y ?: 0F) < lineHeight * curWeekNum) {
                var col = (((event?.y ?: 0F) / lineHeight) + 1).toInt()
                var row = (((event?.x ?: 0F) / dayWidth)).toInt()
                selectMonthEntity =
                    curMonthDays[((col - 1) * 7) + row]
                if (selectMonthEntity?.onTheMonth == true) {
                    selectLine = col
                    this.selectDate = selectMonthEntity!!.date
                    initWeekDays()
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
        return false;
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (isDrawWeek && Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > minTouchSlop) {
            isHorizontal = true
        }
        if ((Math.abs(distanceX) < Math.abs(distanceY) && isHorizontal == null
                    && Math.abs(distanceY) > minTouchSlop) || isHorizontal == false) {
            //竖直方向滑动 控制是平移还是渐变透明度变化
            isHorizontal = false
            isScroll = true
            isDrawWeek = false
            mScrollY += distanceY
            Log.e("TAG", "onScroll: $mScrollY")
            if ((e2?.y ?: 0F) > (e1?.y ?: 0F)) {
                //下滑
                if (mScrollY >= 0 && Math.abs(mScrollY) <= lineHeight * curWeekNum) {
                    isExpand = true
                    isCollapse = false
                    //在日历绘制的区间范围内
                    if (Math.abs(mScrollY) <= lineHeight * (selectLine - 1)) {
                        //如果小于这个高度值时，做向上平移移动
                        translateY = -mScrollY
                        mScrollDistance = 0F
                        isTranslate = false
                        requestLayout()
                        invalidate()
                    } else if (Math.abs(mScrollDistance) <= lineHeight) {
                        translateY = -lineHeight * (selectLine - 1).toFloat()
                        if (selectLine == curWeekNum) {
                            mScrollDistance = 0F
                        } else {
                            mScrollDistance =
                                -(mScrollY - lineHeight * (selectLine - 1)) / (curWeekNum - selectLine)
                        }

                        if (Math.abs(mScrollDistance) > lineHeight) {
                            mScrollDistance = -lineHeight.toFloat()
                        }
                        isTranslate = true
                        requestLayout()
                        invalidate()
                    }else{
                        translateY = 0F
                        mScrollDistance = 0F
                    }
                } else {
                    isExpand = false
                }
            } else {
                //上滑
                isExpand = false
                isCollapse = true
                if (Math.abs(mScrollY) <= lineHeight * curWeekNum) {
                    //在日历绘制的区间范围内
                    if (Math.abs(mScrollY) <= lineHeight * (selectLine - 1)) {
                        //如果小于这个高度值时，做向上平移移动
                        translateY = -mScrollY
                        mScrollDistance = 0F

                        isTranslate = true
                        requestLayout()
                        invalidate()
                    } else if (Math.abs(mScrollDistance) <= lineHeight) {
                        translateY = -lineHeight * (selectLine - 1).toFloat()
                        if (selectLine == curWeekNum) {
                            mScrollDistance = 0F
                        } else {
                            mScrollDistance =
                                -(mScrollY - lineHeight * (selectLine - 1)) / (curWeekNum - selectLine)
                        }
                        isTranslate = false
                        if (Math.abs(mScrollDistance) > lineHeight) {
                            mScrollDistance = -lineHeight.toFloat()
                        }
                        requestLayout()
                        invalidate()
                    }
                } else {
                    isCollapse = false
                }
            }
            if (mScrollY <= 0) {
                mScrollY = 0F
                isHorizontal = null
                isScroll = false
                if (mScrollDistance != 0F) {
                    mScrollDistance = 0F
                    requestLayout()
                    invalidate()
                }

            }
            if (mScrollY >= lineHeight * (curWeekNum)) {
                isScroll = false
                mScrollY = lineHeight * (curWeekNum).toFloat()
                requestLayout()
                //移动内容
            }
        } else if ((Math.abs(distanceX) > Math.abs(distanceY) && isHorizontal == null
                    && Math.abs(distanceX) > minTouchSlop) || isHorizontal == true
        ) {
            //水平方向滑动
            isHorizontal = true
            //左为负，右为正
            mScrollX += -distanceX
            Log.e("", "onScroll: mScrollX"+mScrollX )
            if (mScrollX < -screenWidth) {
                mScrollX = -screenWidth.toFloat()
            }
            if (mScrollX > screenWidth) {
                mScrollX = screenWidth.toFloat()
            }
            requestLayout()
            invalidate()
        }
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }


    fun selectDayCallBack(callBack: (Date?) -> Unit) {
        this.onDaySelectCallBack = callBack
    }

    fun selectMonthCallBack(callBack: (Date?) -> Unit) {
        this.onMonthSelectCallBack = callBack
    }

    fun selectWeekCallBack(callBack: (Date?) -> Unit) {
        this.onWeekSelectCallBack = callBack
    }
}