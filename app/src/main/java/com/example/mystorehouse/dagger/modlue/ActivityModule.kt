package com.example.mystorehouse.dagger.modlue

import com.example.mystorehouse.dagger.scope.ActivityScope
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.Module
import dagger.Provides

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
@Module
class ActivityModule(private val mActivity: RxAppCompatActivity) {

    @Provides
    @ActivityScope
    fun provideActivity(): RxAppCompatActivity {
        return mActivity
    }

}