package com.example.mystorehouse.date.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
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

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: CalendarView2,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        Log.e("TAG", "onStartNestedScroll: "+child.scrollY)
        return false
    }

}