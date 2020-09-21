package com.example.mystorehouse.mvp.module

import com.example.common.mvp.utils.RxUtils
import com.example.mystorehouse.data.CommonResult
import com.example.mystorehouse.data.WeatherResult
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class WeatherModule @Inject constructor() : BaseModuleWrapper() {

    fun getWeatherData(cityname: String,dtype: String = "", format: String =""): Observable<CommonResult<WeatherResult>>? {
        return myStoreHouseApi.getWeatherData(cityname, dtype, format).compose(RxUtils.defaultTransformer(mRxDialogFragment))
    }
}
