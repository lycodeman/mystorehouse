package com.example.mystorehouse

import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.mvp.base.BaseEmptyActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_date_picker4.*
import kotlinx.android.synthetic.main.item_date.view.*
import mActivityComponent

class DatePickerActivity4 : BaseEmptyActivity(){
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_date_picker4
    }
    var mScrollY = 0

    override fun initView(): RxAppCompatActivity {
        return this
    }

    override fun initData() {
        my_list.layoutManager = LinearLayoutManager(this)
        val adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_date) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.itemView.tv.text = item;
            }

        }
        var list = mutableListOf<String>();
        for (i in 1..50) {
            list.add("str="+i.toString())
        }
        adapter.setList(list)
        my_list.adapter = adapter
    }



}
