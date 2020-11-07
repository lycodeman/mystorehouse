package com.example.mystorehouse.date.entity

import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/10/21
 *     Desc   :
 *     PackageName: com.example.mystorehouse.date.entity
 */
class MonthEntity( var position: Int,var solarDay: String,var lunarDay: String,
                   var date: Date,var onTheMonth: Boolean = true,
                   var isSelect: Boolean = false,pointX: Int = 0,pointY: Int = 0) {


}