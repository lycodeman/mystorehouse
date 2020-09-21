package com.example.mystorehouse.mvp.module

import com.example.common.mvp.utils.RxUtils
import com.example.mystorehouse.data.CommonResult
import com.example.mystorehouse.data.DataWrapper
import com.example.mystorehouse.data.JokeResult
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/25
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp.module
 */
class JokeModule @Inject constructor(): BaseModuleWrapper() {



    fun getJokeData(page: Int): Observable<CommonResult<DataWrapper<JokeResult>>> {
        return myStoreHouseApi.getJokeData(
            "desc",
            page.toString(),
            "20",
            (System.currentTimeMillis()/1000).toString()
        ).compose(RxUtils.defaultTransformer(mAppCompatActivity))
    }
}