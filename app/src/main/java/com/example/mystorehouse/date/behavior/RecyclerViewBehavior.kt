package com.example.mystorehouse.date.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView


/**
 *     Author : 李勇
 *     Create Time   : 2020/12/23
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.behavior
 */
class RecyclerViewBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<RecyclerView>(context, attrs) {

    fun layoutDependsOn(
        parent: CoordinatorLayout?,
        child: RecyclerView?,
        dependency: View?
    ): Boolean {
        return dependency is TextView
    }

    fun onDependentViewChanged(
        parent: CoordinatorLayout?,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        //计算列表y坐标，最小为0
        var y: Float = dependency.getHeight() + dependency.getTranslationY()
        if (y < 0) {
            y = 0f
        }
        child.y = y
        return true
    }
}