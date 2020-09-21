package com.example.mystorehouse.mvp.activity

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.FragmentUtils
import com.chad.library.adapter.base.util.getItemView
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.common.mvp.utils.RxUtils
import com.example.mystorehouse.R
import com.example.mystorehouse.api.CityApi
import com.example.mystorehouse.mvp.fragment.WeatherFragment
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_weather.*
import mActivityComponent
import javax.inject.Inject

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/27
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp.activity
 */
class WeatherActivity : BaseEmptyActivity() {

    @Inject
    lateinit var cityApi: CityApi
    var fragmnetList = mutableListOf<WeatherFragment>()
    var currentPage = 0

    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_weather
    }

    private val fragmentStateAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return fragmnetList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmnetList.get(position)
        }
    }

    override fun initView(): RxAppCompatActivity {
        viewpager.isUserInputEnabled = false
        fragmnetList.add(WeatherFragment(0,cityApi))
        fragmnetList.add(WeatherFragment(1,cityApi))
        fragmnetList.add(WeatherFragment(2,cityApi))
        viewpager.adapter = fragmentStateAdapter
        return this
    }

    override fun initData() {

    }

    override fun initListener() {
        super.initListener()
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)


            }
        })
    }

    fun switchPage(pagePosition: Int, code: String) {
        viewpager.setCurrentItem(pagePosition)
        val fragment = fragmnetList[pagePosition]
        fragment.showDataByCode(pagePosition,code)
        currentPage = pagePosition
    }

    override fun onBackPressed() {
        if (currentPage!=0){
            viewpager.setCurrentItem(--currentPage)
        }else {
            super.onBackPressed()
        }
    }
}