package com.example.common.mvp

import android.os.Environment
import com.example.common.mvp.utils.ActivityManager
import java.io.File

/**
 * Created by CodeManLY on 2017/7/20 0020.
 */
object Constants {
    //================= PATH ====================
    val PATH_DATA =
        ActivityManager.appContext().cacheDir
            .absolutePath + File.separator + "data"
    val PATH_CACHE = "$PATH_DATA/NetCache"
    val PATH_SDCARD = Environment.getExternalStorageDirectory()
        .absolutePath + File.separator + "codeest" + File.separator + "GeekNews"


    //    *    天气网把城市分为了3级
    //    *    1级列表获取地址：http://www.weather.com.cn/data/city3jdata/china.html。通过访问这个地址，天气
    //    *    网会返回一级省（自治区）的名称、ID信息；
    //    *    2级城市城市列表获取地址：http://www.weather.com.cn/data/city3jdata/provshi/10120.html。其中“10120”
    //    *    为一级城市的ID，返回结果是归属于该城市的2级省市的名称、ID；
    //    *    3级城市列表获取地址：http://www.weather.com.cn/data/city3jdata/station/1012002.html。其中“1012002”
    //    *    为2级省市ID，返回结果就是3级城市的名称和ID了。
    //    *    获取到3级城市的名称和ID之后，就可以根据上面那篇博客里的内容获取当地的天气信息了！

    const val PROVINCE_URL= "http://www.weather.com.cn"
}