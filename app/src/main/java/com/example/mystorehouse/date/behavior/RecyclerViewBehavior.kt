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
import com.zhy.autolayout.utils.ScreenUtils


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
    private var lastScrollY = 0
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
//            Log.e("TAG", "onDependentViewChanged childOriginalHeight: " + childOriginalHeight )
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
//            Log.e("TAG", "onDependentViewChanged viewShowHeight: "+viewShowHeight )
//            Log.e("TAG", "onDependentViewChanged viewShowHeight: "+(child.bottom-(viewShowHeight + dpToPx(78.5F)).toInt()) )
//            Log.e("TAG", "onDependentViewChanged child top : "+child.top )
//            Log.e("TAG", "onDependentViewChanged child y : "+child.y )
//            Log.e("TAG", "onDependentViewChanged child bottom : "+child.bottom )
//            Log.e("TAG", "onDependentViewChanged child scrollY : "+child.scrollY )
//            ViewCompat.offsetTopAndBottom(child,(viewShowHeight + dpToPx(78.5F)).toInt())
            child.translationY = viewShowHeight + dpToPx(78.5F)
//            child.bottom = ScreenUtils.getScreenSize(dependency.context,true)[1] - dpToPx(128.5F).toInt()
////            if ()
//            child.invalidate()
//            child.requestLayout()
            val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.bottomMargin =  dpToPx(78.5F).toInt() + dependency?.lineHeight
//            child.layout(child.left,-(viewShowHeight + dpToPx(78.5F)).toInt(),
//                child.right,
//                ScreenUtils.getScreenSize(dependency.context,true)[1] - dpToPx(128.5F).toInt())
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
        Log.e("TAG", "onStartNestedScroll: "+( axes == ViewCompat.SCROLL_AXIS_VERTICAL && dependencyView?.isDrawWeek == true))
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL && dependencyView?.isDrawWeek != true
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
        Log.e("TAG", "onNestedScroll mScrollY : "+mScrollY )
        if (mScrollY<0){
            mScrollY=0
        }
        child.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        Log.e("TAG", "onNestedPreScroll child.measuredHeight: "+child.measuredHeight )
        Log.e("TAG", "onNestedPreScroll child.translationY: "+child.translationY )
        Log.e("TAG", "onNestedPreScroll child.top: "+child.top )
        if (mScrollY<child.measuredHeight - ScreenUtils.getScreenSize(child.context,true)[1]+
            me.jessyan.autosize.utils.ScreenUtils.getStatusBarHeight() +dpToPx(128.5F)){

            if (mScrollY>0 && mScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
                dependencyView?.refreshScrollY(mScrollY,lastScrollY<mScrollY)

//            Log.e("TAG", "onNestedScroll dy qqq: "+dy )
//            Log.e("TAG", "onNestedScroll consumed qqqqq[1: "+consumed[1] )
                if (lastScrollY != mScrollY){
                    //Log.e("TAG", "onNestedScroll dy: "+dy )
//            Log.e("TAG", "onNestedScroll consumed[1: "+consumed[1] )
                    if (lastScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F) && mScrollY>=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
                        dependencyView?.refreshScrollY((dependencyView?.getScrollHeight()?.toInt()?:0),lastScrollY<mScrollY)
                        consumed[1] = (dependencyView?.getScrollHeight()?:0) - lastScrollY
                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                    }else if (lastScrollY>=(dependencyView?.getScrollHeight()?.toFloat()?:0F) && mScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
                        dependencyView?.refreshScrollY((dependencyView?.getScrollHeight()?.toInt()?:0),lastScrollY<mScrollY)
                        consumed[1] = lastScrollY -(dependencyView?.getScrollHeight()?:0)
                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                    }else{
                        consumed[1] = dy
                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                    }
                }else{
                    consumed[1] = dy
                    super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                }
            }else{
                if (lastScrollY != mScrollY){
                    //Log.e("TAG", "onNestedScroll dy: "+dy )
//            Log.e("TAG", "onNestedScroll consumed[1: "+consumed[1] )
                    if (lastScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F) && mScrollY>=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
                        dependencyView?.refreshScrollY((dependencyView?.getScrollHeight()?.toInt()?:0),lastScrollY<mScrollY)
                        consumed[1] = (dependencyView?.getScrollHeight()?:0) - lastScrollY
                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                    }else if (lastScrollY>=(dependencyView?.getScrollHeight()?.toFloat()?:0F) && mScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
                        dependencyView?.refreshScrollY((dependencyView?.getScrollHeight()?.toInt()?:0),lastScrollY<mScrollY)
                        consumed[1] = lastScrollY -(dependencyView?.getScrollHeight()?:0)
                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                    }else{
                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                    }
                }
            }
        }else{
            mScrollY=child.measuredHeight - ScreenUtils.getScreenSize(child.context,true)[1]
            +me.jessyan.autosize.utils.ScreenUtils.getStatusBarHeight()+ dpToPx(128.5F).toInt()
            Log.e("TAG", "onNestedPreScroll mScrollY === : "+mScrollY )
            if (lastScrollY != mScrollY){
                //Log.e("TAG", "onNestedScroll dy: "+dy )
//            Log.e("TAG", "onNestedScroll consumed[1: "+consumed[1] )
//                    if (lastScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F) && mScrollY>=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
//                        dependencyView?.refreshScrollY((dependencyView?.getScrollHeight()?.toInt()?:0),lastScrollY<mScrollY)
//                        consumed[1] = (dependencyView?.getScrollHeight()?:0) - lastScrollY
//                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//                    }else if (lastScrollY>=(dependencyView?.getScrollHeight()?.toFloat()?:0F) && mScrollY<=(dependencyView?.getScrollHeight()?.toFloat()?:0F)){
//                        dependencyView?.refreshScrollY((dependencyView?.getScrollHeight()?.toInt()?:0),lastScrollY<mScrollY)
//                        consumed[1] = lastScrollY -(dependencyView?.getScrollHeight()?:0)
//                        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//                    }else{
//                super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//                    }
            }
        }

        lastScrollY = mScrollY
    }

//    override fun onNestedPreFling(
//        coordinatorLayout: CoordinatorLayout,
//        child: RecyclerView,
//        target: View,
//        velocityX: Float,
//        velocityY: Float
//    ): Boolean {
//        return false
//    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return false
    }


    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        target: View,
        type: Int
    ) {
        Log.e("TAG", "onStopNestedScroll: " )
//        if (lastScrollY<mScrollY){
//            //上滑
//            mScrollY = (dependencyView?.getScrollHeight()?:0)
//        }else {
//            mScrollY = 0
//        }
//        Log.e("TAG", "onStopNestedScroll: mScrollY==== "+mScrollY )
//        lastScrollY = mScrollY
////        super.onStopNestedScroll(coordinatorLayout, child, target, type)
//        if (dependencyView!=null){
//            dependencyView?.stopScroll()
//        }
    }



    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: RecyclerView,
        ev: MotionEvent
    ): Boolean {
        if (ev.action == MotionEvent.ACTION_UP){
            if (dependencyView !=null){
                Log.e("TAG", "onTouchEvent: " )
                if (mScrollY<(dependencyView?.getScrollHeight()?:0)){
//                    dependencyView?.stopScroll()
                }
//                return dependencyView!!.onTouchEvent(ev)
            }
        }
        return false
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
//        mDyConsumed+=dyConsumed
//        if(mDyConsumed < viewShowHeight+dpToPx(78.5F)){
//
//        }else{
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
//        }
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
        child.layout(child.left,child.top,child.right,child.bottom)
        return super.onLayoutChild(parent, child, layoutDirection)
    }


}