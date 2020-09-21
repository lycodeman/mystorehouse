package com.example.mystorehouse.mvp.module

import com.example.common.mvp.module.BaseModule
import com.example.mystorehouse.api.MyStoreHouseApi
import javax.inject.Inject

/**
 *     Author : 李勇
 *     Create Time   : 2020/08/26
 *     Desc   :
 *     PackageName: com.example.mystorehouse.mvp.module
 */
open class BaseModuleWrapper : BaseModule() {

    @Inject
    lateinit var myStoreHouseApi: MyStoreHouseApi
}