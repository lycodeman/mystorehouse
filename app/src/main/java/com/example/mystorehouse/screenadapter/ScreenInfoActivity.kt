package com.example.mystorehouse.screenadapter

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.common.mvp.base.BaseEmptyActivity
import com.example.mystorehouse.R
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_screen_info.*
import mActivityComponent

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/09
 *     Desc   :
 *     PackageName: com.example.mystorehouse.screenadapter
 */
class ScreenInfoActivity : BaseEmptyActivity() {
    override fun initBefore() {
        mActivityComponent.inject(this)
    }

    override fun initContentId(): Int {
        return R.layout.activity_screen_info
    }

    override fun initView(): RxAppCompatActivity {
        supportActionBar?.setTitle(
            "屏幕适配"
        )
        if (intent.getStringExtra("type") == "one") {
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoFragment
            navHostFragment.navController.graph = navSimple
        } else if (intent.getStringExtra("type") == "two") {
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoTwoFragment
            navHostFragment.navController.graph = navSimple

        } else if (intent.getStringExtra("type") == "three") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoThreeFragment
            navHostFragment.navController.graph = navSimple
        } else if (intent.getStringExtra("type") == "four") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoFourFragment
            navHostFragment.navController.graph = navSimple
        } else if (intent.getStringExtra("type") == "five") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoFiveFragment
            navHostFragment.navController.graph = navSimple
        } else if (intent.getStringExtra("type") == "sixe") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoSixeFragment
            navHostFragment.navController.graph = navSimple
        }else if (intent.getStringExtra("type") == "seven") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfoSevenFragment
            navHostFragment.navController.graph = navSimple
        }else if (intent.getStringExtra("type") == "8") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfo8Fragment
            navHostFragment.navController.graph = navSimple
        }else if (intent.getStringExtra("type") == "9") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfo9Fragment
            navHostFragment.navController.graph = navSimple
        }else if (intent.getStringExtra("type") == "11") {
            supportActionBar?.hide()
            var navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            var navSimple: NavGraph =
                navHostFragment.navController.navInflater.inflate(R.navigation.screen_adapter_nav)
            navSimple.startDestination = R.id.screenInfo10Fragment
            navHostFragment.navController.graph = navSimple
        }
        return this
    }

    override fun initData() {

    }


}