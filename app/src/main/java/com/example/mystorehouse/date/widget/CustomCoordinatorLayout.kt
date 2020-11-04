package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/24
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.widget
 */

class CustomCoordinatorLayout(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs) {

    var startX = 0F

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            startX = event.x
        }else if (event?.action == MotionEvent.ACTION_MOVE){
            if (event.x - startX > 0 && Math.abs(event.x - startX.toInt()) > 30) {
                //向下
//                getParent().requestDisallowInterceptTouchEvent(false)
                false
            }
            if (event.x - startX < 0 && Math.abs(event.x - startX.toInt()) > 30) {
                //向下
//                getParent().requestDisallowInterceptTouchEvent(false)
                false
            }
        }else if (event?.action == MotionEvent.ACTION_UP){

        }
        Log.i("TAG", "onTouchEvent: "+(event?.x?:0 - startX))
        return super.onTouchEvent(event)
    }

}