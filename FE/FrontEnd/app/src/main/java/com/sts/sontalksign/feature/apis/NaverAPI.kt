package com.sts.sontalksign.feature.apis

import com.google.firebase.encoders.annotations.Encodable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface NaverAPI {
    //  네이버 음성합성 Open API
    @Streaming
    @FormUrlEncoded
    @POST("tts-premium/v1/tts")
    fun generateSpeech(
        @Field("speaker") speaker: String,
        @Field("volume") volume: Int,
        @Field("speed") speed: Int,
        @Field("pitch") pitch: Int,
        @Field("format") format: String,
        @Field("text") text: String
    ): Call<ResponseBody>

    companion object {
        private const val BASE_URL_NAVER_API = "https://naveropenapi.apigw.ntruss.com/"
        private const val CLIENT_ID : String = "89kna7451i"
        private const val CLIENT_SECRET : String = "es3gL95EKgmmcmLSgeVDyKzZ9dHBknHNT7htAG1h"
        private const val CONTENT_TYPE : String = "application/x-www-form-urlencoded"

        fun create(): NaverAPI {
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("X-NCP-APIGW-API-KEY-ID", CLIENT_ID)
                    .addHeader("X-NCP-APIGW-API-KEY", CLIENT_SECRET)
                    .addHeader("Content-Type", CONTENT_TYPE)
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL_NAVER_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NaverAPI::class.java)
        }
    }
}