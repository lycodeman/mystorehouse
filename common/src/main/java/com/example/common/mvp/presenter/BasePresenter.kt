package com.example.common.mvp.presenter

import com.example.common.mvp.view.BaseView

/**
 * Created by codeest on 2016/8/2.
 * Presenter基类
 */
interface BasePresenter {
    fun <T> attachView(view: T)
    val isActive: Boolean
    fun detachView()
}