package com.example.mystorehouse.dagger.component

import com.example.mystorehouse.dagger.modlue.FragmentModule
import com.example.mystorehouse.dagger.scope.FragmentScope
import com.example.mystorehouse.mvp.fragment.WeatherDetailFragment
import com.example.mystorehouse.mvp.fragment.WeatherFragment
import com.example.mystorehouse.screenadapter.*
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.Component

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {
    fun injectFragment(weatherFragment: WeatherFragment)
    fun injectFragment(weatherDetailFragment: WeatherDetailFragment)
    fun injectFragment(screenInfoFragment: ScreenInfoFragment)
    fun injectFragment(screenInfoTwoFragment: ScreenInfoTwoFragment)
    fun injectFragment(screenInfoThreeFragment: ScreenInfoThreeFragment)
    fun injectFragment(screenInfoFourFragment: ScreenInfoFourFragment)
    fun injectFragment(screenInfoFiveFragment: ScreenInfoFiveFragment)
    fun injectFragment(screenInfoSixeFragment: ScreenInfoSixFragment)
    fun injectFragment(screenInfoSevenFragment: ScreenInfoSevenFragment)
    fun injectFragment(screenInfo8Fragment: ScreenInfo8Fragment)
    fun injectFragment(screenInfo9Fragment: ScreenInfo9Fragment)
    fun injectFragment(screenInfo10Fragment: ScreenInfo10Fragment)

    //    void inject(JokeContentFragment contentFragment);

    val activity: RxAppCompatActivity?
    //    void inject(JokeRandomFragment randomFragment);
    //    void inject(JokeImgFragment imgFragment);
    //
    //    void inject(GilsItemFragment gilsItemFragment);
    //    void inject(GirlsDetailFragment girlsDetailFragment);
    //
    //    void inject(NewsDTFragment newsFragment);
}