package com.example.mystorehouse

import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/**
 * 动态创建shape标签
 */
object ShapeUtils {

    fun createSimpleShape(radiusDp: Int, solidColor: Int): GradientDrawable {
        var shapeDrawable = GradientDrawable()
        shapeDrawable.cornerRadius =  dpToPx(radiusDp).toFloat()
        shapeDrawable.setColor(getColorFromResource(solidColor))
        return shapeDrawable
    }

    fun createSimpleShape(radiusDp: Int): GradientDrawable {
        var shapeDrawable = GradientDrawable()
        shapeDrawable.cornerRadius =  dpToPx(radiusDp).toFloat()
        return shapeDrawable
    }

    /**
     * 创建带边框 填充色 drawable
     */
    fun createSimpleShape(radiusDp: Int, strokeColor: Int,width: Int, solidColor: Int): GradientDrawable {
        var shapeDrawable = GradientDrawable()
        shapeDrawable.cornerRadius =  dpToPx(radiusDp).toFloat()
        shapeDrawable.setStroke(dpToPx(width),strokeColor)
        shapeDrawable.setColor(solidColor)
        return shapeDrawable
    }

    /**
     * 创建带边框  drawable
     */
    fun createSimpleShape(radiusDp: Int, strokeColor: Int,width: Int): GradientDrawable {
        var shapeDrawable = GradientDrawable()
        shapeDrawable.cornerRadius =  dpToPx(radiusDp).toFloat()
        shapeDrawable.setStroke(dpToPx(width), getColorFromResource(strokeColor))
        shapeDrawable.setShape(GradientDrawable.RECTANGLE)
        //透明背景
        shapeDrawable.setColor(Color.TRANSPARENT)
        return shapeDrawable
    }
}