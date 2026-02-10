package com.yourcompany.digitaltok.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://3.37.213.174:8080/api/v1/"

    // (임시) 스웨거에서 발급받은 토큰
    private const val TEMP_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0NTY3OEB0ZXN0LmNvbSIsInVzZXJJZCI6OSwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc3MDcxMDk3OCwiZXhwIjoxNzcwNzEyNzc4fQ.nxlnBF7s5RZ8VltjlOFugi3pu1qysEni8OuRs-q29rOFFRer_wDyFFXIYc2dWGRw"

    // 모든 요청에 자동으로 토큰을 추가하는 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = "Bearer $TEMP_TOKEN" // "Bearer " 접두사 추가
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", token)
            .build()
        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp 클라이언트에 AuthInterceptor 추가
    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // 인증 인터셉터 추가
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
