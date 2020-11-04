package com.example.mystorehouse.date.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by aifeng.hong on 2016/5/9 09:24.
 */
abstract class BaseViewPager2Adapter<T> : RecyclerView.Adapter<BaseViewPager2Adapter.ViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener<T>? = null
    private val mLists: MutableList<T> = mutableListOf()

    fun setData(dataLists: MutableList<T>){
        this.mLists.clear()
        this.mLists.addAll(dataLists)
        notifyDataSetChanged()
    }

    fun addData(dataLists: MutableList<T>){
        this.mLists.addAll(dataLists)
        notifyDataSetChanged()
    }

    fun getDatas(): MutableList<T> {
        return this.mLists
    }

    interface OnItemClickListener<T> {
        fun onItemClick(v: ViewHolder?, position: Int, data: T)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mItem = LayoutInflater.from(parent.context).inflate(getItemLayoutId(viewType), parent, false)
        return ViewHolder(mItem)
    }

    abstract fun getItemLayoutId(viewType: Int): Int

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBind(holder,position,mLists[position])
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(holder,position,mLists[position])
        }
    }

    abstract fun onBind(holder: ViewHolder, position: Int, t: T)

    override fun getItemCount(): Int {
        return if (mLists != null && !mLists.isEmpty()) {
            mLists.size
        } else 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    companion object {
        private const val TAG = "GridAdapter"
    }

}