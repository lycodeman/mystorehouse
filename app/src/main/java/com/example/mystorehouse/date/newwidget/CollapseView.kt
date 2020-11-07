package com.example.mystorehouse.date.newwidget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.example.mystorehouse.R
import kotlinx.android.synthetic.main.layout_collapse_viewgroup.view.*


/**
 *     Author : 李勇
 *     Create Time   : 2020/11/04
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.newwidget
 */

class CollapseView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    var startPointX = 0F;
    var startPointY = 0F;
    var mScrollY = 0F;
    var collapseHeight = 0
    var scrollDistance = 200;
    var weekViewHeight = 0;
    var isIntercepted = false
    var smallTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_collapse_viewgroup,this)
        collapse_view.post {
            collapseHeight = collapse_view.measuredHeight
            weekViewHeight = week_view.measuredHeight
        }

        month_view.setOnTouchListener(object : OnTouchListener{

            var startX = 0F
            var startY = 0F
            var tempScrollY = 0F
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN){
                    startX = event.x
                    startY = event.y
//            isIntercepted = true
                }else if (event?.action == MotionEvent.ACTION_MOVE){
                    //y方向的滑动才处理
                    tempScrollY += event.y - startY
                    if ((Math.abs(event.y - startY) > Math.abs(event.x - startX)) && Math.abs(tempScrollY) > smallTouchSlop){
                        isIntercepted = true
                    }else {
                        isIntercepted = false
                    }
                    startY = event.y
                    startX = event.x
                }else if (event?.action == MotionEvent.ACTION_DOWN){
//            isIntercepted = true
                }
                return !isIntercepted
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    var startX = 0F
    var startY = 0F
    var tempScrollY = 0F
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            startX = event.x
            startY = event.y
//            isIntercepted = true
        }else if (event?.action == MotionEvent.ACTION_MOVE){
            //y方向的滑动才处理
            tempScrollY += event.y - startY
            if ((Math.abs(event.y - startY) > Math.abs(event.x - startX)) && Math.abs(tempScrollY) > smallTouchSlop){
                isIntercepted = true
            }else {
                isIntercepted = false
            }
            startY = event.y
            startX = event.x
        }else if (event?.action == MotionEvent.ACTION_DOWN){
//            isIntercepted = true
        }
        return isIntercepted
    }


    var isTouch = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            startPointX = event.x
            startPointY = event.y
            isTouch = true
        }else if (event?.action == MotionEvent.ACTION_MOVE){

            //y方向的滑动才处理
            if ((Math.abs(event.y - startPointY) > Math.abs(event.x - startPointX)) ){
                mScrollY += event.y - startPointY
                if (mScrollY>0){
                    //卡死在0的位置
                    mScrollY = 0F
                }
                if (event.y > startPointY){
                    //下滑
                    setCollpaseHeight(mScrollY)
                }else {
                    //上滑
                    setCollpaseHeight(mScrollY)
                }

                startPointY = event.y
                startPointX = event.x
                isTouch = true
            }else {
                isTouch = false
                isIntercepted = false
            }

            Log.i("TAG", "onTouchEvent: $mScrollY")
        }else if (event?.action == MotionEvent.ACTION_UP){
            if (isNeedCollapse){
                val animator = ValueAnimator.ofFloat(
                    collapse_view.scaleY,
                    (scrollDistance).toFloat() / (collapseHeight - scrollDistance)
                )
                animator.addUpdateListener {
                    val scaleY = it.animatedValue as Float
                    collapse_view.scaleY = scaleY
                    //滑动scrollview
                    val layoutParams = scroll_view.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.topMargin = -((1-collapse_view.scaleY)*collapseHeight).toInt()
                    scroll_view.layoutParams = layoutParams
                }
                animator.duration = 300
                animator.start()
            }
            if (isNeedExpand){
                val animator = ValueAnimator.ofFloat(
                    collapse_view.scaleY,
                    1F
                )
                animator.addUpdateListener {
                    val scaleY = it.animatedValue as Float
                    collapse_view.scaleY = scaleY
                    //滑动scrollview
                    val layoutParams = scroll_view.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.topMargin = -((1-collapse_view.scaleY)*collapseHeight).toInt()
                    scroll_view.layoutParams = layoutParams
                }
                animator.duration = 300
                animator.start()
            }
        }
        return isTouch
    }

    var isNeedCollapse = false
    var isNeedExpand = false
    private fun setCollpaseHeight(mScrollY: Float) {
        if (Math.abs(mScrollY) <= collapseHeight && mScrollY <= 0){
            if (Math.abs(mScrollY) > scrollDistance){
                collapse_view.pivotY = scrollDistance.toFloat()
                collapse_view.scaleY = (collapseHeight - Math.abs(mScrollY)) / (collapseHeight - scrollDistance)
                //滑动scrollview
                val layoutParams = scroll_view.layoutParams as RelativeLayout.LayoutParams
                layoutParams.topMargin = -((1-collapse_view.scaleY)*collapseHeight).toInt()
                scroll_view.layoutParams = layoutParams
                isNeedCollapse = true
                isNeedExpand = false
            }else {
                val layoutParams = collapse_view.layoutParams as RelativeLayout.LayoutParams
                layoutParams.topMargin = (mScrollY).toInt()
                collapse_view.layoutParams = layoutParams
                isNeedCollapse = false
                isNeedExpand = true
            }
        }else if (Math.abs(mScrollY) > collapseHeight && mScrollY <= 0){
            isNeedCollapse = false
            isNeedExpand = false
        } else if (mScrollY > 0){
            if (collapse_view.scaleY != 1F){
                collapse_view.scaleY = 1F
                this.mScrollY = 0F
                val layoutParams = scroll_view.layoutParams as RelativeLayout.LayoutParams
                layoutParams.topMargin = 0
                scroll_view.layoutParams = layoutParams
            }
        }
    }

}