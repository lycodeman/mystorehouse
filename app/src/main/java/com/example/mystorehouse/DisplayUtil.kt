@file:JvmName("DisplayUtil")
@file:JvmMultifileClass

package com.example.mystorehouse

import android.util.TypedValue


fun dpToPx(dpValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dpValue,
        MyApplication.getInstance().resources.displayMetrics
    )
}

fun dpToPx(dpValue: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(),
        MyApplication.getInstance().resources.displayMetrics
    ).toInt()
}

/**
 * 所有字体均使用dp
 */
fun spToPx(spValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        spValue,
        MyApplication.getInstance().resources.displayMetrics
    )
}