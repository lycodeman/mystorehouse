package com.example.common.mvp.base

import com.example.common.R
import com.example.common.mvp.presenter.BaseListPresenter
import com.example.common.mvp.view.BaseListView


/**
 *     Author : 李勇
 *     Create Time   : 2020/08/06
 *     Desc   :
 *     PackageName: com.genlot.hnapp.mvp.base
 */
abstract class BaseListFragment : BaseFragment<BaseListPresenter>(), BaseListView {

    override fun initContentId(): Int {
        return R.layout.fragment_list
    }


}