package com.example.mystorehouse.screenadapter

import com.blankj.utilcode.util.ScreenUtils
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.mystorehouse.R
import com.example.mystorehouse.screenadapter.view.Utils
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.fragment_screen_info_seven.*
import mActivityComponent

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/10
 *     Desc   :
 *     PackageName: com.example.mystorehouse.screenadapter
 */
class TodayHeadLineActivity : BaseEmptyActivity() {
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_today_head_line
    }

    override fun initView(): RxAppCompatActivity {
        Utils.setCustomDensity(this,application)
        supportActionBar?.hide()
        return this
    }

    override fun initData() {
        arrayOf(tv_info_2, tv_info).forEach {
            it.post {
                it.text = """
                    设置控件宽度为160dp,高度为320dp,
                    屏幕宽度：${ScreenUtils.getAppScreenWidth()}px
                    屏幕高度：${ScreenUtils.getAppScreenHeight()}px
                    屏幕密度：${ScreenUtils.getScreenDensity()}
                    屏幕densityDpi：${ScreenUtils.getScreenDensityDpi()} dp
                    控件高度：${it.height}px
                    控件宽度：${it.width}px
                """.trimIndent()
            }
        }
    }
}