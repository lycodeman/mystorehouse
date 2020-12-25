package com.example.mystorehouse.date.nest

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import com.example.mystorehouse.date.nest.NestChild


/**
 * Author : 李勇
 * Create Time   : 2020/12/25
 * Desc   :
 * PackageName: com.example.mystorehouse.date.nest
 */
class NestChild @JvmOverloads constructor(
    context: Context?,
    @Nullable attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), NestedScrollingChild {
    private val mConsumed: IntArray = IntArray(2)
    private val mOffset: IntArray = IntArray(2)
    private var mOldY: Int = 0
    private val mChildHelper: NestedScrollingChildHelper
    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper.hasNestedScrollingParent()
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        Log.i(TAG, "setNestedScrollingEnabled")
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        Log.i(TAG, "startNestedScroll")
        return mChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        Log.i(TAG, "stopNestedScroll")
        mChildHelper.stopNestedScroll()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        Log.i(TAG, "dispatchNestedScroll")
        // 滚动之后将剩余滑动传给父类
        return mChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed, offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        // 子View滚动之前将滑动距离传给父类
        Log.i(TAG, "dispatchNestedPreScroll")
        return mChildHelper.dispatchNestedPreScroll(
            dx, dy,
            consumed, offsetInWindow
        )
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(
            velocityX, velocityY,
            consumed
        )
    }

    override fun dispatchNestedPreFling(
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    companion object {
        private val TAG = NestChild::class.java.simpleName
    }

    init {
        // 生成辅助类，并传入当前控件
        mChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 启动滑动，传入方向
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                // 记录y值
                mOldY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.rawY.toInt()
                // 计算y值的偏移
                var offsetY: Int = y - mOldY
                Log.i(
                    TAG,
                    mConsumed.get(0)
                        .toString() + ":" + mConsumed.get(1) + "--" + mOffset.get(0) + ":" + mOffset.get(
                        1
                    )
                )
                // 通知父类，如果返回true，表示父类消耗了触摸
                if (dispatchNestedPreScroll(0, offsetY, mConsumed, mOffset)) {
                    offsetY -= mConsumed.get(1)
                }
                var unConsumed = 0
                val targetY = translationY + offsetY
                if (targetY > -40 && targetY < 40) {
                    translationY = targetY
                } else {
                    unConsumed = offsetY
                    offsetY = 0
                }
                // 滚动完成之后，通知当前滑动的状态
                dispatchNestedScroll(0, offsetY, 0, unConsumed, mOffset)
                Log.i(
                    TAG,
                    mConsumed.get(0)
                        .toString() + ":" + mConsumed.get(1) + "--" + mOffset.get(0) + ":" + mOffset.get(
                        1
                    )
                )
                mOldY = y
            }
            MotionEvent.ACTION_UP ->                 // 滑动结束
                stopNestedScroll()
            else -> {
            }
        }
        return true
    }
}