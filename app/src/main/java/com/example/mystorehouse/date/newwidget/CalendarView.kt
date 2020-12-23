package com.example.mystorehouse.date.newwidget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
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
class CalendarView(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs),
    GestureDetector.OnGestureListener {

    private var gestureDetector: GestureDetector = GestureDetector(context, this)
    private var selectLine: Int = 1

    //在1-selectLine之间的距离
    private var translateY: Float = 0F
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
    var realHeight = 0

    var dy = 0f
    var raduis = dpToPx(4)

    //上滑为正 下滑为负
    var mScrollY = 0F

    //上滑为负 下滑为正
    var mContentScrollY = 0F

    //左向右滑为正，右向左滑为负
    var mScrollX = 0F

    //变化范围0-lineheight 负值
    var mScrollDistance = 0F
    var isScroll = false
    var isCollapse = false
    var isExpand = false
    var isDrawWeek = false
    var isTranslate = false
    var isCollapseOver = false
    var isNeedScrollContent = false
    var isTopScrollToBottom: Boolean? = null

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
    var whiteBcRect = RectF()
    var whiteBcPaint = Paint()

    var calenderType: String = CalenderType.TYPE_TRANSLATION
    var lastCalenderType: String = CalenderType.TYPE_TRANSLATION

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
        setWillNotDraw(false)
        whiteBcPaint.setColor(Color.WHITE)
        whiteBcRect =
            RectF(-screenWidth.toFloat(), 0F, 2 * screenWidth.toFloat(), lineHeight.toFloat())
    }

    private fun resetSelectLine() {
        val instance = Calendar.getInstance()
        instance.time = Date()
        var curDay = instance.get(Calendar.DAY_OF_YEAR)
        instance.set(Calendar.DAY_OF_MONTH, 1)
        var firstDay = instance.get(Calendar.DAY_OF_YEAR)
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH))
        var lastDay = instance.get(Calendar.DAY_OF_YEAR)
        if (curDay in firstDay..lastDay) {
            curMonthDays.forEachIndexed { index, monthEntity ->
                if (Utils().isCurrentDay(monthEntity.date)) {
                    selectLine = index / 7 + 1
                }
            }
        } else {
            selectLine = 1
        }

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
                Log.e("TAG", "onTouchEvent: isTopScrollToBottom "+isTopScrollToBottom )
                if (isTopScrollToBottom == true) {
                    if(calenderType == CalenderType.TYPE_TRANSLATION){
                        isTranslate = false
                        startExpandAnimator()
                    }else if(calenderType == CalenderType.TYPE_EXPAND){
                        isTranslate = true
                        startExpandAnimator()
                    }else if(calenderType == CalenderType.TYPE_SCROLL_CONTENT){

                    }

                } else if (isTopScrollToBottom == false) {
                    if(calenderType == CalenderType.TYPE_TRANSLATION){
                        isTranslate = true
                        startCollapseAnimator()
                    }else if(calenderType == CalenderType.TYPE_COLLAPSE){
                        isTranslate = false
                        startCollapseAnimator()
                    }else if(calenderType == CalenderType.TYPE_SCROLL_CONTENT){

                    }
                } else {

                }
//                if (isScroll && !isNeedScrollContent) {
//                    if (isCollapse) {
//                        startCollapseAnimator()
//                    }
//                    if (isExpand) {
//                        startExpandAnimator()
//                    }
//                }
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
            Log.e("TAG", "onTouchEvent: " +(event?.action == MotionEvent.ACTION_UP))
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        } else {
            val onTouchEvent = gestureDetector.onTouchEvent(event)
            return onTouchEvent
        }
    }

    //从左向右滑动
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
                isAnimatorStart = false
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
                    if (Utils().isInCurrentMonth(curMonthDays[curMonthDays.size / 2].date)) {
                        selectLine = Utils().getSelectLine(Date())
                    }

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
                isAnimatorStart = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimatorStart = true
            }
        })
        horizontalAnimator?.setDuration(500)
        horizontalAnimator?.start()
    }

    //从右向左滑动
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
                isAnimatorStart = false
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
                    if (Utils().isInCurrentMonth(curMonthDays[curMonthDays.size / 2].date)) {
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
                isAnimatorStart = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimatorStart = true
            }
        })
        horizontalAnimator?.setDuration(500)
        horizontalAnimator?.start()
    }

    //展开动画（包括平移）
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
                translateAnimator?.start()
                calenderType = CalenderType.TYPE_TRANSLATION
            }

            override fun onAnimationCancel(animation: Animator?) {
                isAnimatorStart = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isNeedScrollContent = false
                isAnimatorStart = true
            }
        })

        isExpand = false
        expandAnimator?.setDuration(100)
        translateAnimator = ValueAnimator.ofFloat(translateY, 0F)
        translateAnimator?.setDuration(100)
        translateAnimator?.addUpdateListener {
            translateY = (it.animatedValue as Float)
            requestLayout()
            invalidate()
        }
        translateAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                calenderType = CalenderType.TYPE_EXPAND
                mScrollY = 0F
                translateY = 0F
                mScrollDistance = 0F
                invalidate()
                requestLayout()
                isAnimatorStart = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isAnimatorStart = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isNeedScrollContent = false
                isAnimatorStart = true
                Log.e("TAG", "onAnimationStart: isNeedScrollContent = false 0")
            }
        })

        if (!isTranslate) {
            translateAnimator?.start()
        } else {
            expandAnimator?.start()

        }
    }

    //收缩动画（包括平移）
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
                Log.e("TAG", "onAnimationEnd: " )
                isDrawWeek = true
                calenderType = CalenderType.TYPE_SCROLL_CONTENT
                mScrollY = lineHeight * (curWeekNum-1).toFloat()
                translateY = -lineHeight * (selectLine - 1).toFloat()
                mScrollDistance = -(lineHeight).toFloat()
                invalidate()
                requestLayout()
                isCollapseOver = true
                isNeedScrollContent = true
                isAnimatorStart = false
