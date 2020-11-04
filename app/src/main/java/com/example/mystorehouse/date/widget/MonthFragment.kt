package com.example.mystorehouse.date.widget

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.mvp.base.BaseEmptyFragment
import com.example.mystorehouse.R
import com.example.mystorehouse.date.Lunar
import com.example.mystorehouse.date.entity.MonthEntity
import com.example.mystorehouse.getColorFromResource
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_month.*
import kotlinx.android.synthetic.main.item_month.view.*
import mFragmentComponent
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   : 展示月份
 *     PackageName: com.example.mystorehouse.date.widget
 */
class MonthFragment(var date: Date, var clearSelectDay: (position: Int) -> Unit) :
    BaseEmptyFragment() {

    var lastPosition = -1
    var onSelectDate: (Date) -> Unit = {}
    var selectDate = {
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.time
    }

    fun isCurrentMonth(): Boolean{
        var calendar = Calendar.getInstance()
        calendar.time = date
        var selectYear = calendar.get(Calendar.YEAR)
        var selectMonth = calendar.get(Calendar.MONTH)
        calendar.time = Date()
        var curYear = calendar.get(Calendar.YEAR)
        var curMonth = calendar.get(Calendar.MONTH)
        return selectMonth == curMonth && curYear == selectYear
    }

    fun isCurrentDay(day: Int): Boolean{
        var calendar = Calendar.getInstance()
        calendar.time = date
        var selectMonth = calendar.get(Calendar.MONTH)
        calendar.time = Date()
        var curDay = calendar.get(Calendar.DAY_OF_MONTH)
        var curMonth = calendar.get(Calendar.MONTH)
        return selectMonth == curMonth && curDay == day
    }

    var tag = -1

    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }

    override fun initData() {

    }

    fun clearSelectDate() {
//        if (lastPosition != -1) {
//            mAdapter.data[lastPosition].isSelect = true
//            mAdapter.notifyItemChanged(lastPosition)
//            lastPosition = -1
//        }
    }

    override fun initContentId(): Int {
        return R.layout.fragment_month
    }

    private val mAdapter =
        object : BaseQuickAdapter<MonthEntity, BaseViewHolder>(R.layout.item_month) {

            override fun convert(holder: BaseViewHolder, item: MonthEntity) {
                if (item.onTheMonth) {
                    holder.itemView.tv_solar_date.text = item.solarDay
                    holder.itemView.tv_lunar_date.text = item.lunarDay
                    if (item.isSelect) {
                        lastPosition = item.position
                        holder.itemView.tv_solar_date.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        holder.itemView.tv_lunar_date.setTextColor(getColorFromResource(R.color.color_E35C5C))
                        holder.itemView.ll_date.setBackgroundResource(R.drawable.shape_month_date)
                    } else {
                        holder.itemView.tv_solar_date.setTextColor(getColorFromResource(R.color.color_1C1C1C))
                        holder.itemView.tv_lunar_date.setTextColor(getColorFromResource(R.color.color_9A9A9A))
                        holder.itemView.ll_date.setBackgroundColor(getColorFromResource(R.color.transparent))
                    }

                } else {
                    holder.itemView.tv_solar_date.text = item.solarDay
                    holder.itemView.tv_lunar_date.text = item.lunarDay
                    holder.itemView.tv_solar_date.setTextColor(getColorFromResource(android.R.color.darker_gray))
                    holder.itemView.tv_lunar_date.setTextColor(getColorFromResource(android.R.color.darker_gray))
                    holder.itemView.ll_date.setBackgroundColor(getColorFromResource(R.color.transparent))
                }

            }
        }

    override fun initView(): RxFragment {
        rv_month.layoutManager = GridLayoutManager(context, 7)
        rv_month.adapter = mAdapter
        initDate()

        mAdapter.addChildClickViewIds(R.id.ll_date_root)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.ll_date_root) {
                if (mAdapter.data[position].onTheMonth) {
                    if (lastPosition != -1) {
                        mAdapter.data[lastPosition].isSelect = false
                    }
                    mAdapter.data[position].isSelect = true
                    mAdapter.notifyDataSetChanged()
                    lastPosition = position
                    clearSelectDay.invoke(tag)
                    onSelectDate.invoke(mAdapter.data[lastPosition].date)
                }
            }
        }
        return this
    }


    fun initDate() {
        var temp = mutableListOf<MonthEntity>()
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        if (dayOfWeek == 0) {
            dayOfWeek = 7
        }
        var position = -1
        if (dayOfWeek <= 5) {
            //计算上一个月
            calendar.clear()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            //getActualMaximum之前要重新设置才会生效
            var lastMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in -(dayOfWeek - 2)..(0)) {
                calendar.set(Calendar.DAY_OF_MONTH, lastMonthLastDay + i)
                val lunar = Lunar(calendar)
                temp.add(
                    MonthEntity(
                        ++position,
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        lunar.getChinaDay(),
                        calendar.time,
                        onTheMonth = false
                    )
                )
            }
            //添加当前月份的时间
            calendar.clear()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            var maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 1..maxDay) {
                calendar.set(Calendar.DAY_OF_MONTH, i)
                val lunar = Lunar(calendar)
                temp.add(
                    MonthEntity(
                        ++position,
                        i.toString(),
                        lunar.getChinaDay(),
                        calendar.time,
                        onTheMonth = true,
                        isSelect = (i == 1 && !isCurrentMonth()) || (isCurrentMonth() && isCurrentDay(i))
                    )
                )
            }
            //添加之后月份的时间
            val size = temp.size
            calendar.clear()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month + 1)
            for (i in 1..(35 - size)) {
                calendar.set(Calendar.DAY_OF_MONTH, i)
                val lunar = Lunar(calendar)
                temp.add(
                    MonthEntity(
                        ++position,
                        i.toString(),
                        lunar.getChinaDay(),
                        calendar.time,
                        onTheMonth = false
                    )
                )
            }
        } else {
            //计算上一个月
            calendar.clear()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            //getActualMaximum之前要重新设置才会生效
            var lastMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in -(dayOfWeek - 2)..(0)) {
                calendar.set(Calendar.DAY_OF_MONTH, lastMonthLastDay + i)
                val lunar = Lunar(calendar)
                temp.add(
                    MonthEntity(
                        ++position,
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        lunar.getChinaDay(),
                        date,
                        onTheMonth = false
                    )
                )
            }
            calendar.clear()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            var maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 1..maxDay) {
                calendar.set(Calendar.DAY_OF_MONTH, i)
                val lunar = Lunar(calendar)
                temp.add(
                    MonthEntity(
                        ++position,
                        i.toString(),
                        lunar.getChinaDay(),
                        date,
                        onTheMonth = true,
                        isSelect = (i == 1 && !isCurrentMonth()) || (isCurrentMonth() && isCurrentDay(i))
                    )
                )
            }
            val size = temp.size
            calendar.clear()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month + 1)
            for (i in 1..(42 - size)) {
                calendar.set(Calendar.DAY_OF_MONTH, i)
                val lunar = Lunar(calendar)
                temp.add(
                    MonthEntity(
                        ++position,
                        i.toString(),
                        lunar.getChinaDay(),
                        date,
                        onTheMonth = false
                    )
                )
            }
        }
        mAdapter.setList(temp)
    }

    fun getRv(): RecyclerView? {
        return rv_month
    }
}