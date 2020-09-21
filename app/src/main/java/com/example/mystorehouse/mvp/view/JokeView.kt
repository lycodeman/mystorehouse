package com.example.mystorehouse.mvp.view

import com.example.common.mvp.view.RxRefreshView
import com.example.mystorehouse.data.JokeResult

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse
 */
interface JokeView : RxRefreshView {

    fun showData(
        isRefresh: Boolean,
        mutableList: MutableList<JokeResult>
    )
}