//                startDrawWeek()
//                mScrollY = lineHeight * curWeekNum.toFloat()
            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.e("TAG", "onAnimationCancel: " )
                isAnimatorStart = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimatorStart = true
                Log.e("TAG", "onAnimationStart: " )
            }
        })
        collapseAnimator?.setDuration(100)
        if (isTranslate) {
            translateAnimator =
                ValueAnimator.ofFloat(translateY, -(lineHeight * (selectLine - 1)).toFloat())
            translateAnimator?.setDuration(100)
            translateAnimator?.addUpdateListener {
                translateY = (it.animatedValue as Float)
                requestLayout()
                invalidate()
            }
            translateAnimator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    collapseAnimator?.start()
                    calenderType = CalenderType.TYPE_COLLAPSE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isAnimatorStart = false
                }

                override fun onAnimationStart(animation: Animator?) {
                    isAnimatorStart = true
                }
            })
            translateAnimator?.start()
        } else {
            collapseAnimator?.start()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (realHeight == 0) {
            realHeight = MeasureSpec.getSize(heightMeasureSpec)
            Log.e("TAG", "onMeasure: realHeight = " + realHeight)
        }
        this.widthMeasureSpec = MeasureSpec.makeMeasureSpec(
            screenWidth,
            MeasureSpec.AT_MOST
        )
        tempHeight = 0
        allChildHeight = 0
        if (childCount > 0) {
            for (i in 0..childCount) {
                val childAt = getChildAt(i)
                if (childAt is ViewGroup) {
                    childAt.measure(
                        MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY)
                        , MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    allChildHeight += childAt.measuredHeight
                }
            }
        }

        if (!isDrawWeek) {
            tempHeight =
                lineHeight * curWeekNum + translateY.toInt() +
                        mScrollDistance.toInt() * (curWeekNum - selectLine) + allChildHeight
            this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                if (tempHeight < lineHeight) lineHeight else tempHeight,
                MeasureSpec.EXACTLY
            )
        } else {
            this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                realHeight,
                MeasureSpec.AT_MOST
            )
        }
//        super.onMeasure(this.widthMeasureSpec, this.heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (!isDrawWeek) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                whiteBcRect = RectF(
                    -screenWidth.toFloat(),
                    0F,
                    2 * screenWidth.toFloat(),
                    tempHeight.toFloat()
                )
                canvas?.saveLayer(whiteBcRect, whiteBcPaint)
                canvas?.drawRect(whiteBcRect, whiteBcPaint)
                //绘制当前月
                onDrawMonth(canvas, curWeekNum, 0 + mScrollX.toInt(), curMonthDays)
