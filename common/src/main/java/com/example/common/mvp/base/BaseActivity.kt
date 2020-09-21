package com.example.common.mvp.base

import android.os.Bundle
import com.example.common.mvp.presenter.RxPresenter
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */

abstract class BaseActivity<P : RxPresenter<*,*>> : RxAppCompatActivity(){

    //presenter
    @Inject
    lateinit var mPresenter: P
    //rxjava 手动绑定解绑
    val mCompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initBefore()
        super.onCreate(savedInstanceState)
        setContentView(initContentId())
        mPresenter.attachView(initView())
        initListener()
        initData()
    }

    abstract fun initBefore()
    abstract fun initContentId(): Int
    abstract fun initView(): RxAppCompatActivity
    abstract fun initData()
    open fun initListener() {}
    open fun releaseResource() {}

    /**
     * 所有rx订阅后，需要调用此方法，用于在detachView时取消订阅
     */
    protected fun addDisposable(disposable: Disposable?) {
        mCompositeDisposable.add(disposable!!)
    }

    /**
     * 取消本页面所有订阅
     */
    protected fun onDisposable() {
        if (mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }

    override fun onDestroy() {
        releaseResource()
        mPresenter.detachView()
        super.onDestroy()
    }

}