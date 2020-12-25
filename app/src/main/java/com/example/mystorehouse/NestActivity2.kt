package com.example.mystorehouse

import com.example.common.mvp.base.BaseEmptyActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import mActivityComponent

class NestActivity2 : BaseEmptyActivity() {
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_nest2
    }

    var mScrollY = 0

    override fun initView(): RxAppCompatActivity {

        return this
    }

    override fun initListener() {
        super.initListener()

    }


    override fun initData() {

    }


}
