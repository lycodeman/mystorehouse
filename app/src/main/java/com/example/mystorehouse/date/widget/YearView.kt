package com.example.mystorehouse.date.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mystorehouse.R
import com.example.mystorehouse.getColorFromResource
import kotlinx.android.synthetic.main.view_date_year.view.*
import java.util.*


/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   : 支持选择某一天的view
 *     PackageName: com.example.mystorehouse.date.widget
 */
class YearView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    var selectDate: Date = Date()
    var onRefresh: (date: Date) -> Unit = {}

    private val DURATION = 1000L
    init {
        LayoutInflater.from(context).inflate(R.layout.view_date_year,this)
        if (!isInEditMode){
            gravity = Gravity.CENTER
            setBackgroundColor(getColorFromResource(android.R.color.holo_red_light))
        }
        tv_year.setFactory {
            val textView = TextView(context)
            textView.textSize = 16f
            textView.setTextColor(Color.WHITE)
            textView
        }
        tv_month.setFactory {
            val textView = TextView(context)
            textView.textSize = 16f
            textView.setTextColor(Color.WHITE)
            textView
        }
        setListener()
    }

    fun setListener(){
        ll_root.setOnClickListener {
            DatePickerDialog { date ->
                selectDate = date
                val instance = Calendar.getInstance()
                instance.time = date
                setMonth(instance.get(Calendar.MONTH)+1)
                setYear(instance.get(Calendar.YEAR))
                onRefresh.invoke(date)
            }.show((context as AppCompatActivity).supportFragmentManager,"")
        }
    }

    fun getYear(): String{
        return  (tv_year.currentView as TextView).text.toString()
    }

    fun getMonth(): String{
        return (tv_month.currentView as TextView).text.toString()
    }

    fun setMonth(month: Int) {
        tv_month.setText(month.toString()).toString()
    }

    fun setYear(year: Int) {
        tv_year.setText(year.toString()).toString()
    }

}