package com.example.mystorehouse.date.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mystorehouse.R
import com.example.mystorehouse.getColorFromResource
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import kotlinx.android.synthetic.main.view_date_picker.view.*
import kotlinx.android.synthetic.main.view_date_week.view.*
import kotlinx.android.synthetic.main.view_scroll_to_collapse.view.*
import java.util.*


/**
 *     Author : 李勇
 *     Create Time   : 2020/10/22
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.widget
 */
class WeekView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {


    var mFragmentLsit = mutableListOf<WeekFragment>()
    var onPageSelect: (date: Date) -> Unit = {}
    var onSelectDate: (date: Date) -> Unit = {}

    private val adapter = object : FragmentStateAdapter(context as FragmentActivity) {
        override fun getItemCount(): Int {
            return mFragmentLsit.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragmentLsit[position]
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_date_week,this)
//        setBackgroundColor(getColorFromResource(R.color.white))
        createWeekFg(Date())
    }

    fun createWeekFg(date: Date) {
        var calendar = Calendar.getInstance()
        calendar.time = date
        var dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        //默认加载4条数据，方便左右侧滑
        mFragmentLsit.clear()
        for (i in 1..5) {
//            calendar.clear()
            calendar.set(Calendar.DAY_OF_YEAR, dayOfYear+( - 3 + i)*7)
            mFragmentLsit.add(createMonthFg(calendar.time, i - 1) { position ->
                mFragmentLsit.forEachIndexed { index, weekFragment ->
                    if (index != position) {
                        weekFragment.clearSelectDate()
                    }
                    weekFragment.onSelectDate = {date ->
                        onSelectDate.invoke(date)
                    }
                }
            })
        }
        vp_week.adapter = adapter
        vp_week.offscreenPageLimit = 3
        var currentPosition = 0
        vp_week.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (currentPosition == 0) {
                        //右滑
                        val instance = Calendar.getInstance()
                        mFragmentLsit.mapIndexed { index, weekFragment ->
                            instance.clear()
                            instance.time = weekFragment.date
                            instance.set(Calendar.DAY_OF_YEAR, instance.get(Calendar.DAY_OF_YEAR) - 3*7)
                            weekFragment.date = instance.time
                            weekFragment.initView()
                            weekFragment.initListener()
                        }
                        vp_week.setCurrentItem(mFragmentLsit.size - 2, false);//切换，不要动画效果
                    } else if (currentPosition == mFragmentLsit.size - 1) {
                        //左滑
                        val instance = Calendar.getInstance()
                        mFragmentLsit.mapIndexed { index, weekFragment ->
                            instance.clear()
                            instance.time = weekFragment.date
                            instance.set(Calendar.DAY_OF_YEAR, instance.get(Calendar.DAY_OF_YEAR) + 3*7)
                            weekFragment.date = instance.time
                            weekFragment.initView()
                            weekFragment.initListener()
                        }
                        vp_week.setCurrentItem(1, false);//切换，不要动画效果
                    }
                    onPageSelect.invoke(mFragmentLsit[currentPosition].date)
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }


        })
        vp_week.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp_week.setCurrentItem(2, false)

    }


    fun createMonthFg(date: Date,position: Int,clearSelectDay: (position: Int) -> Unit): WeekFragment {
        val weekFragment = WeekFragment(date, clearSelectDay)
        weekFragment.tag = position
        return weekFragment
    }

    var startX = 0F

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            startX = event.x
        }else if (event?.action == MotionEvent.ACTION_MOVE){
            if (event.x - startX > 0 && Math.abs(event.x - startX.toInt()) > 30) {
                //向下
                getParent().requestDisallowInterceptTouchEvent(true)
                true
            }
        }else if (event?.action == MotionEvent.ACTION_UP){

        }
        return super.onTouchEvent(event)
    }

}