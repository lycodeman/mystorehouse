package com.example.mystorehouse.screenadapter

import com.blankj.utilcode.util.ScreenUtils
import com.example.common.mvp.base.BaseEmptyFragment
import com.example.mystorehouse.R
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_screen_info_9.*
import mFragmentComponent
import me.jessyan.autosize.internal.CancelAdapt

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/09
 *     Desc   : 百分比适配
 *     PackageName: com.example.mystorehouse.screenadapter
 */
class ScreenInfo9Fragment : BaseEmptyFragment(), CancelAdapt {
    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }

    override fun initData() {

    }

    override fun initContentId(): Int {
        return R.layout.fragment_screen_info_9
    }

    override fun onResume() {
        super.onResume()
//        activity.supportActionBar?.show()
    }

    override fun initView(): RxFragment {
        arrayOf(tv_info_2, tv_info).forEach {
            it.post {
                it.text = """
                    设置控件宽度为320px,高度为640px,
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