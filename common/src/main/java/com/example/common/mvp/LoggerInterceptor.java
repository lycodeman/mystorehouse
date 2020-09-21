package com.example.common.mvp;

import android.text.TextUtils;


import com.example.common.mvp.utils.LoggerUtils;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 日志打印拦截器
 * Created by zhy on 16/3/1.
 */
public class LoggerInterceptor implements Interceptor {

    public static final String TAG = "llll_";
    private String tag;
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            LoggerUtils.e(response.toString());
            LoggerUtils.e("========response'log=======");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            LoggerUtils.e("code : " + clone.code());
            if (!TextUtils.isEmpty(clone.message()))
                LoggerUtils.e("message : " + clone.message());
            if (showResponse) {
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        LoggerUtils.e("responseBody's contentType : " + mediaType.toString());
                        if (isText(mediaType)) {
                            String resp = body.string();
                            LoggerUtils.e("responseBody's content : " + resp);
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else {
                            LoggerUtils.e("responseBody's content : maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }
            LoggerUtils.e("========response'log=======end");
        } catch (Exception e) {
            LoggerUtils.e(e.getMessage());

        }finally {
            LoggerUtils.e("========response'log=======end");
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            LoggerUtils.e("________request'log________");
            LoggerUtils.e("method : " + request.method());
            LoggerUtils.e("url : " + url);
            if (headers != null && headers.size() > 0) {
                LoggerUtils.e("headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    LoggerUtils.e("requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        LoggerUtils.e("requestBody's content : " + bodyToString(request));
                    } else {
                        LoggerUtils.e("requestBody's content : maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            LoggerUtils.e("________request'log________end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")||
                    mediaType.subtype().equals("x-www-form-urlencoded")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
