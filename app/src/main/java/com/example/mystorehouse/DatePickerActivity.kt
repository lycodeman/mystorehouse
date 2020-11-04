package com.example.mystorehouse

import com.example.common.mvp.base.BaseEmptyActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import mActivityComponent

class DatePickerActivity : BaseEmptyActivity(){
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_date_picker
    }

    override fun initView(): RxAppCompatActivity {

        return this
    }

    override fun initData() {

    }

}
