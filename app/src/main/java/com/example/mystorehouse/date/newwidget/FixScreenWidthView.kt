package com.example.mystorehouse.date.newwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 *     Author : 李勇
 *     Create Time   : 2020/11/08
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.newwidget
 */
class FixScreenWidthView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    val screenWidth = ScreenUtils.getScreenWidth()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var tempHeight = measureViewGroupHeight(this)
        var tempWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            screenWidth
            ,MeasureSpec.getMode(widthMeasureSpec))
        var tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            tempHeight
            ,MeasureSpec.getMode(heightMeasureSpec))
        super.onMeasure(tempWidthMeasureSpec, tempHeightMeasureSpec)
    }

    fun measureViewGroupHeight(viewGroup: ViewGroup): Int {
        var tempHeight = 0
        for (i in 0 until viewGroup.childCount) {
            val childAt = viewGroup.getChildAt(i)
            if (childAt is ViewGroup) {
                tempHeight += measureViewGroupHeight(childAt)
            } else {
                tempHeight += measureViewHeight(childAt)
            }
        }
        return tempHeight
    }

    fun measureViewHeight(view: View): Int {
        view.measure(
            MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST)
            , MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        return view.measuredWidth / screenWidth * view.measuredHeight
    }
}