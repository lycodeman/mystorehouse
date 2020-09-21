package com.example.mystorehouse.screenadapter.view

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout


/**
 *     Author : 李勇
 *     Create Time   : 2020/09/10
 *     Desc   :
 *     PackageName: com.example.mystorehouse.screenadapter.view
 */
class SAFrameLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var flag: Boolean = false

    constructor( context: Context): this(context,null,0)
    constructor( context: Context, attrs: AttributeSet?): this(context,attrs,0)

    @Override
    override fun onMeasure(widthMeasureSpec:Int, heightMeasureSpec: Int) {
        if (!flag && !isInEditMode){
            var scaleX = Utils.getInstance(getContext()).getHorizontalScale();
            var scaleY = Utils.getInstance(getContext()).getVerticalScale();

            var count = getChildCount()
            for (i in 0..count) {
                var child: View? = getChildAt(i)
                child?.run {
                    val params = this.getLayoutParams() as LayoutParams
                    params.width = (params.width * scaleX).toInt()
                    params.height = (params.height * scaleY).toInt()
                    params.leftMargin = (params.leftMargin * scaleX).toInt()
                    params.rightMargin = (params.rightMargin * scaleX).toInt()
                    params.topMargin = (params.topMargin * scaleY).toInt()
                    params.bottomMargin = (params.bottomMargin * scaleY).toInt()
                    child.setPadding(child.paddingLeft*scaleX.toInt(),child.paddingTop*scaleY.toInt(),
                        child.paddingRight*scaleX.toInt(),child.paddingBottom*scaleY.toInt())
                }
            }
            flag = true
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}