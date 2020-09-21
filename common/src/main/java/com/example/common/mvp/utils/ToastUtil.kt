package com.example.common.mvp.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.common.R

/**
 * Created by codeest on 2016/8/4.
 */
class ToastUtil private constructor(var context: Context) {
    private var tvToastMsgShort: TextView? = null
    private var tvToastMsgLong: TextView? = null
    var toastLong: Toast? = null
    var toastShort: Toast? = null
    fun createLong(): ToastUtil {
        if (toastLong == null) {
            val contentView = View.inflate(context, R.layout.dialog_toast, null)
            tvToastMsgLong = contentView.findViewById<View>(R.id.tv_toast_msg) as TextView
            toastLong = Toast(context)
            toastLong!!.view = contentView
            toastLong!!.setGravity(Gravity.CENTER, 0, 0)
            toastLong!!.duration = Toast.LENGTH_LONG
        }
        return this
    }

    fun createShort(): ToastUtil {
        if (toastShort == null) {
            val contentView = View.inflate(context, R.layout.dialog_toast, null)
            tvToastMsgShort = contentView.findViewById<View>(R.id.tv_toast_msg) as TextView
            toastShort = Toast(context)
            toastShort!!.view = contentView
            toastShort!!.setGravity(Gravity.CENTER, 0, 0)
            toastShort!!.duration = Toast.LENGTH_SHORT
        }
        return this
    }

    fun showShort(msg: String) {
        if (toastShort != null) {
            tvToastMsgShort?.text = msg
            toastShort!!.show()
        }
    }

    fun showLong(msg: String) {
        if (toastLong != null) {
            tvToastMsgLong?.text = msg
            toastLong!!.show()
        }
    }

    companion object {
        var toastUtil: ToastUtil? = null

        fun showLong(msg: String) {
            if (toastUtil == null) {
                toastUtil = ToastUtil(ActivityManager.appContext())
                toastUtil!!.createLong()
            }
            toastUtil!!.showLong(msg)
        }

        fun showShort(msg: String) {
            if (toastUtil == null) {
                toastUtil = ToastUtil(ActivityManager.appContext())
                toastUtil!!.createShort()
            }
            toastUtil!!.showShort(msg)
        }
    }

}