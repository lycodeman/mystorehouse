package com.example.mystorehouse.mvp.fragment

import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.mvp.base.BaseEmptyFragment
import com.example.common.mvp.base.BaseFragment
import com.example.common.mvp.utils.LoggerUtils
import com.example.common.mvp.utils.RxUtils
import com.example.mystorehouse.Constants.Companion.CITY_ONE
import com.example.mystorehouse.R
import com.example.mystorehouse.api.CityApi
import com.example.mystorehouse.data.WeatherResult
import com.example.mystorehouse.mvp.activity.WeatherActivity
import com.example.mystorehouse.mvp.presneter.WeatherPresenter
import com.example.mystorehouse.mvp.view.WeatherView
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.item_weather.view.*
import mFragmentComponent
import javax.inject.Inject

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/27
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp
 */
class WeatherFragment(var pagePosition: Int,var cityApi: CityApi) : BaseEmptyFragment(){


    var dataList = mutableMapOf<String,String>()

    private val baseQuickAdapter =
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_weather) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.itemView.tv_name.text = item
            }
        }

    override fun initView(): RxFragment {
        rv_weather.layoutManager = LinearLayoutManager(context)
        rv_weather.adapter = baseQuickAdapter
        return this
    }

    override fun initData() {
        if (pagePosition == 0){
            cityApi.getProvinceList().compose(RxUtils.defaultTransformer(this)).subscribe {
                dataList.putAll(it)
                baseQuickAdapter.setList(it.values)
            }
            baseQuickAdapter.setOnItemClickListener { adapter, view, position ->
                (activity as WeatherActivity).switchPage(1, dataList.keys.elementAt(position))
            }
        }
    }

    override fun initContentId(): Int {
        return R.layout.fragment_weather
    }

    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }

    override fun initListener() {
        super.initListener()
        baseQuickAdapter.setOnItemLongClickListener { adapter, view, position ->
//            LoggerUtils.d(dataList[position].toString())
            WeatherDetailFragment(adapter.data[position] as String).show(activity.supportFragmentManager,"")
            true
        }
    }

    fun showDataByCode(pagePosition: Int, code: String) {
        if (pagePosition == 0){
            cityApi.getProvinceList().compose(RxUtils.defaultTransformer(this)).subscribe {
                dataList.putAll(it)
                baseQuickAdapter.setList(it.values)
            }
            baseQuickAdapter.setOnItemClickListener { adapter, view, position ->
                (activity as WeatherActivity).switchPage(1, dataList.keys.elementAt(position))
            }
        }else if (pagePosition == 1){
            cityApi.getCityList(code).compose(RxUtils.defaultTransformer(this)).subscribe {
                baseQuickAdapter.setList(it.values)
                dataList.putAll(it)
            }
            if (!CITY_ONE.contains(code.toInt())){
                baseQuickAdapter.setOnItemClickListener { adapter, view, position ->
                    (activity as WeatherActivity).switchPage(2,code+ dataList.keys.elementAt(position))
                }
            }
        }else if (pagePosition == 2){
            cityApi.getCountryList(code).compose(RxUtils.defaultTransformer(this)).subscribe {
                baseQuickAdapter.setList(it.values)
                dataList.putAll(it)
            }
            baseQuickAdapter.setOnItemClickListener { adapter, view, position ->

            }
        }
    }

}