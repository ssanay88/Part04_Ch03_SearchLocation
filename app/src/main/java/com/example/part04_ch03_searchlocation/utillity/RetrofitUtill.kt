package com.example.part04_ch03_searchlocation.utillity

import com.example.part04_ch03_searchlocation.BuildConfig
import com.example.part04_ch03_searchlocation.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

// API를 호출할 객체
object RetrofitUtill {

    val apiService: ApiService by lazy { getRetrofit().create(ApiService::class.java) }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Url.TMAP_URL)    // 베이스 URL
            .addConverterFactory(GsonConverterFactory.create())    // Json으로 받은 요청은 Gson으로 파싱
            .client(buildOkHttpClient())    // 클라이언트 연결
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()    // API를 호출할때마다 로그 표시
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)    // 5초 동안 API에 대한 응답이 없으면 에러 처리
            .addInterceptor(interceptor)
            .build()
    }
}