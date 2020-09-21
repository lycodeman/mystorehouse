package com.example.mystorehouse.mvp.view

import com.example.common.mvp.view.RxRefreshView
import com.example.mystorehouse.data.WeatherResult

interface WeatherView : RxRefreshView {
    fun showData(weatherResult: WeatherResult)

}
