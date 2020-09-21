package com.example.common.mvp.module;


import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle4.components.support.RxDialogFragment;
import com.trello.rxlifecycle4.components.support.RxFragment;

/**
 * Created by codeest on 2016/8/2.
 * Presenter基类
 */
public abstract class BaseModule{

    protected RxFragment mRxFragment;
    protected RxDialogFragment mRxDialogFragment;
    protected RxAppCompatActivity mAppCompatActivity;

    public void bindRxAppCompatActivity(RxAppCompatActivity appCompatActivity){
        this.mAppCompatActivity = appCompatActivity;
    }
    public void bindRxDialogFragment(RxFragment rxFragment){
        this.mRxFragment = rxFragment;
    }
    public void bindRxDialogFragment(RxDialogFragment rxDialogFragment){
        this.mRxDialogFragment = rxDialogFragment;
    }
}
