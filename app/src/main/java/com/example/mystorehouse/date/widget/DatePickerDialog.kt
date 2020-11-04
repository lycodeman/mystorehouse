package com.example.mystorehouse.date.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import com.example.mystorehouse.R
import com.example.mystorehouse.date.Lunar
import kotlinx.android.synthetic.main.dialog_date_picker.*
import kotlinx.android.synthetic.main.dialog_date_picker.tv_year
import kotlinx.android.synthetic.main.view_date_year.*
import java.util.*


/**
 *     Author : 李勇
 *     Create Time   : 2020/10/22
 *     Desc   : 日期选择弹窗
 *     PackageName: com.example.mystorehouse.date.widget
 */
class DatePickerDialog(var onSelectDate: (Date) -> Unit) : DialogFragment(){

    private var mDate: Date? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_date_picker,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_confirm.setOnClickListener {
            mDate?.run{
                onSelectDate.invoke(this)
            }
            dismiss()
        }
        tv_cancle.setOnClickListener {
            dismiss()
        }
        calendar_view.setOnDateChangeListener(object : CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(
                view: CalendarView,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {
                initDate(year,month,dayOfMonth)
            }
        })
    }


    override fun onResume() {
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        super.onResume()
        val instance = Calendar.getInstance()
        instance.time = Date()
        initDate(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH),instance.get(Calendar.DAY_OF_MONTH))
    }

    private fun initDate(year: Int,
                         month: Int,
                         dayOfMonth: Int) {
        tv_year.setText(year.toString())
        val instance = Calendar.getInstance()
        instance.set(Calendar.YEAR,year)
        instance.set(Calendar.MONTH,month)
        instance.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        mDate = instance.time
        var dayOfWeek = instance.get(Calendar.DAY_OF_WEEK)
        var dayOfWeekToStr = ""
        if (dayOfWeek == 1){
            dayOfWeekToStr = "周日"
        }else if (dayOfWeek == 2){
            dayOfWeekToStr = "周一"
        }else if (dayOfWeek == 3){
            dayOfWeekToStr = "周二"
        }else if (dayOfWeek == 4){
            dayOfWeekToStr = "周三"
        }else if (dayOfWeek == 5){
            dayOfWeekToStr = "周四"
        }else if (dayOfWeek == 6){
            dayOfWeekToStr = "周五"
        }else if (dayOfWeek == 7){
            dayOfWeekToStr = "周六"
        }
        tv_solar_date.setText((month+1).toString()+"月"+dayOfMonth+"日"+dayOfWeekToStr)

        val lunar = Lunar(instance)
        tv_lunar_date.setText("农历："+lunar.toString())
    }

}