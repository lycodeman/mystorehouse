package com.example.common.mvp.presenter

import com.example.common.mvp.module.BaseModule
import com.example.common.mvp.view.BaseView
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxDialogFragment
import com.trello.rxlifecycle4.components.support.RxFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

/**
 * Created by codeest on 2016/8/2.
 * 基于Rx的Presenter封装,控制订阅的生命周期
 */
open class RxPresenter<V : BaseView,M : BaseModule>(): BasePresenter {

    protected var mView: V? =null
    protected var appCompatActivity: RxAppCompatActivity? =null
    @Inject
    lateinit var mModule: M
    protected var mCompositeDisposable: CompositeDisposable? = null
    protected fun unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    protected fun addSubscribe(subscription: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(subscription)
    }

    protected fun <U> addRxBusSubscribe(
        eventType: Class<U>?,
        act: Consumer<U>?
    ) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        //        mCompositeDisposable.add(RxBus.getDefault().toDefaultFlowable(eventType, act));
    }

    /**
     * 完成view的绑定 与生命周期的注入
     */
    override fun <T> attachView(view: T) {

        if (view is RxFragment){
            this.appCompatActivity = view.activity as RxAppCompatActivity
            mModule.bindRxDialogFragment(view)
        }else if (view is RxDialogFragment){
            this.appCompatActivity = view.activity as RxAppCompatActivity
            mModule.bindRxDialogFragment(view)
        }else {
            this.appCompatActivity = view as RxAppCompatActivity
            mModule.bindRxAppCompatActivity(this.appCompatActivity)
        }

        mView = view as V
    }

    override val isActive: Boolean
        get() =  mView != null

    override fun detachView() {
        mView = null
        unSubscribe()
    }

    fun bindModule(bindModule: M) {
        mModule = bindModule
    }
}