package com.example.mystorehouse.dagger.modlue

import com.example.mystorehouse.dagger.scope.FragmentScope
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxDialogFragment
import com.trello.rxlifecycle4.components.support.RxFragment
import dagger.Module
import dagger.Provides

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
@Module
class FragmentModule{

    private var fragment: RxFragment? = null
    private var rxDialogFragment: RxDialogFragment? = null

    constructor(fragment: RxFragment) {
        this.fragment = fragment
    }

    constructor(rxDialogFragment: RxDialogFragment) {
        this.rxDialogFragment = rxDialogFragment
    }

    @Provides
    @FragmentScope
    fun provideActivity(): RxAppCompatActivity {
        return if (rxDialogFragment != null) {
            rxDialogFragment!!.activity as RxAppCompatActivity
        } else fragment!!.activity as RxAppCompatActivity
    }
}