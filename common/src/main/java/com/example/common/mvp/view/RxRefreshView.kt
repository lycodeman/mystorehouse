package com.example.common.mvp.view

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/01
 *     Desc   :
 *     PackageName: com.genlot.hnapp.mvp.view
 */
interface RxRefreshView  : BaseView {
    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun showToast(msg: String?) {

    }

    fun onRefresh(){}
    fun onLoadMore(){}
    fun onRefreshComplete(){}
    fun onLoadMoreComplete(noMoreData: Boolean){}
}