package com.example.mystorehouse

import android.app.Application
import com.blankj.utilcode.util.ActivityUtils
import com.example.common.mvp.utils.ActivityManager
import com.facebook.stetho.Stetho
import com.tencent.mmkv.MMKV
import me.ele.uetool.UETool
import me.jessyan.autosize.AutoSizeConfig

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/26
 *     Desc   :
 *     PackageName: com.example.mystorehouse
 */
class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        instance = this
        ActivityManager.getInstance().init(this)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                WebView.setWebContentsDebuggingEnabled(true)
//            }
        }
        if (BuildConfig.DEBUG) {
//            UETool.showUETMenu()
        }
        AutoSizeConfig.getInstance().setCustomFragment(true)
        MMKV.initialize(this)
    }
     companion object{
         private lateinit var instance: MyApplication

         fun getInstance() = instance

     }

}