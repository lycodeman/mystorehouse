package com.example.mystorehouse.date.nest

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper




/**
 *     Author : 李勇
 *     Create Time   : 2020/12/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.nest
 */

class NestParent(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), NestedScrollingParent{

    private val TAG = NestParent::class.java.simpleName

    var mParentHelper: NestedScrollingParentHelper? = null

    init {

        mParentHelper = NestedScrollingParentHelper(this)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mParentHelper!!.onNestedScrollAccepted(child, target, nestedScrollAxes)
    }


    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        // 滑动的child  , 目标child , 两者唯一
        // child  嵌套滑动的子控件(当前控件的子控件) ， target ， 手指触摸的控件
        Log.i(
            TAG,
            "onStartNestedScroll:" + child.javaClass.simpleName
                .toString() + ":" + target.javaClass.simpleName
        )
        return true
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return false
    }



    override fun onStopNestedScroll(target: View) {
        Log.i(TAG, "onStopNestedScroll" + target.javaClass.simpleName)
        mParentHelper!!.onStopNestedScroll(target)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        Log.i(TAG, "onNestedScroll" + target.javaClass.simpleName)
        Log.i(TAG, "dxUnconsumed:" + dxUnconsumed + "dyUnconsumed:" + dyUnconsumed)
        getChildAt(0).translationY = getChildAt(0).translationY + dyUnconsumed
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedPreScroll(
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray
    ) {
        Log.i(TAG, "onNestedPreScroll" + target.javaClass.simpleName)
        // 开始滑动之前
        Log.i(TAG, consumed[0].toString() + ":" + consumed[1])

//        consumed[1] = 10;// 消费10px
    }


    override fun getNestedScrollAxes(): Int {
        // 垂直滚动
        return mParentHelper!!.nestedScrollAxes
    }
    
}