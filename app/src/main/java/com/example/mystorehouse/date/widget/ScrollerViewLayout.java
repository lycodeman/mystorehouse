package com.example.mystorehouse.date.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.example.mystorehouse.R;

/**
 * Author : 李勇
 * Create Time   : 2020/10/25
 * Desc   :
 * PackageName: com.example.mystorehouse.date.widget
 */
public class ScrollerViewLayout extends NestedScrollView {

    private int startScrollHeight = 0;

    public void setStartScrollHeight(int height){
        startScrollHeight = height;
    }


    public ScrollerViewLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ScrollerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScrollerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(@NonNull Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_scroll_view,this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //先缩放，在进行滑动
        if (getTop() > 0){
            getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }else {
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
//        return super.onTouchEvent(ev);
    }
}
