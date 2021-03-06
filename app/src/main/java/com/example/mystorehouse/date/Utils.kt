package com.example.mystorehouse.date

import android.util.Log
import com.example.mystorehouse.date.entity.MonthEntity
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/11/04
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date
 */
class Utils{

    fun getMonthDays(date: Date): MutableList<MonthEntity> {
        var temp = mutableListOf<MonthEntity>()
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        var startWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            //说明是周日
            startWeek--
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        var endWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        var isEndOfYear = false
        if (endWeek == 1){
            //到年底，如果那周包含下一年，就会为1
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-7)
            endWeek = calendar.get(Calendar.WEEK_OF_YEAR) + 1
            isEndOfYear = true
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            //说明是周日
            endWeek--
        }

        for (i in 0..(endWeek - startWeek)) {
            calendar.set(Calendar.WEEK_OF_YEAR,i+startWeek)
            for (j in 2..7) {
                calendar.set(Calendar.DAY_OF_WEEK,j)
                temp.add(MonthEntity((i+1)*j,
                    calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    Lunar(calendar).chinaDay,
                    calendar.time,
                    onTheMonth = isCurrentMonth(calendar.time,date),
                    isSelect = isCurrentDay(calendar.time) || isMonthFirstDay(calendar.time,date)
                ))
            }
            if (i == (endWeek - startWeek) && isEndOfYear){
                calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 1)
            }else{
                calendar.set(Calendar.WEEK_OF_YEAR,i+startWeek + 1)
                calendar.set(Calendar.DAY_OF_WEEK,1)
            }
            temp.add(MonthEntity((i+1)*7,
                calendar.get(Calendar.DAY_OF_MONTH).toString(),
                Lunar(calendar).chinaDay,
                calendar.time,
                onTheMonth = isCurrentMonth(calendar.time,date),
                isSelect = (isCurrentDay(calendar.time))
            ))
        }
        return temp
    }

    fun isFirstDayOfMonth(curDate: Date?): Boolean{
        val calendar = Calendar.getInstance()
        calendar.time = curDate
        calendar.set(Calendar.DAY_OF_MONTH,1)
        var year = calendar.get(Calendar.YEAR)
        var day = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.time = curDate
        var curYear = calendar.get(Calendar.YEAR)
        var curDay = calendar.get(Calendar.DAY_OF_YEAR)
        return curYear == year && curDay == day
    }

    fun isSameWeekDay(curDate: Date?,num: Int): Boolean{
        val calendar = Calendar.getInstance()
        calendar.time = curDate
        var weekNum = calendar.get(Calendar.DAY_OF_WEEK)
        return weekNum == num
    }

    //返回周一到周日 0-6
    fun getWeekNum(curDate: Date?): Int{
        val calendar = Calendar.getInstance()
        calendar.time = curDate
        var weekNum = calendar.get(Calendar.DAY_OF_WEEK)-2
        if (weekNum == -1){
            weekNum = 6
        }
        return weekNum
    }

    fun getLastMonthDays(date: Date): MutableList<MonthEntity> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1)
        return getMonthDays(calendar.time)
    }


    fun getLastMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1)
        return calendar.time
    }

    fun getNextMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)+1)
        return calendar.time
    }

    fun getNextWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.WEEK_OF_YEAR,calendar.get(Calendar.WEEK_OF_YEAR)+1)
        return calendar.time
    }

    fun getLastWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.WEEK_OF_YEAR,calendar.get(Calendar.WEEK_OF_YEAR)-1)
        return calendar.time
    }

    fun getFirstWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        return calendar.time
    }

    fun getNextMonthDays(date: Date): MutableList<MonthEntity> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)+1)
        return getMonthDays(calendar.time)
    }

    fun isCurrentMonth(date: Date?,curDate: Date?): Boolean{
        var calendar = Calendar.getInstance()
        calendar.time = date
        var selectYear = calendar.get(Calendar.YEAR)
        var selectMonth = calendar.get(Calendar.MONTH)
        calendar.time = curDate
        var curYear = calendar.get(Calendar.YEAR)
        var curMonth = calendar.get(Calendar.MONTH)
        return selectMonth == curMonth && curYear == selectYear
    }

    fun isCurrentDay(date: Date?): Boolean{
        var calendar = Calendar.getInstance()
        calendar.time = date
        var day =  calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        calendar.time = Date()
        var curDay = calendar.get(Calendar.DAY_OF_MONTH)
        var curMonth = calendar.get(Calendar.MONTH)
        var curYear = calendar.get(Calendar.YEAR)
        return month == curMonth && curDay == day && curYear == year
    }

    fun isInCurrentMonth(date: Date?): Boolean{
        var calendar = Calendar.getInstance()
        calendar.time = date
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        calendar.time = Date()
        var curMonth = calendar.get(Calendar.MONTH)
        var curYear = calendar.get(Calendar.YEAR)
        return month == curMonth && curYear == year
    }

    fun isSelectDay(date: Date?,selectDate: Date?): Boolean{
        if (date == null || selectDate == null){
            return false
        }
        var calendar = Calendar.getInstance()
        calendar.time = date
        var day =  calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        calendar.time = selectDate
        var curDay = calendar.get(Calendar.DAY_OF_MONTH)
        var curMonth = calendar.get(Calendar.MONTH)
        var curYear = calendar.get(Calendar.YEAR)
        return month == curMonth && curDay == day && curYear == year
    }

    fun isMonthFirstDay(date: Date,selectDate: Date): Boolean{
        var calendar = Calendar.getInstance()
        calendar.time = date
        var day =  calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        calendar.time = selectDate
        calendar.set(Calendar.DAY_OF_MONTH,1)
        var curDay = calendar.get(Calendar.DAY_OF_MONTH)
        var curMonth = calendar.get(Calendar.MONTH)
        var curYear = calendar.get(Calendar.YEAR)
        return month == curMonth && curDay == day && curYear == year
    }

    fun getFirstDayOfmonth(date: Date): Date{
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH,1)
        return calendar.time
    }

    fun getWeekDays(date: Date?): MutableList<MonthEntity>{
        var mnthEntityList = mutableListOf<MonthEntity>()
        var calendar = Calendar.getInstance()
        calendar.time = date
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1){
            calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) - 1)
        }
        var day = calendar.get(Calendar.DAY_OF_YEAR)
        var year = calendar.get(Calendar.YEAR)
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        var isLastYear = false
        var lastWeekNum = 0
        for (i in 1..7) {
            if (!isLastYear && i - dayOfWeek + day > calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
                calendar.set(Calendar.YEAR,year+1)
                calendar.set(Calendar.MONTH,0)
                calendar.set(Calendar.DAY_OF_MONTH,1)
                lastWeekNum = i
                isLastYear = true
            }
            if (isLastYear && i > lastWeekNum){
                calendar.set(Calendar.DAY_OF_YEAR, i - lastWeekNum)
            }else if (!isLastYear){
                calendar.set(Calendar.DAY_OF_YEAR, i - dayOfWeek + day)
            }
            mnthEntityList.add(MonthEntity(i,calendar.get(Calendar.DAY_OF_MONTH).toString()
                ,Lunar(calendar).chinaDay,calendar.time,onTheMonth = isCurrentMonth(calendar.time,date),
                isSelect = isCurrentDay(calendar.time)))
        }
        return mnthEntityList
    }

    fun getLastWeekDays(date: Date?): MutableList<MonthEntity>{
        var mnthEntityList = mutableListOf<MonthEntity>()
        var calendar = Calendar.getInstance()
        calendar.time = date
        var day = calendar.get(Calendar.DAY_OF_YEAR)
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        var year = calendar.get(Calendar.YEAR)
        var isLastYear = false
        var lastWeekNum = 0
        for (i in 1..7) {
            if (!isLastYear && i - dayOfWeek + day > calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
                calendar.set(Calendar.YEAR,year+1)
                calendar.set(Calendar.MONTH,0)
                calendar.set(Calendar.DAY_OF_MONTH,1)
                lastWeekNum = i
                isLastYear = true
            }
            if (isLastYear && i > lastWeekNum){
                calendar.set(Calendar.DAY_OF_YEAR, i - lastWeekNum)
            }else if (!isLastYear){
                calendar.set(Calendar.DAY_OF_YEAR, i - dayOfWeek + day)
            }
            calendar.set(Calendar.DAY_OF_YEAR, i - dayOfWeek + day - 7)
            mnthEntityList.add(MonthEntity(i,calendar.get(Calendar.DAY_OF_MONTH).toString()
                ,Lunar(calendar).chinaDay,calendar.time,onTheMonth = isCurrentMonth(calendar.time,date),
                isSelect = isCurrentDay(calendar.time)))
        }
        return mnthEntityList
    }

    fun getNextWeekDays(date: Date?): MutableList<MonthEntity>{
        var mnthEntityList = mutableListOf<MonthEntity>()
        var calendar = Calendar.getInstance()
        calendar.time = date
        var day = calendar.get(Calendar.DAY_OF_YEAR)
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        var year = calendar.get(Calendar.YEAR)
        var isLastYear = false
        var lastWeekNum = 0
        for (i in 1..7) {
            if (!isLastYear && i - dayOfWeek + day > calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
                calendar.set(Calendar.YEAR,year+1)
                calendar.set(Calendar.MONTH,0)
                calendar.set(Calendar.DAY_OF_MONTH,1)
                lastWeekNum = i
                isLastYear = true
            }
            if (isLastYear && i > lastWeekNum){
                calendar.set(Calendar.DAY_OF_YEAR, i - lastWeekNum)
            }else if (!isLastYear){
                calendar.set(Calendar.DAY_OF_YEAR, i - dayOfWeek + day)
            }
            calendar.set(Calendar.DAY_OF_YEAR, i - dayOfWeek + day + 7)
            mnthEntityList.add(MonthEntity(i,calendar.get(Calendar.DAY_OF_MONTH).toString()
                ,Lunar(calendar).chinaDay,calendar.time,onTheMonth = isCurrentMonth(calendar.time,date),
                isSelect = isCurrentDay(calendar.time)))
        }
        return mnthEntityList
    }

    fun getCurSelectLine(): Int {
        var calendar = Calendar.getInstance()
        calendar.time = Date()
        var curWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        var days = arrayOf(1,2,3,4,5,6,7)
        //年底需要加1
        if (curWeek == 1 && !days.contains(calendar.get(Calendar.DAY_OF_MONTH))){
            curWeek++
        }
        calendar.set(Calendar.DAY_OF_MONTH,1)
        var firstDayWeek =  calendar.get(Calendar.WEEK_OF_YEAR)
        if (getWeekNum(calendar.time) == 6){
            firstDayWeek--
        }
        return  curWeek - firstDayWeek+1
    }

    fun isSameMonth(selectDate: Date, date: Date?): Boolean {
        var calendar = Calendar.getInstance()
        calendar.time = selectDate
        var selectYear = calendar.get(Calendar.YEAR)
        var selectMonth = calendar.get(Calendar.MONTH)
        calendar.time = date
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        return  selectYear == year && month == selectMonth
    }

    fun getSelectLine(selectDate: Date): Int {
        var calender = Calendar.getInstance()
        var firstDayOfMonth = Utils().getFirstDayOfmonth(selectDate)

        calender.time = firstDayOfMonth
        var monthStartWeek = calender.get(Calendar.WEEK_OF_YEAR)
        //周日
        if (getWeekNum(firstDayOfMonth) == 6){
            monthStartWeek--
        }
        calender.time = selectDate
        var monthSelectWeek = calender.get(Calendar.WEEK_OF_YEAR)
        if (monthSelectWeek == 1){
            //到年底，如果那周包含下一年，就会为1
            calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH)-7)
            monthSelectWeek = calender.get(Calendar.WEEK_OF_YEAR) + 1
        }
        if (getWeekNum(selectDate) == 6){
            monthSelectWeek--
        }
        return monthSelectWeek - monthStartWeek+1
    }

}