package com.example.mystorehouse.api

import com.example.mystorehouse.Constants
import com.example.mystorehouse.data.CommonResult
import com.example.mystorehouse.data.DataWrapper
import com.example.mystorehouse.data.JokeResult
import com.example.mystorehouse.data.WeatherResult
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.api
 */
interface MyStoreHouseApi {

//    val key = "ed17ce27b0f288828838d0e2446d807e"

    //http://v.juhe.cn/joke/content/list.php
    @GET("/joke/content/list.php")
    //sort=desc&page=&pagesize=&time=1598421310&key=01520e99c0c5efc731cfdd608188cc60
    fun getJokeData(@Query("sort") sort: String,@Query("page") page: String,@Query("pagesize") pagesize: String,
                    @Query("time") time: String,@Query("key") key: String = Constants.JOKE_KEY):
            Observable<CommonResult<DataWrapper<JokeResult>>>


    @GET("/weather/index")
    fun getWeatherData(@Query("cityname") cityname: String,@Query("dtype") dtype: String,
                       @Query("format") format: String,@Query("key") key: String = Constants.WEATHER_KEY):
            Observable<CommonResult<WeatherResult>>

}