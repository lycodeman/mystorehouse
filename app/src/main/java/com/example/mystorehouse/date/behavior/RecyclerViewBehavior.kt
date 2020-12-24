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


/**
 *     Author : 李勇
 *     Create Time   : 2020/12/23
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.behavior
 */
class RecyclerViewBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<RecyclerView>(context, attrs) {
    // 列表顶部和title底部重合时，列表的滑动距离。
    private var deltaY = 0f
    private var childOriginalHeight = 0
    private var mScrollY = 0
    private var mDyConsumed = 0
    private var mDyUnconsumed = 0
    private var viewShowHeight = 0F
    private var dependencyView: CalendarView2? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        if (dependency is CalendarView2 && childOriginalHeight == 0){
            childOriginalHeight = dependency.getOriginalHeight()
            dependencyView = dependency
            Log.e("TAG", "onDependentViewChanged childOriginalHeight: " + childOriginalHeight )
        }
        return dependency is CalendarView2
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: RecyclerView,//RecyclerView
        dependency: View//CalendarView2
    ): Boolean {
        if(dependency is CalendarView2){
            viewShowHeight = dependency.getViewShowHeight()
            Log.e("TAG", "onDependentViewChanged viewShowHeight: "+viewShowHeight )
            Log.e("TAG", "onDependentViewChanged viewShowHeight: "+(child.bottom-(viewShowHeight + dpToPx(78.5F)).toInt()) )

            ViewCompat.offsetTopAndBottom(child,(viewShowHeight + dpToPx(78.5F)).toInt())
//            child.translationY = viewShowHeight + dpToPx(78.5F)
//            child.layout(child.left,child.top,child.right,child.bottom-(viewShowHeight + dpToPx(78.5F)).toInt())
        }
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        Log.e("TAG", "onStartNestedScroll: "+child.scrollY)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL && dependencyView?.isDrawWeek == false
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
        mScrollY +=dy
        Log.e("TAG", "onNestedScroll mScrollY00: "+mScrollY )
        if (mScrollY>0 && mScrollY<=viewShowHeight+dpToPx(78.5F)){
            dependencyView?.refreshScrollY(mScrollY)
        }else{
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
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
        mDyConsumed+=dyConsumed
        if(mDyConsumed < viewShowHeight+dpToPx(78.5F)){

        }else{
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
        mDyUnconsumed+=dyUnconsumed
        if(mDyConsumed == 0 && dependencyView?.isDrawWeek == true){

        }
        Log.e("TAG", "onNestedScroll mDyConsumed: "+mDyConsumed )
        Log.e("TAG", "onNestedScroll mDyUnconsumed: "+mDyUnconsumed )

    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: RecyclerView,
        layoutDirection: Int
    ): Boolean {
        return super.onLayoutChild(parent, child, layoutDirection)
    }


}