//            canvas?.drawColor(Color.WHITE)
                //绘制上个月
                onDrawMonth(canvas, lastWeekNum, -screenWidth + mScrollX.toInt(), lastMonthDays)
                //绘制下个月
                onDrawMonth(canvas, nextWeekNum, screenWidth + mScrollX.toInt(), nextMonthDays)
                canvas?.restore()
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                whiteBcRect =
                    RectF(
                        -screenWidth.toFloat(),
                        0F,
                        2 * screenWidth.toFloat(),
                        lineHeight.toFloat()
                    )
                canvas?.saveLayer(whiteBcRect, whiteBcPaint)
                canvas?.drawRect(whiteBcRect, whiteBcPaint)
                onDrawWeek(canvas, curWeekDays, 0 + mScrollX.toInt())
                onDrawWeek(canvas, lastWeekDays, -screenWidth + mScrollX.toInt())
                onDrawWeek(canvas, nextWeekDays, screenWidth + mScrollX.toInt())
                canvas?.restore()
            }

        }
    }

    private fun onDrawMonth(
        canvas: Canvas?,
        weekNum: Int,
        offsetX: Int,
        monthDays: MutableList<MonthEntity>
    ) {
        Log.e("TAG", "onDrawMonth: ")
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
                    Utils().isSameMonth(selectDate, monthEntity?.date)
                ) {
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
                Utils().isSameMonth(selectDate, monthEntity?.date)
            ) {
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

    var allChildHeight = 0

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            for (i in 0..childCount) {
                val childAt = getChildAt(i)
                if (childAt is ViewGroup) {
                    childAt.measure(
                        MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY)
                        , MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    if (!isDrawWeek) {
                        tempHeight =
                            lineHeight * curWeekNum + translateY.toInt() + mScrollDistance.toInt() * (curWeekNum - selectLine).toInt()
                        tempHeight = if (tempHeight < lineHeight) lineHeight else tempHeight

                        childAt.layout(
                            0,
                            tempHeight + mContentScrollY.toInt()
                            ,
                            screenWidth,
                            tempHeight + childAt.measuredHeight + mContentScrollY.toInt()
                        )
                    } else {
                        childAt.layout(
                            0, -lineHeight+ mContentScrollY.toInt(), screenWidth,
                            childAt.measuredHeight - lineHeight +  mContentScrollY.toInt()
                        )
                    }
                }
            }
        }
    }


    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(event: MotionEvent?): Boolean {
        isScroll = false
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
                    onDaySelectCallBack?.invoke(selectMonthEntity?.date)
                } else {
                    if (selectMonthEntity?.date?.time ?: 0 > selectDate.time) {
                        startRightScrollToLeft()
                    } else {
                        startLeftScrollToRight()
                    }
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
                    onDaySelectCallBack?.invoke(selectMonthEntity?.date)
                } else {
                    if (selectMonthEntity?.date?.time ?: 0 > selectDate.time) {
                        startRightScrollToLeft()
                    } else {
                        startLeftScrollToRight()
                    }
                }
                return true
            }
        }
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        //事件起始重置滑动方向
        isHorizontal = null
        return true
    }


    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (calenderType == CalenderType.TYPE_SCROLL_CONTENT && isHorizontal == false){
            var lastContentScrollY = mContentScrollY
            mContentScrollY += ((e2?.y ?: 0F) - (e1?.y ?: 0F))
            if (mContentScrollY < 0 && Math.abs(mContentScrollY) >= allChildHeight - realHeight + lineHeight) {
                mContentScrollY = -allChildHeight + realHeight.toFloat() - lineHeight
            }
            if (Math.abs(mContentScrollY) > allChildHeight - realHeight - lineHeight) {
                mContentScrollY = -(allChildHeight - realHeight - lineHeight).toFloat()
            }
            if (mContentScrollY > 0) {
                mContentScrollY = 0F
                isNeedScrollContent = false
                mScrollY = lineHeight * (curWeekNum-1).toFloat()
                translateY = -lineHeight * (selectLine - 1).toFloat()
                mScrollDistance = -(lineHeight).toFloat()
            }
            startFlingAnimator(lastContentScrollY, mContentScrollY)
        }
        return calenderType == CalenderType.TYPE_SCROLL_CONTENT
    }

    var flingAnimator: ValueAnimator? = null
    var isAnimatorStart = false
    private fun startFlingAnimator(lastContentScrollY: Float, mContentScrollY: Float) {
        if (flingAnimator?.isStarted == true) {
            flingAnimator?.cancel()
        }
        flingAnimator = ValueAnimator.ofFloat(lastContentScrollY, mContentScrollY)
        flingAnimator?.interpolator = AccelerateDecelerateInterpolator()
        flingAnimator?.setDuration(300)
        flingAnimator?.addUpdateListener {
            this.mContentScrollY = it.animatedValue as Float
            invalidate()
            requestLayout()
        }
        flingAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isAnimatorStart = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isAnimatorStart = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimatorStart = true
            }
        })
        flingAnimator?.start()
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (isAnimatorStart) {
            return false
        }
        if (isHorizontal == null){
            isHorizontal = if(Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > minTouchSlop){
                true
            }else if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > minTouchSlop){
                false
            }else {
                null
            }
        }
        if (isHorizontal == false) {
            //竖直方向滑动 控制是平移还是渐变透明度变化
            isHorizontal = false
            mScrollY += distanceY
            if (mScrollY < 0) {
                mScrollY = 0F
            }
            if (mScrollY > allChildHeight + lineHeight*curWeekNum) {
                mScrollY = allChildHeight + lineHeight*curWeekNum.toFloat()
            }
            if ((e2?.y ?: 0F) > (e1?.y ?: 0F)) {
                isDrawWeek = false
                //下滑
                if (Math.abs(mScrollY) <= lineHeight * (selectLine - 1) && mContentScrollY==0F) {
                    isTopScrollToBottom = true
                    calenderType = CalenderType.TYPE_TRANSLATION
                    translateY = -mScrollY
                    mScrollDistance = 0F
                    invalidate()
                    requestLayout()
                } else if (Math.abs(mScrollY) > lineHeight * (selectLine - 1) &&
                    Math.abs(mScrollY) <= lineHeight * (curWeekNum-1) && mContentScrollY == 0.0F
                ) {

                    isTopScrollToBottom = true
                    calenderType = CalenderType.TYPE_EXPAND
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
                    invalidate()
                    requestLayout()

                } else {
                    isDrawWeek = true
                    isTopScrollToBottom = null
                    calenderType = CalenderType.TYPE_SCROLL_CONTENT
                    translateY = -lineHeight * (selectLine - 1).toFloat()
                    mScrollDistance = -lineHeight.toFloat()
                    mContentScrollY -= distanceY
                    if (mContentScrollY > 0F) {
                        mContentScrollY = 0F
                        mScrollY = (curWeekNum - 1) * lineHeight.toFloat();
                    }
                    invalidate()
                    requestLayout()

                }
            } else {
                //上滑
                isDrawWeek = false
                if (Math.abs(mScrollY) <= lineHeight * (selectLine - 1)&& mContentScrollY==0F) {
                    isTopScrollToBottom = false
                    //做平移操作
                    calenderType = CalenderType.TYPE_TRANSLATION
                    translateY = -mScrollY
                    mScrollDistance = 0F
                    invalidate()
                    requestLayout()
                } else if (Math.abs(mScrollY) > lineHeight * (selectLine - 1) &&
                    Math.abs(mScrollY) <= lineHeight * (curWeekNum -1) && mContentScrollY==0F
                ) {
                    calenderType = CalenderType.TYPE_COLLAPSE
                    translateY = -lineHeight * (selectLine - 1).toFloat()
                    if (selectLine == curWeekNum) {
                        mScrollDistance = 0F
                    } else {
                        mScrollDistance =
                            -(mScrollY - lineHeight * (selectLine - 1)) / (curWeekNum - selectLine)
                    }
                    isTopScrollToBottom = false
                    invalidate()
                    requestLayout()
                } else {
                    isDrawWeek = true
                    isTopScrollToBottom = null
                    calenderType = CalenderType.TYPE_SCROLL_CONTENT
                    translateY = -lineHeight * (selectLine - 1).toFloat()
                    mScrollDistance = -lineHeight.toFloat()
                    mContentScrollY -= distanceY
                    if (mContentScrollY > 0F) {
                        mContentScrollY = 0F
                        mScrollY = (curWeekNum - 1) * lineHeight.toFloat();
                    }
                    if (Math.abs(mContentScrollY) > allChildHeight - realHeight - lineHeight) {
                        mContentScrollY = -(allChildHeight - realHeight - lineHeight).toFloat()
                    }
                    invalidate()
                    requestLayout()
                }
            }
        } else if (isHorizontal == true) {
            isTopScrollToBottom = null
            var canMove = false
            if (!isDrawWeek) {
                if (((e2?.y) ?: 0F) < tempHeight.toFloat() && ((e1?.y)
                        ?: 0F) < tempHeight.toFloat()
                ) {
                    canMove = true
                }
            } else {
                if (((e2?.y) ?: 0F) < lineHeight.toFloat() && ((e1?.y)
                        ?: 0F) < lineHeight.toFloat()
                ) {
                    canMove = true
                }
            }
            if (canMove) {
                //水平方向滑动
                isHorizontal = true
                //左为负，右为正
                mScrollX += -distanceX
                if (mScrollX < -screenWidth) {
                    mScrollX = -screenWidth.toFloat()
                }
                if (mScrollX > screenWidth) {
                    mScrollX = screenWidth.toFloat()
                }
                invalidate()
                requestLayout()
            }
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