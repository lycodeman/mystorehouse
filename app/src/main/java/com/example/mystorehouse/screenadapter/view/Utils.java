package com.example.mystorehouse.screenadapter.view;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Author : 李勇
 * Create Time   : 2020/09/10
 * Desc   :
 * PackageName: com.example.mystorehouse.screenadapter.view
 */
public class Utils {

    private static Utils utils;

    //这里是设计稿参考宽高
    private static final float STANDARD_WIDTH = 720;
    private static final float STANDARD_HEIGHT = 1280;

    //这里是屏幕显示宽高
    private int mDisplayWidth;
    private int mDisplayHeight;

    private Utils(Context context){
        //获取屏幕的宽高
        if(mDisplayWidth == 0 || mDisplayHeight == 0){
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null){
                DisplayMetrics displayMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(displayMetrics);
                if (displayMetrics.widthPixels > displayMetrics.heightPixels){
                    //横屏
                    mDisplayWidth = displayMetrics.heightPixels;
                    mDisplayHeight = displayMetrics.widthPixels;
                }else{
                    mDisplayWidth = displayMetrics.widthPixels;
                    mDisplayHeight = displayMetrics.heightPixels - getStatusBarHeight(context);
                }
            }
        }

    }

    public int getStatusBarHeight(Context context){
        int resID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resID > 0){
            return context.getResources().getDimensionPixelSize(resID);
        }
        return 0;
    }

    public static Utils getInstance(Context context){
        if (utils == null){
            utils = new Utils(context.getApplicationContext());
        }
        return utils;
    }

    //获取水平方向的缩放比例
    public float getHorizontalScale(){
        return mDisplayWidth / STANDARD_WIDTH;
    }

    //获取垂直方向的缩放比例
    public float getVerticalScale(){
        return mDisplayHeight / STANDARD_HEIGHT;
    }


    /**
     * 今日头条适配方案 最终版 适配比例 以宽为纬度，360dp来适配
     */
    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    public static void setCustomDensity(@NonNull Activity activity, @NonNull final Application application) {
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }
                @Override
                public void onLowMemory() {
                }
            });
        }
        final float targetDensity = appDisplayMetrics.widthPixels / 320;
        final float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

}
