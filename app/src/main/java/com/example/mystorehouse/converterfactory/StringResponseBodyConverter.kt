package com.example.mystorehouse.converterfactory

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * Author : 李勇
 * Create Time   : 2020/08/09
 * Desc   :
 * PackageName: com.genlot.hnapp.mvp.converterfactory
 */
class StringResponseBodyConverter : Converter<ResponseBody, String> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): String {
        return try {
            value.string()
        } finally {
            value.close()
        }
    }
}