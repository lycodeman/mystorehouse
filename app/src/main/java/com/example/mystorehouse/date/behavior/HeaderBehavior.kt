package com.example.mystorehouse.date.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout


/**
 *     Author : 李勇
 *     Create Time   : 2020/12/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.behavior
 */
class HeaderBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    private var mLastY = 0

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mLastY = ev.rawY.toInt()
            MotionEvent.ACTION_MOVE -> {
                val y = ev.rawY.toInt()
                child.translationY = child.translationY + y - mLastY
                mLastY = y
            }
        }
        return true
    }

}