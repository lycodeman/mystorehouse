package com.example.mystorehouse.screenadapter

import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ScreenUtils
import com.example.common.mvp.base.BaseEmptyFragment
import com.example.mystorehouse.R
import com.example.mystorehouse.utils.ScreenUitls
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_screen_info.*
import mFragmentComponent
import me.jessyan.autosize.internal.CancelAdapt

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/09
 *     Desc   :
 *     PackageName: com.example.mystorehouse.screenadapter
 */
class ScreenInfoFragment : BaseEmptyFragment(),CancelAdapt {
    override fun initBefore() {
        mFragmentComponent.injectFragment(this)
    }

    override fun initData() {

    }

    override fun initContentId(): Int {
        return R.layout.fragment_screen_info
    }

    override fun onResume() {
        super.onResume()
//        activity.supportActionBar?.show()
    }

    override fun initView(): RxFragment {
        tv_info.post {
                var tv = TypedValue();

                if (requireActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    var actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data,
                        getResources().getDisplayMetrics()
                    )
                    tv_info.text = """
                    隐藏toolbar 高度为：${actionBarHeight}px
                    隐藏toolbar 真实高度为：${(requireActivity() as AppCompatActivity).supportActionBar?.height}px
                    状态栏StatusBar 高度为：${me.jessyan.autosize.utils.ScreenUtils.getStatusBarHeight()}px
                    布局 高度为：${rootView?.height}px
                    屏幕宽度：${ScreenUtils.getAppScreenWidth()}px
                    屏幕高度：${ScreenUtils.getAppScreenHeight()}px
                    屏幕密度density：${ScreenUtils.getScreenDensity()}
                    屏幕densityDpi：${ScreenUtils.getScreenDensityDpi()} dp
                    虚拟按键高度：${ScreenUitls.getCurrentNavigationBarHeight(requireActivity())} px
                    设置高度：320dp 设置宽度： 180dp
                """.trimIndent()
                }
        }
        return this
    }
}