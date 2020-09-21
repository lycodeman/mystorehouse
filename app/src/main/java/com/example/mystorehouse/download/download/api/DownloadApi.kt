package com.example.common.download.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 *     Author : 李勇
 *     Create Time   : 2020/09/17
 *     Desc   : 下载文件的
 *     PackageName: com.example.common.download.api
 */
interface DownloadApi {

    @Streaming
    @GET
    fun getFileContentLength(@Url url: String) : Observable<ResponseBody>

    @Streaming
    @GET
    fun getFileContentLength(@Url url: String, @FieldMap paramsMap: Map<String, String>): Observable<ResponseBody>

    @Streaming
    @GET
    fun downLoadFile(@Url url: String, @Header("RANGE") range: String?): Observable<ResponseBody>


    @GET
    fun getJson(@Url url: String): Observable<String>


    @GET
    fun getJson(@Url url: String, @QueryMap paramsMap: Map<String, String>): Observable<String>


    @POST
    fun postJson(@Url url: String): Observable<String>

    @FormUrlEncoded
    @POST
    fun postJson(@Url url: String, @FieldMap paramsMap: Map<String, String>): Observable<String>
}