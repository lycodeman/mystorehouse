package com.example.mystorehouse.screenadapter

import com.blankj.utilcode.util.ScreenUtils
import com.example.common.mvp.base.BaseEmptyFragment
import com.example.mystorehouse.R
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_screen_info_two.*
import mFragmentComponent

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/09
 *     Desc   :
 *     PackageName: com.example.mystorehouse.screenadapter
 */
class ScreenInfoThreeFragment : BaseEmptyFragment() {
    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }

    override fun initData() {

    }

    override fun initContentId(): Int {
        return R.layout.fragment_screen_info_two
    }

    override fun initView(): RxFragment {
//        activity.supportActionBar?.hide()
        arrayOf(tv_half_top, tv_half_bottom).forEach {
            it.post {
                it.text = """
                    设置控件宽度为180dp,高度为320dp,
                    设计稿满屏尺寸：360dp,640dp
                    屏幕宽度：${ScreenUtils.getAppScreenWidth()}px
                    屏幕高度：${ScreenUtils.getAppScreenHeight()}px
                    屏幕密度：${ScreenUtils.getScreenDensity()}
                    屏幕densityDpi：${ScreenUtils.getScreenDensityDpi()} dp
                    控件高度：${it.height}px
                    控件宽度：${it.width}px
                """.trimIndent()
            }
        }

        return this
    }
}