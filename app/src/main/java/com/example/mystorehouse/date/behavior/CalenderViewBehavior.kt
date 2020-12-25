package com.example.mystorehouse.date.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorehouse.date.newwidget.CalendarView2
import com.example.mystorehouse.dpToPx
import com.google.android.material.appbar.AppBarLayout


/**
 *     Author : 李勇
 *     Create Time   : 2020/12/23
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.behavior
 */
class CalenderViewBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<CalendarView2>(context, attrs) {
    // 列表顶部和title底部重合时，列表的滑动距离。
    private var deltaY = 0f
    private var mLastY = 0

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: CalendarView2,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: CalendarView2,//CalendarView2
        dependency: View//recyclerview
    ): Boolean {
        if (deltaY === 0F) {
            deltaY = dependency.y - child.height
        }
        child.translationY = dpToPx(78.5F)
        return true
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: CalendarView2,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: CalendarView2,
        ev: MotionEvent
    ): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mLastY = ev.rawY.toInt()
            MotionEvent.ACTION_MOVE -> {
                val y = ev.rawY.toInt()
//                child.translationY = child.translationY + y - mLastY
                child.refreshScrollY(y - mLastY, y < mLastY)
                mLastY = y
            }
        }
        return true
    }
}