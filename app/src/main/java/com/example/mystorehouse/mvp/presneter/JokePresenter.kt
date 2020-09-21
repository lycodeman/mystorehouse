package com.example.mystorehouse.mvp.presneter

import android.annotation.SuppressLint
import com.example.common.mvp.presenter.RxPresenter
import com.example.mystorehouse.mvp.module.JokeModule
import com.example.mystorehouse.mvp.view.JokeView
import javax.inject.Inject


/**
 *     Author : 李勇
 *     Create Time   : 2020/08/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp.presneter
 */

class JokePresenter @Inject constructor(): RxPresenter<JokeView,JokeModule>(){

    var page = 1

    @SuppressLint("CheckResult")
    fun getJokeData(isRefresh: Boolean,isShowLoading: Boolean){
        if (isRefresh){
            page = 1
        }
        if (isShowLoading){
            mView?.showLoading()
        }
        mModule.getJokeData(page).subscribe {
            it.result?.data?.run {
                mView?.showData(isRefresh,this)
            }
            page ++

            if (isRefresh){
                mView?.onRefreshComplete()
            }else {
                mView?.onLoadMoreComplete(it.result?.data?.size == 0)
            }
            if (isShowLoading){
                mView?.hideLoading()
            }
        }

    }
}


