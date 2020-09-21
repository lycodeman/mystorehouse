package com.example.mystorehouse.ex

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/01
 *     Desc   :
 *     PackageName: com.genlot.hnapp.mvp.helper
 */

fun Any.showKeyBord(et: EditText) {
    et.requestFocus()
    val imm = et.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
}