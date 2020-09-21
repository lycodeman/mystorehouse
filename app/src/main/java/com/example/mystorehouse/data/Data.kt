package com.example.mystorehouse.data
//数据类集合
data class CommonResult<T>(var reason: String = "",var result: T? = null,var error_code: String = "")

data class DataWrapper<T>(var data: MutableList<T> = mutableListOf())

data class JokeRequest(var sort: String = "desc",var page: String = "",var pagesize: String = "",var time: Long = 0)

data class JokeResult(var content: String = "",var hashId: String = "",var unixtime: Long = 0,var updatetime: String = "")


data class SK(var temp: String,var wind_direction: String,var wind_strength: String,var humidity: String,var time: String)
data class Today(var temperature: String,var weather: String,var weather_id: Any,var wind: String,var week: String,
                 var city: String,var date_y: String,var dressing_index: String,var dressing_advice: String,var uv_index: String,
                 var drying_index: String,var comfort_index: String,var wash_index: String,var travel_index: String,var exercise_index: String)
data class WeatherResult(var sk: SK, var today: Today, var future: Any)
