package com.example.mystorehouse.dagger.modlue

import com.example.common.mvp.LoggerInterceptor
import com.example.common.mvp.utils.ActivityManager
import com.example.common.mvp.utils.NetWorkUtils
import com.example.mystorehouse.Constants
import com.example.mystorehouse.api.CityApi
import com.example.mystorehouse.api.MyStoreHouseApi
import com.example.mystorehouse.converterfactory.StringConverterFactory
import com.example.mystorehouse.dagger.qualifier.CityApiUrl
import com.example.mystorehouse.dagger.qualifier.JokeUrl
import com.example.mystorehouse.interceptor.ErrorInterceptor
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by codeest on 2017/2/26.
 */
@Module
class HttpModule {
    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
    }

    @Singleton
    @Provides
    fun provideOkHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

    @Singleton
    @Provides
    @JokeUrl
    fun provideMyStoreHouseApi(builder: Retrofit.Builder, client: OkHttpClient): Retrofit {
        return createRetrofit(builder, client, Constants.BASE_URL)
    }

    @Singleton
    @Provides
    fun provideMystoreHouseApis(@JokeUrl retrofit: Retrofit): MyStoreHouseApi {
        return retrofit.create(MyStoreHouseApi::class.java)
    }

    @Singleton
    @Provides
    @CityApiUrl
    fun provideCityApiRetrofit(builder: Retrofit.Builder, client: OkHttpClient): Retrofit {
        return createRetrofit(builder, client, Constants.WEATHER_URL)
    }

    @Singleton
    @Provides
    fun provideCityApi(@CityApiUrl retrofit: Retrofit): CityApi {
        return retrofit.create(CityApi::class.java)
    }

    @Singleton
    @Provides
    fun provideClient(builder: OkHttpClient.Builder): OkHttpClient {
        val loggingInterceptor = LoggerInterceptor("==DEBUG  TEST==", true)
        builder.addInterceptor(loggingInterceptor)
        val cacheFile = File(Constants.PATH_CACHE)
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
        builder.addInterceptor(ErrorInterceptor())
        builder.cache(cache)
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        return builder.build()
    }

    private fun createRetrofit(
        builder: Retrofit.Builder,
        client: OkHttpClient,
        url: String
    ): Retrofit {
        val gson =
            GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create()
        return builder
            .baseUrl(url)
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(StringConverterFactory.create())
            .build()
    }
}