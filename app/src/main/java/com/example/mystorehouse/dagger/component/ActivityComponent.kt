package com.example.mystorehouse.dagger.component
import com.example.mystorehouse.*
import com.example.mystorehouse.mvp.activity.JokeListActivity
import com.example.mystorehouse.dagger.modlue.ActivityModule
import com.example.mystorehouse.dagger.scope.ActivityScope
import com.example.mystorehouse.download.DownLoadActivity
import com.example.mystorehouse.mvp.activity.WeatherActivity
import com.example.mystorehouse.screenadapter.ScreenInfoActivity
import com.example.mystorehouse.screenadapter.TodayHeadLineActivity
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.Component

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    var activity: RxAppCompatActivity

    fun inject(jokeListActivity: JokeListActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(weatherActivity: WeatherActivity)
    fun inject(screenAdapterActivity: ScreenAdapterActivity)
    fun inject(screenInfoActivity: ScreenInfoActivity)
    fun inject(todayHeadLineActivity: TodayHeadLineActivity)
    fun inject(downLoadActivity: DownLoadActivity)
    fun inject(datePickerActivity: DatePickerActivity)
    fun inject(datePickerActivity2: DatePickerActivity2)
    fun inject(datePickerActivity3: DatePickerActivity3)
    fun inject(datePickerActivity4: DatePickerActivity4)
    fun inject(nestActivity: NestActivity)
    fun inject(nestActivity2: NestActivity2)
}