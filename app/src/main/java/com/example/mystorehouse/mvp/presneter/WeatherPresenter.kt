package com.example.mystorehouse.mvp.presneter

import com.example.common.mvp.presenter.RxPresenter
import com.example.mystorehouse.mvp.module.WeatherModule
import com.example.mystorehouse.mvp.view.WeatherView
import javax.inject.Inject

class WeatherPresenter @Inject constructor(): RxPresenter<WeatherView, WeatherModule>() {
    fun getWeatherData(cityName: String) {
        mView?.showLoading()
        mModule.getWeatherData(cityName)?.subscribe {
            it.result?.run {
                mView?.showData(this)
            }
            mView?.hideLoading()
        }
    }

}
