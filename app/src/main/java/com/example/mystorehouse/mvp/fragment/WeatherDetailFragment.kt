package com.example.mystorehouse.mvp.fragment

import com.example.common.mvp.base.BaseDialogFragment
import com.example.mystorehouse.R
import com.example.mystorehouse.data.WeatherResult
import com.example.mystorehouse.mvp.presneter.WeatherPresenter
import com.example.mystorehouse.mvp.view.WeatherView
import com.trello.rxlifecycle4.components.support.RxDialogFragment
import kotlinx.android.synthetic.main.fragment_weather_detail.*
import mFragmentComponent

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/27
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp
 */
class WeatherDetailFragment(var cityname: String) : BaseDialogFragment<WeatherPresenter>(), WeatherView {

    override fun initView(): RxDialogFragment {
        return this
    }

    override fun initData() {
        et_city.setText(cityname)
        mPresenter.getWeatherData(cityname)
    }

    override fun initListener() {
        super.initListener()
        bt_search.setOnClickListener{
            mPresenter.getWeatherData(et_city.text.toString())
        }
    }

    override fun initContentId(): Int {
        return R.layout.fragment_weather_detail
    }

    override fun showData(weatherResult: WeatherResult) {
        tv_sk.text = weatherResult.sk.toString()
        tv_today.text = weatherResult.today.toString()
        tv_future.text = weatherResult.future.toString()
    }

    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }
}