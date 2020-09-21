package com.example.mystorehouse.api

import com.example.mystorehouse.*
import com.example.mystorehouse.data.CommonResult
import com.example.mystorehouse.data.DataWrapper
import com.example.mystorehouse.data.JokeResult
import com.example.mystorehouse.data.WeatherResult
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.api
 */
interface CityApi {

    @GET("/data/city3jdata/china.html")
    fun getProvinceList(): Observable<MutableMap<String, String>>

    @GET("/data/city3jdata/provshi/{provinceCode}.html")
    fun getCityList(@Path("provinceCode") provinceCode: String): Observable<MutableMap<String, String>>

    @GET("/data/city3jdata/station/{citycode}.html")
    fun getCountryList(@Path("citycode") citycode: String): Observable<MutableMap<String, String>>


}