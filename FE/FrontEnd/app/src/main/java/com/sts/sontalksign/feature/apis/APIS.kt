package com.sts.sontalksign.feature.apis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface APIS {

    /*
    * @POST("api/~~")
    * fun [함수명](
    *   @Body   [보낼꺼],
    * ): Call<[반환]>
    *
    *함수명 규칙은 카멜표기법 (signUp)
    *변수명 규칙은 카멜표기법 (nickName)
    *
    * */


    companion object {
        private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com/"    // 주소 --> 없으면 빼도 됨

        fun create(): APIS {
            val gson : Gson = GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(APIS::class.java)
        }
    }
}