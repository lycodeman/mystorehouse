package com.example.mystorehouse.interceptor

import android.text.TextUtils
import com.example.common.mvp.utils.LoggerUtils
import com.example.common.mvp.utils.RxUtils
import com.example.common.mvp.utils.ToastUtil
import com.example.mystorehouse.data.CommonResult
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException


/**
 *     Author : 李勇
 *     Create Time   : 2020/08/20
 *     Desc   : Session失效拦截
 *     PackageName: com.genlot.hnapp.mvp.interceptor
 */
class ErrorInterceptor : Interceptor {

    private val UTF8 = Charset.forName("UTF-8")

    override fun intercept(chain: Interceptor.Chain): Response {
//        val request: Request = chain.request()
//
//        val proceed = chain.proceed(request)
        val request = chain.request()
        val response = chain.proceed(request)

        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()


        //注意 >>>>>>>>> okhttp3.4.1这里变成了 !HttpHeader.hasBody(response)
        //if (!HttpEngine.hasBody(response)) {
        if (!HttpHeaders.hasBody(response)) { //HttpHeader -> 改成了 HttpHeaders，看版本进行选择
            //END HTTP
        } else if (bodyEncoded(response.headers())) {
            //HTTP (encoded body omitted)
        } else {
            val source = responseBody!!.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer: Buffer = source.buffer()
            var charset: Charset = UTF8
            val contentType: MediaType? = responseBody!!.contentType()


            if (contentType != null) {
                charset = try {
                    contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    //Couldn't decode the response body; charset is likely malformed.
                    return response
                }!!
            }
            if (!isPlaintext(buffer)) {
                LoggerUtils.i("<-- END HTTP (binary " + buffer.size().toString() + "-byte body omitted)")
                return response
            }
            if (contentLength != 0L) {
                var result: String = buffer.clone().readString(charset)

                //获取到response的body的string字符串
                //统一处理后台返回的错误
                result?.run {
                    val commonResult = Gson().fromJson(result, CommonResult::class.java)
                    if (!TextUtils.equals(commonResult.error_code, "0")) {
                        if (!TextUtils.isEmpty(commonResult.reason)) {
                            Observable.create(ObservableOnSubscribe<Any>{ e ->
                                e.onNext("")
                            }).compose(RxUtils.defaultTransformer()).subscribe {
                                ToastUtil.Companion.showShort(commonResult.reason)
                            }
                        }
                    }
                }
            }
            LoggerUtils.i("<-- END HTTP (" + buffer.size().toString() + "-byte body)")
        }

        return response
    }

    @Throws(EOFException::class)
    fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }


    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding: String? = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }
}