package com.example.mystorehouse.date.newwidget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.example.mystorehouse.R
import kotlinx.android.synthetic.main.activity_date_picker.view.*
import kotlinx.android.synthetic.main.activity_date_picker.view.custom_month_view
import kotlinx.android.synthetic.main.layout_custome_scroll_view.view.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/11/07
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.newwidget
 */

class CustomScrollView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs),GestureDetector.OnGestureListener {

    var gestureDetector: GestureDetector
    var  monthViewHeight = 0
    var mScrollY = 0F
    var startY = 0F
    var isIntercept = true
    var isOnTouch = true

    init {
        gestureDetector = GestureDetector(context,this)
        LayoutInflater.from(context).inflate(R.layout.layout_custome_scroll_view,this)
        custom_month_view.post({
            monthViewHeight = custom_month_view.measuredHeight
        })
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        if (custom_month_view.onTouchEvent(ev)){
//            return onTouchEvent(ev);
//        }

        if (ev?.action == MotionEvent.ACTION_DOWN){
            startY = ev.y
            isOnTouch = true
        }else if (ev?.action == MotionEvent.ACTION_MOVE){
            mScrollY+= startY - ev.y
            Log.e("TAG", "onTouchEvent: $mScrollY")
            startY = ev.y
            if (mScrollY<monthViewHeight){
                isIntercept = true
                isOnTouch = true
                return custom_month_view.onTouchEvent(ev)
            }else {
                isOnTouch = false
            }
        }else if (ev?.action == MotionEvent.ACTION_UP){
            if (mScrollY<monthViewHeight){
                return custom_month_view.onTouchEvent(ev)
            }else {
                isOnTouch = false
            }
        }
        isIntercept = false
        return custom_scroll_view_content.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isIntercept
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
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
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
//        if (Math.abs(distanceY) < monthViewHeight){
//
//        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        TODO("Not yet implemented")
    }


}