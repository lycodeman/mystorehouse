package com.example.mystorehouse.mvp.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.mystorehouse.data.JokeResult
import com.example.mystorehouse.R
import kotlinx.android.synthetic.main.item_joke.view.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/27
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp.adapter
 */

class JokeAdapter : BaseQuickAdapter<JokeResult,BaseViewHolder>(R.layout.item_joke) {

    override fun convert(holder: BaseViewHolder, item: JokeResult) {
        holder.itemView.tv_content.text = item.content
        holder.itemView.tv_publish_time.text = item.updatetime
    }
}