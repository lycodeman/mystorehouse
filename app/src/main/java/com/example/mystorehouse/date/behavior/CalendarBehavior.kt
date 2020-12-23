package com.example.mystorehouse.date.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorehouse.date.newwidget.CalendarView2


/**
 *     Author : 李勇
 *     Create Time   : 2020/12/23
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.behavior
 */
class CalendarBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<RecyclerView>(context, attrs) {
    // 列表顶部和title底部重合时，列表的滑动距离。
    private var deltaY = 0f
    private var childOriginalHeight = 0


    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        if (dependency is CalendarView2 && childOriginalHeight == 0){
            childOriginalHeight = dependency.getOriginalHeight()
            Log.e("TAG", "onDependentViewChanged childOriginalHeight: " + childOriginalHeight )
        }
        return dependency is CalendarView2
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: RecyclerView,//RecyclerView
        dependency: View//CalendarView2
    ): Boolean {
        if (deltaY === 0F) {
            deltaY = dependency.y - child.height
        }
        var dy = dependency.y - child.height
        dy = if (dy < 0) 0F else dy
        val y: Float = -(dy / deltaY) * child.height
        child.translationY = childOriginalHeight.toFloat()
            child.layout(child.left,dependency.height,child.right,child.bottom)

        Log.e("TAG", "onDependentViewChanged child: "+(dependency is CalendarView2) )
        Log.e("TAG", "onDependentViewChanged dy: "+dy )
        Log.e("TAG", "onDependentViewChanged dependency.y: "+dependency.y )
        return true
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        Log.e("TAG", "onDependentViewChanged dy : "+dy)
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        Log.e("TAG", "onNestedScroll dyConsumed: "+dyConsumed )
        Log.e("TAG", "onNestedScroll dyUnconsumed: "+dyUnconsumed )
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: RecyclerView,
        layoutDirection: Int
    ): Boolean {
        if (child is RecyclerView){
            child.layout(child.left,child.top+childOriginalHeight,child.right,child.bottom);
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }


}