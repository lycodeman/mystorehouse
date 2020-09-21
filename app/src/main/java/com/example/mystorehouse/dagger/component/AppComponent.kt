package com.example.mystorehouse.dagger.component
import com.example.mystorehouse.api.CityApi
import com.example.mystorehouse.dagger.modlue.AppModule
import com.example.mystorehouse.api.MyStoreHouseApi
import com.example.mystorehouse.dagger.modlue.HttpModule
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [HttpModule::class, AppModule::class])
interface AppComponent {

    fun getMyStoreHouseApi(): MyStoreHouseApi

    fun getCityApi(): CityApi

    //    RetrofitHelper retrofitHelper();  //提供http的帮助类
    //    SPUtils spHelper();  //提供http的帮助类
    //    DataManager dataManager();  //提供http的帮助类,内含RetrofitHelper，是所有数据的入口
}