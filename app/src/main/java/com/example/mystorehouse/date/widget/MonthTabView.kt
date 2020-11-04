package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.mystorehouse.R
import com.example.mystorehouse.getColorFromResource

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   : 支持选择某一天的view
 *     PackageName: com.example.mystorehouse.date.widget
 */
class MonthTabView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_date_month_tab,this)
        if (!isInEditMode){
            gravity = Gravity.CENTER
            setBackgroundColor(getColorFromResource(android.R.color.white))
        }
    }

}