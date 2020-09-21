package com.example.mystorehouse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.common.mvp.utils.ActivityManager
import com.example.mystorehouse.mvp.activity.JokeListActivity
import com.example.mystorehouse.mvp.activity.WeatherActivity
import com.example.mystorehouse.screenadapter.ScreenInfoActivity
import com.example.mystorehouse.screenadapter.TodayHeadLineActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_screen_adapter.*
import kotlinx.android.synthetic.main.item_function.view.*
import mActivityComponent
import org.androidannotations.annotations.EActivity
import java.util.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/27
 *     Desc   :
 *     PackageName: com.example.mystorehouse
 */

class ScreenAdapterActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_adapter)
        initView()

    }

    fun initView() {
        supportActionBar
        bt_info_one.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","one"))
        }
        bt_info_two.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","two"))
        }
        bt_info_three.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","three"))
        }
        bt_info_four.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","four"))
        }
        bt_info_five.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","five"))
        }
        bt_info_sixe.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","sixe"))
        }
        bt_info_seven.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","seven"))
        }
        bt_info_8.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","8"))
        }
        bt_info_9.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","9"))
        }
        bt_info_10.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,TodayHeadLineActivity::class.java))
        }
        bt_info_11.setOnClickListener {
            ActivityUtils.startActivity(
                Intent(this,ScreenInfoActivity::class.java).putExtra("type","11"))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}