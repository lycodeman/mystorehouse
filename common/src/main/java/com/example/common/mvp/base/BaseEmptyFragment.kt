package com.example.common.mvp.base

import com.example.common.mvp.presenter.EmptyPresenter
import com.example.common.mvp.view.EmptyView


/**
 *     Author : 李勇
 *     Create Time   : 2020/08/06
 *     Desc   :
 *     PackageName: com.genlot.hnapp.mvp.base
 */
abstract class BaseEmptyFragment : BaseFragment<EmptyPresenter>(), EmptyView {
}