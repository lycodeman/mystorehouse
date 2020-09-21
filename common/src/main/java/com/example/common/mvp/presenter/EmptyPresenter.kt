package com.example.common.mvp.presenter

import com.example.common.mvp.module.EmptyModule
import com.example.common.mvp.view.EmptyView
import javax.inject.Inject

class EmptyPresenter @Inject constructor(): RxPresenter<EmptyView, EmptyModule>(){

}
