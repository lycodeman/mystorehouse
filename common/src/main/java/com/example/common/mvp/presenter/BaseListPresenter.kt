package com.example.common.mvp.presenter

import com.example.common.mvp.module.BaseListModule
import com.example.common.mvp.view.BaseListView
import org.androidannotations.annotations.EActivity
import javax.inject.Inject
@EActivity
class BaseListPresenter @Inject constructor(): RxPresenter<BaseListView, BaseListModule>(){

}
