package com.example.mystorehouse.date.behavior

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import com.example.mystorehouse.R


/**
 *     Author : 李勇
 *     Create Time   : 2020/12/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.behavior
 */
class BottomBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.custom_month_view
    }

    // ================ 第一部分：定位 ========================
    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        // getDependencies  获取child 依赖的view
        val dependencies = parent.getDependencies(child)
        //        parent.getDependents(child)  // 获取依赖child 的控件
        val header = findFirstDependency(dependencies)
        if (header != null) {
            val lp =
                child.layoutParams as CoordinatorLayout.LayoutParams
            val available = Rect()
            available.set(
                parent.paddingLeft + lp.leftMargin,
                header.bottom + lp.topMargin,
                parent.width - parent.paddingRight - lp.rightMargin,
                parent.height - parent.paddingBottom - lp.bottomMargin
            )
            val out = Rect()
            GravityCompat.apply(
                resolveGravity(getFinalGravity(lp.gravity)), child.measuredWidth,
                child.measuredHeight, available, out, layoutDirection
            )
            child.layout(out.left, out.top, out.right, out.bottom)
        } else {
            super.onLayoutChild(parent, child, layoutDirection)
        }
        return true
    }

    fun getFinalGravity(gravity: Int): Int {
        // 获取当前控件的`layout_gravity`属性
        var gravity = gravity
        if (gravity and Gravity.VERTICAL_GRAVITY_MASK == 0) {
            gravity = gravity or Gravity.TOP
        }
        if (gravity and Gravity.HORIZONTAL_GRAVITY_MASK == 0) {
            gravity = gravity or Gravity.LEFT
        }
        return gravity
    }

    private fun resolveGravity(gravity: Int): Int {
        return if (gravity == Gravity.NO_GRAVITY) GravityCompat.START or Gravity.TOP else gravity
    }

    private fun findFirstDependency(views: List<View>): View? {
        // 查找最根本的依赖
        for (view in views) {
            if (view.id == R.id.custom_month_view) {
                return view
            }
        }
        return null
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.translationY = dependency.translationY
        return true
    }

}