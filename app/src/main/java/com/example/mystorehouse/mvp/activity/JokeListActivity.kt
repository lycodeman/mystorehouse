package com.example.mystorehouse.mvp.activity


import androidx.recyclerview.widget.LinearLayoutManager
import checkNetwork
import com.example.common.mvp.base.BaseActivity
import com.example.mystorehouse.data.JokeResult
import com.example.mystorehouse.R
import com.example.mystorehouse.mvp.adapter.JokeAdapter
import com.example.mystorehouse.mvp.presneter.JokePresenter
import com.example.mystorehouse.mvp.view.JokeView
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import hideProgress
import kotlinx.android.synthetic.main.activity_joke_list.*
import mActivityComponent
import showProgress


class JokeListActivity : BaseActivity<JokePresenter>(), JokeView {

    val jokeAdapter = JokeAdapter()

    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initData() {
        checkNetwork()
        mPresenter.getJokeData(true,true)
    }

    override fun initContentId(): Int {
        return R.layout.activity_joke_list
    }

    override fun initView(): RxAppCompatActivity {
        rv_joke.layoutManager = LinearLayoutManager(this)
        rv_joke.adapter = jokeAdapter
        return this
    }

    override fun initListener() {
        super.initListener()
        smart_refresh.setOnRefreshListener {
            checkNetwork()
            mPresenter.getJokeData(true,false)
        }
        smart_refresh.setOnLoadMoreListener {
            checkNetwork()
            mPresenter.getJokeData(false,false)
        }

    }

    override fun showData(
        isRefresh: Boolean,
        mutableList: MutableList<JokeResult>
    ) {
        if (isRefresh){
            jokeAdapter.setList(mutableList)
        }else {
            jokeAdapter.addData(mutableList)
        }

    }

    override fun onRefreshComplete() {
        super.onRefreshComplete()
        smart_refresh.finishRefresh()
    }

    override fun onLoadMoreComplete(noMoreData: Boolean) {
        super.onLoadMoreComplete(noMoreData)
        smart_refresh.finishLoadMore()
        smart_refresh.setNoMoreData(noMoreData)
    }

    override fun showLoading() {
        super.showLoading()
        showProgress("加载中...")
    }

    override fun hideLoading() {
        super.hideLoading()
        hideProgress()
    }

}

