package com.example.mystorehouse.converterfactory

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset

/**
 * Author : 李勇
 * Create Time   : 2020/08/09
 * Desc   :
 * PackageName: com.genlot.hnapp.mvp.converterfactory
 */
class StringRequestBodyConverter internal constructor() :
    Converter<String?, RequestBody> {
    @Throws(IOException::class)
    override fun convert(value: String?): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(
            buffer.outputStream(),
            UTF_8
        )
        writer.write(value)
        writer.close()
        return RequestBody.create(
            MEDIA_TYPE,
            buffer.readByteString()
        )
    }

    companion object {
        private val MEDIA_TYPE =
            MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }
}