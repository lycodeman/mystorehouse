package com.example.common.download

import com.example.common.download.api.DownloadApi
import com.example.common.mvp.LoggerInterceptor
import com.example.common.mvp.utils.ActivityManager
import com.example.common.mvp.utils.NetWorkUtils
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 请求管理
 *     PackageName: com.example.common.download
 */
object RequestManager {

    fun getDownloadApi(): DownloadApi {
        return createRetrofit(Constants.BASE_URL).create(DownloadApi::class.java)
    }

    fun provideClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = LoggerInterceptor("==DEBUG  TEST==", true)
        builder.addInterceptor(loggingInterceptor)
        val pathname = ActivityManager.appContext().cacheDir
            .absolutePath + File.separator + "data/NetCache"
        val cacheFile = File(pathname)
        val cache = Cache(cacheFile, 1024 * 1024 * 50)
        val cacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!NetWorkUtils.isNetworkConnected(ActivityManager.appContext())) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            val response = chain.proceed(request)
            if (NetWorkUtils.isNetworkConnected(ActivityManager.appContext())) {
                val maxAge = 0
                // 有网络时, 不缓存, 最大保存时长为0
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader("Pragma")
                    .build()
            } else {
                // 无网络时，设置超时为4周
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()
            }
            response
        }
        //设置缓存
        builder.addNetworkInterceptor(cacheInterceptor)
        builder.addNetworkInterceptor(ChuckInterceptor(ActivityManager.appContext()))
        builder.addInterceptor(cacheInterceptor)
        builder.cache(cache)
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        return builder.build()
    }

    fun createRetrofit(url: String): Retrofit {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create()
        return Retrofit.Builder()
            .baseUrl(url)
            .client(provideClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}