@file:JvmName("CodeUtil")
@file:JvmMultifileClass

package com.example.mystorehouse

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.facebook.stetho.common.LogUtil

fun getStringArrayFromRecource(@ArrayRes arrayId: Int): Array<String> {
    return MyApplication.getInstance().resources.getStringArray(arrayId)
}

fun getStringFromResource(@StringRes stringId: Int): String {
    return MyApplication.getInstance().getString(stringId)
}

fun getStringFromResource(@StringRes stringId: Int, vararg formatArgs: Any?): String? {
    return MyApplication.getInstance().getString(stringId, *formatArgs)
}

fun getColorFromResource(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(MyApplication.getInstance(), colorRes)
}

fun getDimensionFromResource(@DimenRes dimenRes: Int): Int {
    return MyApplication.getInstance().resources
        .getDimensionPixelSize(dimenRes)
}

fun getColorDrawable(@ColorRes colorRes: Int): ColorDrawable? {
    return ColorDrawable(
        ContextCompat.getColor(
            MyApplication.getInstance(),
            colorRes
        )
    )
}

fun getParcelable(intent: Intent?, key: String?): Parcelable? {
    try {
        if (intent != null && intent.extras != null && intent.extras!!.containsKey(key)
            && intent.extras!![key] != null && intent.extras!![key] is Parcelable
        ) {
            return intent.extras!!.getParcelable(key)
        }
    } catch (e: Exception) {
        LogUtil.e("Code Util getParcelable :", e)
    }
    return null
}

/**
 * Get activity from context object
 *
 * @param context something
 * @return object of Activity or null if it is not Activity
 */
fun scanForActivity(context: Context?): Activity? {
    if (context == null) {
        return null
    }
    if (context is Activity) {
        return context
    } else if (context is ContextWrapper) {
        return scanForActivity(context.baseContext)
    }
    return null
}

