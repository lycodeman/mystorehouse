package com.example.mystorehouse.date.newwidget;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.jar.Attributes;

/**
 * Author : 李勇
 * Create Time   : 2020/12/24
 * Desc   :
 * PackageName: com.example.mystorehouse.date.newwidget
 */
public class MyRecycleview extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public MyRecycleview(Context context) {
        super(context);
    }

    public MyRecycleview(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }


}