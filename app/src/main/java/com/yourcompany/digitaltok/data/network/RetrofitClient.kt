package com.yourcompany.digitaltok.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://3.37.213.174:8080/api/v1/"

    private const val TEMP_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0NTY3OEB0ZXN0LmNvbSIsInVzZXJJZCI6OSwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc3MDc0MjM3NSwiZXhwIjoxNzcwNzQ0MTc1fQ.FEwkqLA4U6Ut_MZojYRT_CqF_S5f_DHaifhFXfnCxnU0zseEQPwmJ63GAGNG7U2i"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 인증이 필요한 요청을 위한 OkHttpClient (기존 로직)
    private val authenticatedOkHttpClient: OkHttpClient by lazy {
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = "Bearer $TEMP_TOKEN"
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
            chain.proceed(newRequest)
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // 인증이 필요 없는(공개) 요청을 위한 OkHttpClient (인증 인터셉터 없음)
    private val publicOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // 인증이 필요한 요청을 위한 Retrofit 인스턴스
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 인증이 필요 없는(공개) 요청을 위한 Retrofit 인스턴스
    private val publicRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 기본 URL은 여전히 필요함
            .client(publicOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 기본 ApiService는 인증이 필요한 요청을 처리
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // 공개 ApiService는 인증이 필요 없는 요청을 처리
    val publicApiService: ApiService by lazy {
        publicRetrofit.create(ApiService::class.java)
    }

    // 기존 create 함수는 유지 (인증된 서비스 생성)
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
