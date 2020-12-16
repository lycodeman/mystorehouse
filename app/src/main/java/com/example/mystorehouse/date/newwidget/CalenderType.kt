package com.example.mystorehouse.date.newwidget

import androidx.annotation.IntDef
import androidx.annotation.StringDef

/**
 *     Author : 李勇
 *     Create Time   : 2020/12/16
 *     Desc   : 列出日历的四种状态，方便控制view的滑动，缩放平移
 *     PackageName: com.example.mystorehouse.date.newwidget
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@StringDef(CalenderType.TYPE_TRANSLATION,CalenderType.TYPE_EXPAND,CalenderType.TYPE_COLLAPSE,CalenderType.TYPE_SCROLL_CONTENT)
annotation class CalenderType {
    companion object{
         const val TYPE_TRANSLATION = "translation"
         const val TYPE_COLLAPSE = "collapse"
         const val TYPE_EXPAND = "expand"
         const val TYPE_SCROLL_CONTENT = "scroll content"
    }
}

