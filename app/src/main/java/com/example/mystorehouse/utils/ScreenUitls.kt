package com.example.mystorehouse.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/09
 *     Desc   :
 *     PackageName: com.example.mystorehouse.utils
 */
object ScreenUitls {

    /**
     * 获取虚拟按键的高度
     *      1. 全面屏下
     *          1.1 开启全面屏开关-返回0
     *          1.2 关闭全面屏开关-执行非全面屏下处理方式
     *      2. 非全面屏下
     *          2.1 没有虚拟键-返回0
     *          2.1 虚拟键隐藏-返回0
     *          2.2 虚拟键存在且未隐藏-返回虚拟键实际高度
     */
    fun getNavigationBarHeightIfRoom(context: Context): Int {
        if(navigationGestureEnabled(context)){
            return 0;
        }
        return getCurrentNavigationBarHeight(context as Activity);
    }

    /**
     * 全面屏（是否开启全面屏开关 0 关闭  1 开启）
     *
     * @param context
     * @return
     */
    fun navigationGestureEnabled(context: Context) : Boolean{
        return Settings.Global.getInt(context.getContentResolver(), getDeviceInfo(), 0)!=0
    }

    /**
     * 获取设备信息（目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo都可以）
     *
     * @return
     */
    fun getDeviceInfo() : String {
        var brand: java.lang.String =java.lang.String(Build.BRAND)
        if(TextUtils.isEmpty(brand)) return "navigationbar_is_min"

        if (brand.equalsIgnoreCase("HUAWEI")) {
            return "navigationbar_is_min"
        } else if (brand.equalsIgnoreCase("XIAOMI")) {
            return "force_fsg_nav_bar"
        } else if (brand.equalsIgnoreCase("VIVO")) {
            return "navigation_gesture_on"
        } else if (brand.equalsIgnoreCase("OPPO")) {
            return "navigation_gesture_on"
        } else {
            return "navigationbar_is_min"
        }
    }

    /**
     * 非全面屏下 虚拟键实际高度(隐藏后高度为0)
     * @param activity
     * @return
     */
    fun getCurrentNavigationBarHeight(activity: Activity) : Int{
        if(isNavigationBarShown(activity)){
            return getNavigationBarHeight(activity);
        } else{
            return 0;
        }
    }

    /**
     * 非全面屏下 虚拟按键是否打开
     * @param activity
     * @return
     */
    fun isNavigationBarShown( activity: Activity) : Boolean{
        //虚拟键的view,为空或者不可见时是隐藏状态
        var view  = activity.findViewById<View>(android.R.id.navigationBarBackground);
        if(view == null){
            return false;
        }
        var visible = view.getVisibility();
        return !(visible == View.GONE || visible == View.INVISIBLE)
    }

    /**
     * 非全面屏下 虚拟键高度(无论是否隐藏)
     * @param context
     * @return
     */
    fun getNavigationBarHeight(context:Context): Int{
        var result = 0;
        var resourceId = context.getResources().getIdentifier("navigation_bar_height","dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}